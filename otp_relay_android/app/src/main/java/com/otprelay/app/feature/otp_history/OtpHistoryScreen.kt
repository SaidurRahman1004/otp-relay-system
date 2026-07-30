package com.otprelay.app.feature.otp_history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.otprelay.app.core.storage.PendingOtpEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OtpHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: OtpHistoryViewModel = hiltViewModel()
) {
    val otps by viewModel.otpHistory.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "OTP History",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (otps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No OTPs captured yet.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(otps) { otp ->
                    OtpHistoryItem(otp)
                }
            }
        }
    }
}

@Composable
fun OtpHistoryItem(otp: PendingOtpEntity) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())
    val dateString = dateFormat.format(Date(otp.receivedAt))
    
    // Mask OTP: e.g., show only last two digits, hide the rest
    val maskedOtp = if (otp.otpValue.length > 2) {
        "*".repeat(otp.otpValue.length - 2) + otp.otpValue.takeLast(2)
    } else {
        "**"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = otp.sender, fontWeight = FontWeight.Bold)
                Text(text = maskedOtp, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = dateString, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = otp.status.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (otp.status.name) {
                        "UPLOADED" -> MaterialTheme.colorScheme.primary
                        "FAILED" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            if (otp.lastError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = otp.lastError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
