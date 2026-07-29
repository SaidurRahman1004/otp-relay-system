import re

from rest_framework import serializers

from apps.otp_messages.models import OtpMessage
from apps.otp_messages.services import OtpMessageService


SENDER_PATTERN = re.compile(r"^\+?[0-9]{5,20}$")
OTP_PATTERN = re.compile(r"^[0-9]{4,8}$")


class OtpMessageSerializer(serializers.ModelSerializer):
    device_id = serializers.UUIDField(source="device.id", read_only=True)

    class Meta:
        model = OtpMessage
        fields = (
            "id",
            "device_id",
            "client_message_id",
            "sender",
            "otp_value",
            "message_hash",
            "received_at",
            "uploaded_at",
            "created_at",
        )
        read_only_fields = ("id", "device_id", "message_hash", "uploaded_at", "created_at")


class OtpUploadSerializer(serializers.Serializer):
    client_message_id = serializers.CharField(max_length=255)
    sender = serializers.CharField(max_length=20)
    otp_value = serializers.CharField(max_length=8)
    received_at = serializers.DateTimeField()

    def validate_sender(self, value):
        normalized = re.sub(r"[\s().-]", "", value)
        if not SENDER_PATTERN.match(normalized):
            raise serializers.ValidationError("Sender must be a valid phone number.")
        return normalized

    def validate_otp_value(self, value):
        if not OTP_PATTERN.match(value):
            raise serializers.ValidationError("OTP must contain 4 to 8 digits.")
        return value

    def create(self, validated_data):
        return OtpMessageService.upload_otp(device=self.context["device"], **validated_data)
