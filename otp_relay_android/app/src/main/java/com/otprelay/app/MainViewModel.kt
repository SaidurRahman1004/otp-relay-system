package com.otprelay.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otprelay.app.core.storage.PreferencesManager
import com.otprelay.app.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val isOnboardingCompleted = preferencesManager.onboardingCompleted.first()
            _startDestination.value = if (isOnboardingCompleted) {
                Route.MAIN
            } else {
                Route.ONBOARDING
            }
        }
    }
    
    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesManager.setOnboardingCompleted(true)
            _startDestination.value = Route.MAIN
        }
    }
}
