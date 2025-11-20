package com.mobilecomputing.myfinance.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mobilecomputing.myfinance.navigation.Screen
import com.mobilecomputing.myfinance.screens.AnalysisScreen
import com.mobilecomputing.myfinance.screens.ContractsScreen
import com.mobilecomputing.myfinance.screens.DashboardScreen
import com.mobilecomputing.myfinance.screens.EntriesScreen
import com.mobilecomputing.myfinance.screens.SettingsScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
        composable(Screen.Entries.route) {
            EntriesScreen()
        }
        composable(Screen.Contracts.route) {
            ContractsScreen()
        }
        composable(Screen.Analysis.route) {
            AnalysisScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
