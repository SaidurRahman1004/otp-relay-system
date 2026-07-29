import uuid

from django.db import models

from apps.devices.models import Device


class OtpMessage(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    device = models.ForeignKey(Device, on_delete=models.CASCADE, related_name="otp_messages")
    client_message_id = models.CharField(max_length=255)
    sender = models.CharField(max_length=20)
    otp_value = models.CharField(max_length=8)
    message_hash = models.CharField(max_length=64, db_index=True)
    received_at = models.DateTimeField()
    uploaded_at = models.DateTimeField()
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ["-received_at", "-created_at"]
        constraints = [
            models.UniqueConstraint(fields=["device", "client_message_id"], name="unique_client_message_id_per_device"),
        ]

    def __str__(self):
        return f"OTP from {self.sender} for {self.device_id}"
