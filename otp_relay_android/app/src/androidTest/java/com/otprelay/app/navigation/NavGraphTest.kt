package com.otprelay.app.navigation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.otprelay.app.MainViewModel
import com.otprelay.app.core.storage.PreferencesManager
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavGraphTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `Splash navigates to Onboarding for new user`() = runTest {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val prefs = PreferencesManager(context)
        prefs.setOnboardingCompleted(false)
        val viewModel = MainViewModel(prefs)
        
        lateinit var navController: TestNavHostController

        composeTestRule.setContent {
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            AppNavGraph(mainViewModel = viewModel, navController = navController)
        }

        // Wait for Splash screen delay (1000ms)
        composeTestRule.mainClock.advanceTimeBy(1100)

        assertEquals(Route.ONBOARDING, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun `Completing onboarding navigates to main dashboard`() = runTest {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val prefs = PreferencesManager(context)
        prefs.setOnboardingCompleted(false)
        val viewModel = MainViewModel(prefs)
        
        lateinit var navController: TestNavHostController

        composeTestRule.setContent {
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            AppNavGraph(mainViewModel = viewModel, navController = navController)
        }

        // Wait for Splash
        composeTestRule.mainClock.advanceTimeBy(1100)
        
        // At Onboarding, click 'Get Started'
        composeTestRule.onNodeWithText("Get Started").performClick()
        
        // Verify we navigated to MAIN
        assertEquals(Route.MAIN, navController.currentBackStackEntry?.destination?.route)
        
        // Verify back stack does not have ONBOARDING (it was popped)
        val hasOnboardingInBackStack = navController.currentBackStack.value.any { it.destination.route == Route.ONBOARDING }
        assertEquals(false, hasOnboardingInBackStack)
    }
}
