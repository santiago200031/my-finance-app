package com.mobilecomputing.myfinance.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mobilecomputing.myfinance.screens.add_contract.AddContractScreen
import com.mobilecomputing.myfinance.screens.add_entry.AddEntryScreen
import com.mobilecomputing.myfinance.screens.analysis.AnalysisScreen
import com.mobilecomputing.myfinance.screens.budget_planning.BudgetPlanningScreen
import com.mobilecomputing.myfinance.screens.contracts.ContractsScreen
import com.mobilecomputing.myfinance.screens.dashboard.DashboardScreen
import com.mobilecomputing.myfinance.screens.entries.EntriesScreen
import com.mobilecomputing.myfinance.screens.export_data.ExportDataScreen
import com.mobilecomputing.myfinance.screens.export_data.ExportDataScreen
import com.mobilecomputing.myfinance.screens.reminders.RemindersScreen
import com.mobilecomputing.myfinance.screens.add_reminder.AddReminderScreen
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
            AddEntryScreen(navigateBack = { navController.popBackStack() })
        }
        composable(Screen.EditEntry.route) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getString("entryId")
            AddEntryScreen(
                navigateBack = { navController.popBackStack() },
                entryId = entryId
            )
        }
        composable(Screen.Entries.route) {
            EntriesScreen(
                onAddEntryClick = { navController.navigate(Screen.AddEntry.route) },
                onEntryClick = { entryId -> 
                    navController.navigate("edit_entry/$entryId")
                }
            )
        }
        composable(Screen.Contracts.route) {
            ContractsScreen(
                onAddContractClick = { navController.navigate(Screen.AddContract.route) },
                onContractClick = { contractId ->
                    navController.navigate("edit_contract/$contractId")
                }
            )
        }
        composable(Screen.AddContract.route) {
            AddContractScreen(navigateBack = { navController.popBackStack() })
        }
        composable(Screen.EditContract.route) { backStackEntry ->
            val contractId = backStackEntry.arguments?.getString("contractId")
            AddContractScreen(
                navigateBack = { navController.popBackStack() },
                contractId = contractId
            )
        }
        composable(Screen.Analysis.route) {
            AnalysisScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(Screen.Reminders.route) {
            RemindersScreen(
                onAddReminderClick = { navController.navigate(Screen.AddReminder.route) }
            )
        }
        composable(Screen.AddReminder.route) {
             AddReminderScreen(
                 navigateBack = { navController.popBackStack() }
             )
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
