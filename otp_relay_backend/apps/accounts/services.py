from django.contrib.auth import get_user_model
from django.db import IntegrityError, transaction
from rest_framework.exceptions import ValidationError


User = get_user_model()


class UserService:
    @staticmethod
    @transaction.atomic
    def register_user(*, email, password):
        normalized_email = User.objects.normalize_email(email)
        try:
            return User.objects.create_user(email=normalized_email, password=password)
        except IntegrityError as exc:
            raise ValidationError({"email": ["A user with this email already exists."]}) from exc
