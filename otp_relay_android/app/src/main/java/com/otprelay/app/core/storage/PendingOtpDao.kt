package com.otprelay.app.core.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingOtpDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOtp(otp: PendingOtpEntity): Long

    @Update
    suspend fun updateOtp(otp: PendingOtpEntity)

    @Query("SELECT * FROM pending_otps WHERE status = :status ORDER BY createdAt ASC")
    fun getOtpsByStatus(status: OtpStatus): Flow<List<PendingOtpEntity>>

    @Query("SELECT * FROM pending_otps ORDER BY createdAt DESC")
    fun getAllOtps(): Flow<List<PendingOtpEntity>>

    @Query("SELECT COUNT(*) FROM pending_otps WHERE status = :status")
    fun countOtpsByStatus(status: OtpStatus): Flow<Int>
}
