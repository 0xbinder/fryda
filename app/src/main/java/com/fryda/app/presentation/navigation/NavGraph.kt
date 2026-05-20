package com.fryda.app.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fryda.app.presentation.home.HomeScreen
import com.fryda.app.presentation.logs.LogsScreen
import com.fryda.app.presentation.servers.ServersScreen
import com.fryda.app.presentation.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToLogs = { navController.navigate(Screen.Logs.route) },
                onNavigateToServers = {}
            )
        }
        composable(Screen.Servers.route) {
            ServersScreen()
        }
        composable(Screen.Logs.route) {
            LogsScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}