from rest_framework.permissions import AllowAny
from rest_framework.views import APIView
from rest_framework.response import Response

from apps.otp_messages.models import OtpMessage
from apps.otp_messages.serializers import OtpMessageSerializer
from config.api.responses import api_success

class OtpMessageListCreateView(APIView):
    permission_classes = [AllowAny]
    authentication_classes = []

    def post(self, request):
        serializer = OtpMessageSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return api_success("OTP received successfully", serializer.data, status=201)

    def get(self, request):
        messages = OtpMessage.objects.all()[:100]
        data = {
            "count": len(messages),
            "results": OtpMessageSerializer(messages, many=True).data
        }
        return api_success("OTP records retrieved successfully", data)

