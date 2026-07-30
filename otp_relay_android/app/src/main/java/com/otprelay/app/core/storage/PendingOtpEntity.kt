package com.otprelay.app.core.storage

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class OtpStatus {
    PENDING,
    UPLOADING,
    UPLOADED,
    FAILED
}

class Converters {
    @TypeConverter
    fun fromStatus(status: OtpStatus): String = status.name

    @TypeConverter
    fun toStatus(name: String): OtpStatus = OtpStatus.valueOf(name)
}

/**
 * Note: Encryption at rest (e.g. using SQLCipher or EncryptedSharedPreferences equivalents)
 * will be reviewed and implemented before real OTP storage is enabled in production.
 * Currently, otpValue is stored in plain text during this foundation phase.
 */
@Entity(tableName = "pending_otps")
data class PendingOtpEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientMessageId: String,
    val sender: String,
    val otpValue: String,
    val messageBody: String? = null,
    val receivedAt: Long,
    val status: OtpStatus = OtpStatus.PENDING,
    val retryCount: Int = 0,
    val lastError: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
