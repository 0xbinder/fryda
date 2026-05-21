package com.fryda.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val showInBottomBar: Boolean = true
) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Releases : Screen("releases", "Releases", Icons.Default.Settings)
    data object Servers : Screen("servers", "Servers", Icons.Default.Code)
    data object Logs : Screen("logs", "Logs", Icons.AutoMirrored.Filled.List)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}