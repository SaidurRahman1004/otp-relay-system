package com.otprelay.app.data.repository

import com.otprelay.app.core.storage.OtpStatus
import com.otprelay.app.core.storage.PendingOtpDao
import com.otprelay.app.core.storage.PendingOtpEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface LocalOtpRepository {
    fun getAllOtps(): Flow<List<PendingOtpEntity>>
    fun getOtpsByStatus(status: OtpStatus): Flow<List<PendingOtpEntity>>
    fun countOtpsByStatus(status: OtpStatus): Flow<Int>
    suspend fun addOtp(otp: PendingOtpEntity)
    suspend fun updateOtp(otp: PendingOtpEntity)
}

@Singleton
class LocalOtpRepositoryImpl @Inject constructor(
    private val dao: PendingOtpDao
) : LocalOtpRepository {
    override fun getAllOtps(): Flow<List<PendingOtpEntity>> = dao.getAllOtps()
    
    override fun getOtpsByStatus(status: OtpStatus): Flow<List<PendingOtpEntity>> = dao.getOtpsByStatus(status)
    
    override fun countOtpsByStatus(status: OtpStatus): Flow<Int> = dao.countOtpsByStatus(status)
    
    override suspend fun addOtp(otp: PendingOtpEntity) {
        dao.insertOtp(otp)
    }
    
    override suspend fun updateOtp(otp: PendingOtpEntity) {
        dao.updateOtp(otp)
    }
}
