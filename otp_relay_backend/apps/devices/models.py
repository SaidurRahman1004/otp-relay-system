import uuid

from django.conf import settings
from django.db import models


class Device(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name="devices")
    device_name = models.CharField(max_length=120)
    device_identifier = models.CharField(max_length=255)
    device_token = models.CharField(max_length=128, unique=True)
    is_active = models.BooleanField(default=True)
    last_seen_at = models.DateTimeField(null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        ordering = ["-created_at"]
        constraints = [
            models.UniqueConstraint(fields=["user", "device_identifier"], name="unique_device_identifier_per_user"),
        ]

    def __str__(self):
        return f"{self.device_name} ({self.user.email})"
