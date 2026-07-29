package com.otprelay.app.feature.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.otprelay.app.R

data class DashboardState(
    val smsReceiverStatus: String = "",
    val backendStatus: String = "",
    val localQueueStatus: String = "",
    val latestDemoOtp: String = ""
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardState(
        smsReceiverStatus = context.getString(R.string.dashboard_value_not_configured),
        backendStatus = context.getString(R.string.dashboard_value_not_connected),
        localQueueStatus = context.getString(R.string.dashboard_value_ready),
        latestDemoOtp = context.getString(R.string.dashboard_demo_otp_masked)
    ))
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()
}
