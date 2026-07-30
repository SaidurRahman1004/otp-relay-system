import re
from django.utils import timezone
from rest_framework import serializers

from apps.otp_messages.models import OtpMessage

SENDER_PATTERN = re.compile(r"^\+?[0-9]{5,20}$")
OTP_PATTERN = re.compile(r"^[0-9]{4,8}$")

class OtpMessageSerializer(serializers.ModelSerializer):
    class Meta:
        model = OtpMessage
        fields = (
            "id",
            "sender",
            "otp",
            "message_body",
            "received_at",
            "created_at",
        )
        read_only_fields = ("id", "created_at")

    def validate_sender(self, value):
        normalized = re.sub(r"[\s().-]", "", value)
        if not SENDER_PATTERN.match(normalized):
            raise serializers.ValidationError("Sender must be a valid phone number.")
        return normalized

    def validate_otp(self, value):
        if not OTP_PATTERN.match(value):
            raise serializers.ValidationError("OTP must contain 4 to 8 digits.")
        return value

    def create(self, validated_data):
        if 'received_at' not in validated_data:
            validated_data['received_at'] = timezone.now()
        return super().create(validated_data)
