from django.shortcuts import get_object_or_404
from rest_framework.permissions import IsAuthenticated
from rest_framework.views import APIView

from apps.devices.models import Device
from apps.devices.serializers import DeviceRegisterSerializer, DeviceSerializer
from config.api.responses import api_success


class DeviceRegisterView(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        serializer = DeviceRegisterSerializer(data=request.data, context={"request": request})
        serializer.is_valid(raise_exception=True)
        device, raw_token = serializer.save()
        return api_success(
            "Device registered successfully",
            {"device": DeviceSerializer(device).data, "device_token": raw_token},
            status=201,
        )


class DeviceListView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        devices = Device.objects.filter(user=request.user)
        return api_success("Devices retrieved successfully", DeviceSerializer(devices, many=True).data)


class DeviceDetailView(APIView):
    permission_classes = [IsAuthenticated]

    def get_object(self, request, id):
        return get_object_or_404(Device, id=id, user=request.user)

    def get(self, request, id):
        device = self.get_object(request, id)
        return api_success("Device retrieved successfully", DeviceSerializer(device).data)

    def patch(self, request, id):
        device = self.get_object(request, id)
        serializer = DeviceSerializer(device, data=request.data, partial=True)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return api_success("Device updated successfully", serializer.data)
