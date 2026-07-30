package com.otprelay.app.feature.sms.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

@Serializable
data class OtpUploadRequest(
    val client_message_id: String,
    val sender: String,
    val otp_value: String,
    val received_at: String // ISO-8601 string
)

@Serializable
data class OtpUploadResponse(
    val success: Boolean,
    val message: String
)

interface OtpApiService {
    @POST("api/v1/otp-messages/")
    suspend fun uploadOtp(@Body request: OtpUploadRequest): OtpUploadResponse
}
