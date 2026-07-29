package com.otprelay.app.core.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "otp_relay_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val BACKEND_BASE_URL = stringPreferencesKey("backend_base_url")
        val DEVICE_ID = stringPreferencesKey("device_id")
        val DEVICE_TOKEN = stringPreferencesKey("device_token")
    }

    val onboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED] = completed
        }
    }

    val backendBaseUrl: Flow<String?> = dataStore.data.map { prefs ->
        prefs[BACKEND_BASE_URL]
    }

    suspend fun setBackendBaseUrl(url: String) {
        dataStore.edit { prefs ->
            prefs[BACKEND_BASE_URL] = url
        }
    }

    val deviceId: Flow<String?> = dataStore.data.map { prefs ->
        prefs[DEVICE_ID]
    }

    suspend fun setDeviceId(id: String) {
        dataStore.edit { prefs ->
            prefs[DEVICE_ID] = id
        }
    }

    val deviceToken: Flow<String?> = dataStore.data.map { prefs ->
        prefs[DEVICE_TOKEN]
    }

    suspend fun setDeviceToken(token: String) {
        dataStore.edit { prefs ->
            prefs[DEVICE_TOKEN] = token
        }
    }
}
