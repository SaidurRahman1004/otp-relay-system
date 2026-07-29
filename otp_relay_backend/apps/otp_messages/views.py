from django.shortcuts import get_object_or_404
from rest_framework.permissions import IsAuthenticated
from rest_framework.views import APIView

from apps.devices.authentication import DeviceTokenAuthentication
from apps.otp_messages.models import OtpMessage
from apps.otp_messages.serializers import OtpMessageSerializer, OtpUploadSerializer
from config.api.responses import api_success


class OtpMessageListCreateView(APIView):
    def get_authenticators(self):
        if hasattr(self, "request") and getattr(self.request, "method", None) == "POST":
            return [DeviceTokenAuthentication()]
        return super().get_authenticators()

    permission_classes = [IsAuthenticated]

    def post(self, request):
        device = request.auth
        serializer = OtpUploadSerializer(data=request.data, context={"device": device})
        serializer.is_valid(raise_exception=True)
        otp_message, created = serializer.save()
        status_code = 201 if created else 200
        message = "OTP uploaded successfully" if created else "Duplicate OTP upload ignored"
        return api_success(message, {"otp": OtpMessageSerializer(otp_message).data, "created": created}, status=status_code)

    def get(self, request):
        messages = OtpMessage.objects.filter(device__user=request.user).select_related("device")
        return api_success("OTP messages retrieved successfully", OtpMessageSerializer(messages, many=True).data)


class OtpMessageDetailView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, id):
        otp_message = get_object_or_404(OtpMessage.objects.select_related("device"), id=id, device__user=request.user)
        return api_success("OTP message retrieved successfully", OtpMessageSerializer(otp_message).data)
