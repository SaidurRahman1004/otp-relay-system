package com.otprelay.app.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.otprelay.app.R

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val backendUrl by viewModel.backendBaseUrl.collectAsState()
    val allowedSenders by viewModel.allowedSenders.collectAsState()
    
    var urlInput by remember { mutableStateOf(backendUrl) }
    var senderInput by remember { mutableStateOf("") }
    
    LaunchedEffect(backendUrl) {
        urlInput = backendUrl
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = urlInput,
            onValueChange = { urlInput = it },
            label = { Text("Backend Base URL") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.setBackendBaseUrl(urlInput) }) {
            Text("Save URL")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Allowed Senders", style = MaterialTheme.typography.titleMedium)
        allowedSenders.forEach { sender ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(sender)
                TextButton(onClick = { viewModel.removeSender(sender) }) {
                    Text("Remove")
                }
            }
        }
        
        OutlinedTextField(
            value = senderInput,
            onValueChange = { senderInput = it },
            label = { Text("New Sender (e.g. +1234567890)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { 
            viewModel.addSender(senderInput)
            senderInput = ""
        }) {
            Text("Add Sender")
        }
    }
}

