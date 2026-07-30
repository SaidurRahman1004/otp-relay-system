package com.otprelay.app.feature.otp_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otprelay.app.core.storage.PendingOtpDao
import com.otprelay.app.core.storage.PendingOtpEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OtpHistoryViewModel @Inject constructor(
    private val pendingOtpDao: PendingOtpDao
) : ViewModel() {

    val otpHistory: StateFlow<List<PendingOtpEntity>> = pendingOtpDao.getAllOtps()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
