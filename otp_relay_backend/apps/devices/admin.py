from django.contrib import admin

from apps.devices.models import Device


@admin.register(Device)
class DeviceAdmin(admin.ModelAdmin):
    list_display = ("device_name", "user", "device_identifier", "is_active", "last_seen_at", "created_at")
    list_filter = ("is_active", "created_at", "last_seen_at")
    search_fields = ("device_name", "device_identifier", "user__email")
    readonly_fields = ("id", "device_token", "created_at", "updated_at")
