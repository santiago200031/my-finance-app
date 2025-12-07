package com.mobilecomputing.myfinance.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mobilecomputing.myfinance.screens.add_entry.AddEntryScreen
import com.mobilecomputing.myfinance.screens.analysis.AnalysisScreen
import com.mobilecomputing.myfinance.screens.budget_planning.BudgetPlanningScreen
import com.mobilecomputing.myfinance.screens.contracts.ContractsScreen
import com.mobilecomputing.myfinance.screens.dashboard.DashboardScreen
import com.mobilecomputing.myfinance.screens.entries.EntriesScreen
import com.mobilecomputing.myfinance.screens.export_data.ExportDataScreen
import com.mobilecomputing.myfinance.screens.reminders.RemindersScreen
import com.mobilecomputing.myfinance.screens.settings.SettingsScreen
import com.mobilecomputing.myfinance.screens.sharing_settings.SharingSettingsScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onRemindersClick = { navController.navigate(Screen.Reminders.route) },
                onAddEntryClick = { navController.navigate(Screen.AddEntry.route) }
            )
        }
        composable(Screen.AddEntry.route) {
            AddEntryScreen()
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
        composable(Screen.Reminders.route) {
            RemindersScreen()
        }
        composable(Screen.BudgetPlanning.route) {
            BudgetPlanningScreen()
        }
        composable(Screen.ExportData.route) {
            ExportDataScreen()
        }
        composable(Screen.SharingSettings.route) {
            SharingSettingsScreen()
        }
    }
}
