import uuid

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):
    initial = True

    dependencies = [
        ("devices", "0001_initial"),
    ]

    operations = [
        migrations.CreateModel(
            name="OtpMessage",
            fields=[
                ("id", models.UUIDField(default=uuid.uuid4, editable=False, primary_key=True, serialize=False)),
                ("client_message_id", models.CharField(max_length=255)),
                ("sender", models.CharField(max_length=20)),
                ("otp_value", models.CharField(max_length=8)),
                ("message_hash", models.CharField(db_index=True, max_length=64)),
                ("received_at", models.DateTimeField()),
                ("uploaded_at", models.DateTimeField()),
                ("created_at", models.DateTimeField(auto_now_add=True)),
                ("device", models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name="otp_messages", to="devices.device")),
            ],
            options={
                "ordering": ["-received_at", "-created_at"],
                "constraints": [
                    models.UniqueConstraint(fields=("device", "client_message_id"), name="unique_client_message_id_per_device"),
                ],
            },
        ),
    ]
