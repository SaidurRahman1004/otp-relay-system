package com.otprelay.app.core.storage

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferencesManagerTest {

    private lateinit var context: Context
    private lateinit var preferencesManager: PreferencesManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        preferencesManager = PreferencesManager(context)
    }

    @Test
    fun `onboardingCompleted is false by default`() = runTest {
        val result = preferencesManager.onboardingCompleted.first()
        assertEquals(false, result)
    }

    @Test
    fun `setOnboardingCompleted updates the state correctly`() = runTest {
        // Set to true
        preferencesManager.setOnboardingCompleted(true)
        val resultTrue = preferencesManager.onboardingCompleted.first()
        assertEquals(true, resultTrue)
        
        // Set to false
        preferencesManager.setOnboardingCompleted(false)
        val resultFalse = preferencesManager.onboardingCompleted.first()
        assertEquals(false, resultFalse)
    }
}
