package com.otprelay.app.core.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
        val BACKEND_BASE_URL = stringPreferencesKey("backend_base_url")
        val ALLOWED_SENDERS = stringSetPreferencesKey("allowed_senders")
    }

    val backendBaseUrl: Flow<String?> = dataStore.data.map { prefs ->
        prefs[BACKEND_BASE_URL]
    }

    suspend fun setBackendBaseUrl(url: String) {
        dataStore.edit { prefs ->
            prefs[BACKEND_BASE_URL] = url
        }
    }

    val allowedSenders: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[ALLOWED_SENDERS] ?: emptySet()
    }

    suspend fun setAllowedSenders(senders: Set<String>) {
        dataStore.edit { prefs ->
            prefs[ALLOWED_SENDERS] = senders
        }
    }
}
