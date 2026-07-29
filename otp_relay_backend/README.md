# OTP Relay Backend

Django REST API for receiving OTP values extracted from Android SMS messages and storing them with PostgreSQL-backed constraints and idempotent upload behavior.

## Stack

- Python `3-slim` Docker image
- Django
- Django REST Framework
- PostgreSQL only
- django-environ
- djangorestframework-simplejwt
- drf-spectacular
- django-cors-headers
- UUID primary keys
- Service-layer business logic with thin views

## Setup

```bash
cd otp_relay_backend
cp .env.example .env
docker compose build
docker compose up -d db
docker compose run --rm web python manage.py migrate
docker compose up -d web
```

API health:

```bash
curl http://localhost:8000/api/health/
```

Swagger UI:

```text
http://localhost:8000/api/docs/
```

OpenAPI schema:

```text
http://localhost:8000/api/schema/
```

## Tests

```bash
docker compose run --rm web python manage.py test
```

## API Endpoints

- `GET /api/health/`
- `POST /api/v1/auth/register/`
- `POST /api/v1/auth/login/`
- `POST /api/v1/auth/token/refresh/`
- `GET /api/v1/auth/me/`
- `POST /api/v1/devices/register/`
- `GET /api/v1/devices/`
- `GET /api/v1/devices/{id}/`
- `PATCH /api/v1/devices/{id}/`
- `POST /api/v1/otp/`
- `GET /api/v1/otp/`
- `GET /api/v1/otp/{id}/`

## Device Authentication

Device OTP uploads use an `X-Device-Token` header. The raw token is returned once during device registration and only a SHA-256 hash is stored in the database.

```bash
curl -X POST http://localhost:8000/api/v1/otp/ \
  -H "Content-Type: application/json" \
  -H "X-Device-Token: <raw-device-token>" \
  -d '{
    "client_message_id": "sms-001",
    "sender": "+15551234567",
    "otp_value": "123456",
    "received_at": "2026-07-29T12:00:00Z"
  }'
```

## Response Format

Success:

```json
{
  "success": true,
  "message": "Human-readable message",
  "data": {},
  "errors": null
}
```

Validation error:

```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "errors": {}
}
```
