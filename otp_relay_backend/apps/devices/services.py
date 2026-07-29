import hashlib
import secrets

from django.db import IntegrityError, transaction
from django.utils import timezone
from rest_framework.exceptions import ValidationError

from apps.devices.models import Device


class DeviceService:
    @staticmethod
    def generate_device_token():
        raw_token = secrets.token_urlsafe(48)
        token_hash = DeviceService.hash_device_token(raw_token)
        return raw_token, token_hash

    @staticmethod
    def hash_device_token(raw_token):
        return hashlib.sha256(raw_token.encode("utf-8")).hexdigest()

    @staticmethod
    @transaction.atomic
    def register_device(*, user, device_name, device_identifier):
        raw_token, token_hash = DeviceService.generate_device_token()
        try:
            device = Device.objects.create(
                user=user,
                device_name=device_name,
                device_identifier=device_identifier,
                device_token=token_hash,
            )
        except IntegrityError as exc:
            raise ValidationError({"device_identifier": ["This device is already registered for this user."]}) from exc
        return device, raw_token

    @staticmethod
    def mark_seen(device):
        device.last_seen_at = timezone.now()
        device.save(update_fields=["last_seen_at", "updated_at"])
        return device
