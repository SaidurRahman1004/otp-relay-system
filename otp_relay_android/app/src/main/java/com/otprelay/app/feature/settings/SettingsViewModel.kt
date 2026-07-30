package com.otprelay.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otprelay.app.core.storage.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _allowedSenders = MutableStateFlow<Set<String>>(emptySet())
    val allowedSenders: StateFlow<Set<String>> = _allowedSenders.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.allowedSenders.collect { senders ->
                _allowedSenders.value = senders
            }
        }
    }

    fun addSender(sender: String) {
        if (sender.isNotBlank()) {
            val updated = _allowedSenders.value + sender
            viewModelScope.launch {
                preferencesManager.setAllowedSenders(updated)
            }
        }
    }

    fun removeSender(sender: String) {
        val updated = _allowedSenders.value - sender
        viewModelScope.launch {
            preferencesManager.setAllowedSenders(updated)
        }
    }

    fun reprovisionDevice() {
        viewModelScope.launch {
            preferencesManager.setDeviceToken("")
            preferencesManager.setOnboardingCompleted(false)
        }
    }
}
