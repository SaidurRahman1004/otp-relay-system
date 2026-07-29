package com.otprelay.app.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.compose.ui.res.stringResource
import com.otprelay.app.R

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.dashboard_title), style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Status Cards
        StatusCard(
            title = stringResource(R.string.dashboard_status_sms_receiver),
            status = state.smsReceiverStatus,
            isWarning = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        StatusCard(
            title = stringResource(R.string.dashboard_status_backend),
            status = state.backendStatus,
            isWarning = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        StatusCard(
            title = stringResource(R.string.dashboard_status_local_queue),
            status = state.localQueueStatus,
            isWarning = false
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Demo OTP masked view
        Text(stringResource(R.string.dashboard_latest_otp_title), style = MaterialTheme.typography.titleMedium)
        Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.dashboard_demo_sender))
                Text(stringResource(R.string.dashboard_otp_prefix, state.latestDemoOtp))
            }
        }
    }
}

@Composable
private fun StatusCard(title: String, status: String, isWarning: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = status, 
                color = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
