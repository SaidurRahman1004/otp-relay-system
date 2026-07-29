from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.views import APIView
from rest_framework_simplejwt.serializers import TokenRefreshSerializer

from apps.accounts.serializers import LoginSerializer, RegisterSerializer, UserSerializer, jwt_payload_for_user
from config.api.responses import api_success


class RegisterView(APIView):
    authentication_classes = []
    permission_classes = [AllowAny]

    def post(self, request):
        serializer = RegisterSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.save()
        return api_success(
            "User registered successfully",
            {"user": UserSerializer(user).data, "tokens": jwt_payload_for_user(user)},
            status=201,
        )


class LoginView(APIView):
    authentication_classes = []
    permission_classes = [AllowAny]

    def post(self, request):
        serializer = LoginSerializer(data=request.data, context={"request": request})
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data["user"]
        return api_success("Login successful", {"user": UserSerializer(user).data, "tokens": jwt_payload_for_user(user)})


class TokenRefreshView(APIView):
    authentication_classes = []
    permission_classes = [AllowAny]

    def post(self, request):
        serializer = TokenRefreshSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        return api_success("Token refreshed successfully", serializer.validated_data)


class MeView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        return api_success("Authenticated user retrieved", UserSerializer(request.user).data)
