from rest_framework import authentication, exceptions

from apps.devices.models import Device
from apps.devices.services import DeviceService


class DeviceTokenAuthentication(authentication.BaseAuthentication):
    keyword = "X-Device-Token"

    def authenticate(self, request):
        raw_token = request.headers.get(self.keyword)
        if not raw_token:
            return None

        token_hash = DeviceService.hash_device_token(raw_token)
        try:
            device = Device.objects.select_related("user").get(device_token=token_hash, is_active=True, user__is_active=True)
        except Device.DoesNotExist as exc:
            raise exceptions.AuthenticationFailed("Invalid or inactive device token.") from exc

        DeviceService.mark_seen(device)
        return (device.user, device)

    def authenticate_header(self, request):
        return self.keyword
