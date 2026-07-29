package com.otprelay.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.otprelay.app.feature.dashboard.DashboardScreen
import com.otprelay.app.feature.dashboard.DashboardViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dashboardScreen_displaysRequiredStatuses() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val viewModel = DashboardViewModel(context)

        composeTestRule.setContent {
            DashboardScreen(viewModel = viewModel)
        }

        // Verify required UI states are displayed
        composeTestRule.onNodeWithText("Not configured").assertExists()
        composeTestRule.onNodeWithText("Not connected").assertExists()
        composeTestRule.onNodeWithText("Ready").assertExists()
        composeTestRule.onNodeWithText("OTP: *** (Masked)").assertExists()
    }
}
