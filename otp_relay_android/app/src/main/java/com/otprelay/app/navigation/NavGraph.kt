package com.otprelay.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.otprelay.app.feature.dashboard.DashboardScreen
import com.otprelay.app.feature.diagnostics.DiagnosticsScreen
import com.otprelay.app.feature.onboarding.OnboardingScreen
import com.otprelay.app.feature.otp_history.OtpHistoryScreen
import com.otprelay.app.feature.settings.SettingsScreen
import com.otprelay.app.feature.splash.SplashScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import com.otprelay.app.MainViewModel
import com.otprelay.app.R

object Route {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val MAIN = "main"
}

object MainRoute {
    const val DASHBOARD = "dashboard"
    const val HISTORY = "history"
    const val DIAGNOSTICS = "diagnostics"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavGraph(
    mainViewModel: MainViewModel,
    navController: NavHostController = rememberNavController()
) {
    val startDestination by mainViewModel.startDestination.collectAsState()

    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = Route.SPLASH
        ) {
            composable(Route.SPLASH) {
                SplashScreen(
                    onSplashComplete = {
                        navController.navigate(startDestination!!) {
                            popUpTo(Route.SPLASH) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Route.ONBOARDING) {
                OnboardingScreen(
                    onOnboardingComplete = {
                        mainViewModel.completeOnboarding()
                        navController.navigate(Route.MAIN) {
                            popUpTo(Route.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Route.MAIN) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    val items = listOf(
        MainRoute.DASHBOARD to R.string.nav_dashboard,
        MainRoute.HISTORY to R.string.nav_history,
        MainRoute.DIAGNOSTICS to R.string.nav_diagnostics,
        MainRoute.SETTINGS to R.string.nav_settings
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { (route, titleRes) ->
                    val title = stringResource(id = titleRes)
                    NavigationBarItem(
                        icon = { Text(title.take(1)) },
                        label = { Text(title) },
                        selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainRoute.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainRoute.DASHBOARD) { DashboardScreen() }
            composable(MainRoute.HISTORY) { OtpHistoryScreen() }
            composable(MainRoute.DIAGNOSTICS) { DiagnosticsScreen() }
            composable(MainRoute.SETTINGS) { SettingsScreen() }
        }
    }
}
