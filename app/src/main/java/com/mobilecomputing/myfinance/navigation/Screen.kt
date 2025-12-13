package com.mobilecomputing.myfinance.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.IncompleteCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard)

    object Entries : Screen("entries", "Entries", Icons.Filled.Description)

    object AddEntry : Screen("add_entry", "Add Entry", Icons.Filled.Add)

    object EditEntry : Screen("edit_entry/{entryId}", "Edit Entry", Icons.Filled.Add)

    object Contracts : Screen("contracts", "Contracts", Icons.Filled.Description)

    object EditContract :
            Screen("edit_contract/{contractId}", "Edit Contract", Icons.Filled.Description)

    object AddContract : Screen("add_contract", "Add Contract", Icons.Filled.Add)

    object Analysis : Screen("analysis", "Analysis", Icons.Filled.Analytics)

    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)

    object Reminders : Screen("reminders", "Reminders", Icons.Filled.Alarm)

    object AddReminder : Screen("add_reminder", "Add Reminder", Icons.Filled.Alarm)

    object BudgetPlanning :
            Screen("budgetPlanning", "Budget Planning", Icons.Filled.IncompleteCircle)

    object ExportData : Screen("exportData", "Export Data", Icons.Filled.ImportExport)

    object SharingSettings : Screen("sharingSettings", "Sharing Settings", Icons.Filled.Share)
}
