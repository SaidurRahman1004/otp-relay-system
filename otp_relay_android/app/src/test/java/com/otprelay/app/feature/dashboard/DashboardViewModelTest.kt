package com.otprelay.app.feature.dashboard

import android.content.Context
import com.otprelay.app.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class DashboardViewModelTest {
    @Test
    fun `initial state has correct default mock values per requirements`() {
        val mockContext = mock(Context::class.java)
        `when`(mockContext.getString(R.string.dashboard_value_not_configured)).thenReturn("Not configured")
        `when`(mockContext.getString(R.string.dashboard_value_not_connected)).thenReturn("Not connected")
        `when`(mockContext.getString(R.string.dashboard_value_ready)).thenReturn("Ready")
        `when`(mockContext.getString(R.string.dashboard_demo_otp_masked)).thenReturn("*** (Masked)")

        val viewModel = DashboardViewModel(mockContext)
        val state = viewModel.uiState.value

        assertEquals("Not configured", state.smsReceiverStatus)
        assertEquals("Not connected", state.backendStatus)
        assertEquals("Ready", state.localQueueStatus)
        assertEquals("*** (Masked)", state.latestDemoOtp)
    }
}
