package com.otprelay.app.feature.sms.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.otprelay.app.core.storage.OtpStatus
import com.otprelay.app.core.storage.PendingOtpDao
import com.otprelay.app.feature.sms.api.OtpApiService
import com.otprelay.app.feature.sms.api.OtpUploadRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@HiltWorker
class OtpUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val pendingOtpDao: PendingOtpDao,
    private val otpApiService: OtpApiService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val pendingOtps = pendingOtpDao.getOtpsByStatus(OtpStatus.PENDING).first()
            if (pendingOtps.isEmpty()) {
                return Result.success()
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            var hasFailures = false

            for (otp in pendingOtps) {
                try {
                    // Update status to UPLOADING to prevent concurrent attempts
                    pendingOtpDao.updateOtp(otp.copy(status = OtpStatus.UPLOADING, updatedAt = System.currentTimeMillis()))

                    val request = OtpUploadRequest(
                        sender = otp.sender,
                        otp = otp.otpValue,
                        message_body = otp.messageBody ?: "",
                        received_at = dateFormat.format(Date(otp.receivedAt))
                    )

                    otpApiService.uploadOtp(request)
                    
                    // Success
                    pendingOtpDao.updateOtp(otp.copy(status = OtpStatus.UPLOADED, updatedAt = System.currentTimeMillis()))
                } catch (e: HttpException) {
                    val isPermanentError = e.code() in 400..499
                    if (e.code() == 401) {
                        // Unauthorized - device needs reprovisioning
                        // Leave as pending or mark failed, but we need to stop retrying immediately
                        pendingOtpDao.updateOtp(otp.copy(
                            status = OtpStatus.PENDING,
                            lastError = "Unauthorized: Device needs re-provisioning",
                            updatedAt = System.currentTimeMillis()
                        ))
                        return Result.failure()
                    }
                    
                    if (isPermanentError) {
                        pendingOtpDao.updateOtp(otp.copy(
                            status = OtpStatus.FAILED,
                            lastError = "Permanent error: ${e.code()}",
                            updatedAt = System.currentTimeMillis()
                        ))
                    } else {
                        pendingOtpDao.updateOtp(otp.copy(
                            status = OtpStatus.PENDING,
                            retryCount = otp.retryCount + 1,
                            lastError = "Server error: ${e.code()}",
                            updatedAt = System.currentTimeMillis()
                        ))
                        hasFailures = true
                    }
                } catch (e: Exception) {
                    // Network or unknown error
                    pendingOtpDao.updateOtp(otp.copy(
                        status = OtpStatus.PENDING,
                        retryCount = otp.retryCount + 1,
                        lastError = e.localizedMessage ?: "Unknown network error",
                        updatedAt = System.currentTimeMillis()
                    ))
                    hasFailures = true
                }
            }

            return if (hasFailures) Result.retry() else Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }
}
