import uuid

from django.db import models


class OtpMessage(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    sender = models.CharField(max_length=20)
    otp = models.CharField(max_length=8)
    message_body = models.TextField(blank=True, null=True)
    received_at = models.DateTimeField()
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ["-received_at", "-created_at"]

    def __str__(self):
        return f"OTP from {self.sender}"
