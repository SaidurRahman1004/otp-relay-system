from rest_framework.response import Response


def standard_response(success, message, data=None, errors=None, status=None):
    payload = {
        "success": success,
        "message": message,
        "data": data,
        "errors": errors,
    }
    return Response(payload, status=status)


def api_success(message, data=None, status=200):
    return standard_response(True, message, data if data is not None else {}, None, status)


def api_error(message, errors=None, status=400):
    return standard_response(False, message, None, errors if errors is not None else {}, status)
