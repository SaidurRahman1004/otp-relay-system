from django.contrib import admin
from django.urls import path
from drf_spectacular.views import SpectacularAPIView, SpectacularSwaggerView

from config.views import HealthCheckView
from apps.accounts.views import LoginView, MeView, RegisterView, TokenRefreshView
from apps.devices.views import DeviceDetailView, DeviceListView, DeviceRegisterView
from apps.otp_messages.views import OtpMessageListCreateView

urlpatterns = [
    path("admin/", admin.site.urls),
    path("api/health/", HealthCheckView.as_view(), name="health"),
    path("api/schema/", SpectacularAPIView.as_view(), name="schema"),
    path("api/docs/", SpectacularSwaggerView.as_view(url_name="schema"), name="swagger-ui"),
    path("api/v1/auth/register/", RegisterView.as_view(), name="auth-register"),
    path("api/v1/auth/login/", LoginView.as_view(), name="auth-login"),
    path("api/v1/auth/token/refresh/", TokenRefreshView.as_view(), name="token-refresh"),
    path("api/v1/auth/me/", MeView.as_view(), name="auth-me"),
    path("api/v1/devices/register/", DeviceRegisterView.as_view(), name="device-register"),
    path("api/v1/devices/", DeviceListView.as_view(), name="device-list"),
    path("api/v1/devices/<uuid:id>/", DeviceDetailView.as_view(), name="device-detail"),
    path("api/v1/otp-messages/", OtpMessageListCreateView.as_view(), name="otp-list-create"),
]
