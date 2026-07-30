from django.contrib import admin

from apps.otp_messages.models import OtpMessage


@admin.register(OtpMessage)
class OtpMessageAdmin(admin.ModelAdmin):
    list_display = ("id", "sender", "received_at", "created_at")
    list_filter = ("received_at", "created_at")
    search_fields = ("sender",)
    readonly_fields = ("id", "created_at")
    exclude = ("otp",)

