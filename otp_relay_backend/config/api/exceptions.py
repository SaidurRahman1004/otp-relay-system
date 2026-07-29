from django.http import Http404
from rest_framework import exceptions, status
from rest_framework.views import exception_handler

from config.api.responses import api_error


def standard_exception_handler(exc, context):
    response = exception_handler(exc, context)

    if response is None:
        if isinstance(exc, Http404):
            return api_error("Not found", {"detail": "Not found."}, status.HTTP_404_NOT_FOUND)
        return None

    if isinstance(exc, exceptions.ValidationError):
        return api_error("Validation failed", response.data, response.status_code)

    detail = response.data
    message = "Request failed"
    if isinstance(detail, dict) and "detail" in detail:
        message = str(detail["detail"])
    elif response.status_code == status.HTTP_401_UNAUTHORIZED:
        message = "Authentication credentials were not provided or are invalid"
    elif response.status_code == status.HTTP_403_FORBIDDEN:
        message = "Permission denied"
    elif response.status_code == status.HTTP_404_NOT_FOUND:
        message = "Not found"

    return api_error(message, detail, response.status_code)
