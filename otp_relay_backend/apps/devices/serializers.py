from rest_framework import serializers

from apps.devices.models import Device
from apps.devices.services import DeviceService


class DeviceSerializer(serializers.ModelSerializer):
    class Meta:
        model = Device
        fields = (
            "id",
            "device_name",
            "device_identifier",
            "is_active",
            "last_seen_at",
            "created_at",
            "updated_at",
        )
        read_only_fields = ("id", "device_identifier", "last_seen_at", "created_at", "updated_at")


class DeviceRegisterSerializer(serializers.Serializer):
    device_name = serializers.CharField(max_length=120)
    device_identifier = serializers.CharField(max_length=255)

    def create(self, validated_data):
        return DeviceService.register_device(user=self.context["request"].user, **validated_data)
