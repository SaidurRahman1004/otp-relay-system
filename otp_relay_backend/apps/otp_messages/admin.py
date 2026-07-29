from django.contrib import admin

from apps.otp_messages.models import OtpMessage


@admin.register(OtpMessage)
class OtpMessageAdmin(admin.ModelAdmin):
    list_display = ("id", "device", "client_message_id", "sender", "received_at", "uploaded_at", "created_at")
    list_filter = ("received_at", "uploaded_at", "created_at")
    search_fields = ("client_message_id", "sender", "device__device_name", "device__user__email")
    readonly_fields = ("id", "message_hash", "created_at")
    exclude = ("otp_value",)
