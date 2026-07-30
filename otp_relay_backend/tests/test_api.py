from django.urls import reverse
from rest_framework import status
from rest_framework.test import APITestCase

from apps.otp_messages.models import OtpMessage


class OtpRelayApiTests(APITestCase):
    def upload_otp(self, sender="+15551234567", otp="123456"):
        return self.client.post(
            reverse("otp-list-create"),
            {
                "sender": sender,
                "otp": otp,
                "message_body": f"Your code is {otp}",
                "received_at": "2026-07-29T12:00:00Z",
            },
            format="json",
        )

    def test_health_endpoint(self):
        response = self.client.get(reverse("health"))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertTrue(response.data["success"])

    def test_otp_upload(self):
        response = self.upload_otp()
        self.assertEqual(response.status_code, status.HTTP_201_CREATED, response.data)
        self.assertEqual(OtpMessage.objects.count(), 1)

    def test_invalid_otp(self):
        response = self.upload_otp(otp="123")
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST, response.data)
        self.assertIn("otp", response.data["errors"])

    def test_get_otps(self):
        self.upload_otp()
        response = self.client.get(reverse("otp-list-create"))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data["data"]["count"], 1)
        self.assertEqual(len(response.data["data"]["results"]), 1)

