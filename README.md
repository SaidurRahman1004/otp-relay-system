# OTP Relay System

This project is a complete OTP Relay backend system designed to securely relay SMS OTPs from an Android device to backend services. It is built with Django and Django REST Framework, using a custom device token authentication model alongside standard JWT for user authentication.

## Project Structure

- `otp_relay_backend/`: Contains the Django backend implementation.
  - `apps/`: Houses all Django apps (accounts, devices, otp_messages).
  - `config/`: Global Django configuration and settings.
  - `docker/`: Docker and container configurations.

## Features

- **Authentication System:** Complete custom user model with JWT authentication. Endpoints for registration, login, token refresh, and user profile (`/api/v1/auth/*`).
- **Device Management:** Secure device registration that provisions separate, unique device tokens which are hashed before being stored. Includes endpoints to register, list, and view devices (`/api/v1/devices/*`).
- **OTP Upload & Retrieval:** Endpoint for Android devices to upload OTPs (`POST /api/v1/otp/`) authenticated via the hashed device token. Endpoint for web clients to retrieve their synced OTPs (`GET /api/v1/otp/`) using JWT authentication.
- **Security & Schema:** OpenAPI 3.0 schema generation (`/api/schema/`, `/api/docs/`). Sensitive data is excluded from Django Admin, device tokens are strictly hashed.

## Quickstart

### Docker Setup

1. Build and run the database and web services:
   ```bash
   cd otp_relay_backend
   docker compose up -d
   ```

2. Apply migrations:
   ```bash
   docker compose exec web python manage.py migrate
   ```

### Local Setup

1. Set up the virtual environment:
   ```bash
   cd otp_relay_backend
   python -m venv .venv
   source .venv/bin/activate
   pip install -r requirements/base.txt
   ```

2. Run tests:
   ```bash
   python manage.py test
   ```

3. Run the development server:
   ```bash
   python manage.py runserver 0.0.0.0:8000
   ```

## Documentation

For a full implementation summary and audit results, please refer to the [OTP_RELAY_BACKEND_IMPLEMENTATION_SUMMARY.md](OTP_RELAY_BACKEND_IMPLEMENTATION_SUMMARY.md).

For full API documentation, navigate to `/api/docs/` when the server is running.
