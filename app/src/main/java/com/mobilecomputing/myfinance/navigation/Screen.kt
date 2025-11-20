package com.mobilecomputing.myfinance.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard)
    object Entries : Screen("entries", "Entries", Icons.AutoMirrored.Filled.Article)
    object Contracts : Screen("contracts", "Contracts", Icons.Filled.Description)
    object Analysis : Screen("analysis", "Analysis", Icons.Filled.Analytics)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}
