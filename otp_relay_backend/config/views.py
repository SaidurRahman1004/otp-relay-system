from rest_framework.permissions import AllowAny
from rest_framework.views import APIView

from config.api.responses import api_success


class HealthCheckView(APIView):
    authentication_classes = []
    permission_classes = [AllowAny]

    def get(self, request):
        return api_success(message="Service is healthy", data={"status": "ok"})
