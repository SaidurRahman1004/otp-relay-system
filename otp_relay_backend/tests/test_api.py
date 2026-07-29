from django.urls import reverse
from rest_framework import status
from rest_framework.test import APITestCase

from apps.accounts.models import User
from apps.otp_messages.models import OtpMessage


class OtpRelayApiTests(APITestCase):
    def register_user(self, email="user@example.com", password="StrongPass123!"):
        response = self.client.post(
            reverse("auth-register"),
            {"email": email, "password": password},
            format="json",
        )
        self.assertEqual(response.status_code, status.HTTP_201_CREATED, response.data)
        return response.data["data"]["user"], response.data["data"]["tokens"]

    def register_device(self, access_token, device_identifier="android-001"):
        self.client.credentials(HTTP_AUTHORIZATION=f"Bearer {access_token}")
        response = self.client.post(
            reverse("device-register"),
            {"device_name": "Pixel Test", "device_identifier": device_identifier},
            format="json",
        )
        self.assertEqual(response.status_code, status.HTTP_201_CREATED, response.data)
        self.client.credentials()
        return response.data["data"]["device"], response.data["data"]["device_token"]

    def upload_otp(self, device_token, client_message_id="sms-001", otp_value="123456"):
        return self.client.post(
            reverse("otp-list-create"),
            {
                "client_message_id": client_message_id,
                "sender": "+15551234567",
                "otp_value": otp_value,
                "received_at": "2026-07-29T12:00:00Z",
            },
            format="json",
            HTTP_X_DEVICE_TOKEN=device_token,
        )

    def test_health_endpoint(self):
        response = self.client.get(reverse("health"))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertTrue(response.data["success"])
        self.assertEqual(response.data["data"]["status"], "ok")

    def test_user_registration(self):
        user, tokens = self.register_user()
        self.assertIn("id", user)
        self.assertEqual(user["email"], "user@example.com")
        self.assertIn("access", tokens)
        self.assertIn("refresh", tokens)

    def test_login(self):
        self.register_user()
        response = self.client.post(
            reverse("auth-login"),
            {"email": "user@example.com", "password": "StrongPass123!"},
            format="json",
        )
        self.assertEqual(response.status_code, status.HTTP_200_OK, response.data)
        self.assertTrue(response.data["success"])
        self.assertIn("access", response.data["data"]["tokens"])

    def test_jwt_refresh(self):
        _, tokens = self.register_user()
        response = self.client.post(reverse("token-refresh"), {"refresh": tokens["refresh"]}, format="json")
        self.assertEqual(response.status_code, status.HTTP_200_OK, response.data)
        self.assertIn("access", response.data["data"])

    def test_device_registration(self):
        _, tokens = self.register_user()
        device, device_token = self.register_device(tokens["access"])
        self.assertEqual(device["device_identifier"], "android-001")
        self.assertTrue(device_token)
        self.assertNotEqual(device_token, tokens["access"])

    def test_otp_upload(self):
        _, tokens = self.register_user()
        _, device_token = self.register_device(tokens["access"])
        response = self.upload_otp(device_token)
        self.assertEqual(response.status_code, status.HTTP_201_CREATED, response.data)
        self.assertTrue(response.data["data"]["created"])
        self.assertEqual(OtpMessage.objects.count(), 1)

    def test_duplicate_otp_upload(self):
        _, tokens = self.register_user()
        _, device_token = self.register_device(tokens["access"])
        first = self.upload_otp(device_token)
        second = self.upload_otp(device_token)
        self.assertEqual(first.status_code, status.HTTP_201_CREATED, first.data)
        self.assertEqual(second.status_code, status.HTTP_200_OK, second.data)
        self.assertFalse(second.data["data"]["created"])
        self.assertEqual(OtpMessage.objects.count(), 1)

    def test_invalid_otp(self):
        _, tokens = self.register_user()
        _, device_token = self.register_device(tokens["access"])
        response = self.upload_otp(device_token, otp_value="123")
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST, response.data)
        self.assertEqual(response.data["message"], "Validation failed")
        self.assertIn("otp_value", response.data["errors"])

    def test_unauthorized_device(self):
        response = self.upload_otp("invalid-token")
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED, response.data)
        self.assertFalse(response.data["success"])

    def test_device_isolation(self):
        _, tokens_a = self.register_user(email="a@example.com")
        _, device_token_a = self.register_device(tokens_a["access"], device_identifier="a-device")
        upload = self.upload_otp(device_token_a)
        self.assertEqual(upload.status_code, status.HTTP_201_CREATED, upload.data)

        _, tokens_b = self.register_user(email="b@example.com")
        self.client.credentials(HTTP_AUTHORIZATION=f"Bearer {tokens_b['access']}")
        list_response = self.client.get(reverse("otp-list-create"))
        self.assertEqual(list_response.status_code, status.HTTP_200_OK, list_response.data)
        self.assertEqual(list_response.data["data"], [])

        message_id = upload.data["data"]["otp"]["id"]
        detail_response = self.client.get(reverse("otp-detail", kwargs={"id": message_id}))
        self.assertEqual(detail_response.status_code, status.HTTP_404_NOT_FOUND, detail_response.data)

    def test_device_token_is_stored_hashed(self):
        _, tokens = self.register_user()
        device, raw_token = self.register_device(tokens["access"])
        stored_user = User.objects.get(email="user@example.com")
        stored_device = stored_user.devices.get(id=device["id"])
        self.assertNotEqual(stored_device.device_token, raw_token)
        self.assertEqual(len(stored_device.device_token), 64)
