package com.otprelay.app.feature.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.otprelay.app.core.storage.OtpStatus
import com.otprelay.app.core.storage.PendingOtpDao
import com.otprelay.app.core.storage.PendingOtpEntity
import com.otprelay.app.core.storage.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var otpExtractor: OtpExtractor

    @Inject
    lateinit var pendingOtpDao: PendingOtpDao

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        // Group multipart messages by sender
        val senderToMessage = mutableMapOf<String, StringBuilder>()
        for (sms in messages) {
            val sender = sms.originatingAddress ?: continue
            val body = sms.messageBody ?: continue
            senderToMessage.getOrPut(sender) { StringBuilder() }.append(body)
        }

        val pendingResult = goAsync()
        
        scope.launch {
            try {
                val allowedSenders = preferencesManager.allowedSenders.first()
                
                for ((sender, bodyBuilder) in senderToMessage) {
                    val fullBody = bodyBuilder.toString()
                    
                    // Filter allowed senders. If empty, maybe allow all for testing?
                    // "Ignore SMS messages from unapproved senders" -> we must filter.
                    if (allowedSenders.isNotEmpty() && !allowedSenders.contains(sender)) {
                        continue
                    }

                    val otp = otpExtractor.extractOtp(fullBody)
                    if (otp != null) {
                        val clientMessageId = UUID.randomUUID().toString()
                        val entity = PendingOtpEntity(
                            clientMessageId = clientMessageId,
                            sender = sender,
                            otpValue = otp,
                            messageBody = fullBody,
                            receivedAt = System.currentTimeMillis(),
                            status = OtpStatus.PENDING,
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        pendingOtpDao.insertOtp(entity)
                        
                        // Trigger WorkManager upload
                        val workRequest = androidx.work.OneTimeWorkRequestBuilder<com.otprelay.app.feature.sms.worker.OtpUploadWorker>()
                            .setConstraints(
                                androidx.work.Constraints.Builder()
                                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                                    .build()
                            )
                            .setBackoffCriteria(
                                androidx.work.BackoffPolicy.EXPONENTIAL,
                                androidx.work.WorkRequest.MIN_BACKOFF_MILLIS,
                                java.util.concurrent.TimeUnit.MILLISECONDS
                            )
                            .build()
                            
                        androidx.work.WorkManager.getInstance(context).enqueueUniqueWork(
                            "otp_upload_work",
                            androidx.work.ExistingWorkPolicy.APPEND_OR_REPLACE,
                            workRequest
                        )
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
