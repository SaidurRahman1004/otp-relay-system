import hashlib

from django.db import IntegrityError, transaction
from django.utils import timezone
from rest_framework.exceptions import ValidationError

from apps.otp_messages.models import OtpMessage


class OtpMessageService:
    @staticmethod
    def build_message_hash(*, device, client_message_id, sender, otp_value, received_at):
        source = f"{device.id}:{client_message_id}:{sender}:{otp_value}:{received_at.isoformat()}"
        return hashlib.sha256(source.encode("utf-8")).hexdigest()

    @staticmethod
    @transaction.atomic
    def upload_otp(*, device, client_message_id, sender, otp_value, received_at):
        message_hash = OtpMessageService.build_message_hash(
            device=device,
            client_message_id=client_message_id,
            sender=sender,
            otp_value=otp_value,
            received_at=received_at,
        )
        defaults = {
            "sender": sender,
            "otp_value": otp_value,
            "message_hash": message_hash,
            "received_at": received_at,
            "uploaded_at": timezone.now(),
        }

        try:
            otp_message, created = OtpMessage.objects.get_or_create(
                device=device,
                client_message_id=client_message_id,
                defaults=defaults,
            )
        except IntegrityError:
            otp_message = OtpMessage.objects.get(device=device, client_message_id=client_message_id)
            created = False

        if not created and otp_message.message_hash != message_hash:
            raise ValidationError({
                "client_message_id": ["This client_message_id already exists for this device with different message data."]
            })

        return otp_message, created
