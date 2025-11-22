package com.mobilecomputing.myfinance.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.IncompleteCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NavigationBurgerMenu(
    onRemindersClick: () -> Unit,
    onBudgetPlanningClick: () -> Unit,
    onExportDataClick: () -> Unit,
    onSharingSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier) {
        Spacer(modifier = Modifier.height(8.dp))

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Alarm,
                    contentDescription = "Reminders"
                )
            },
            label = { Text("Reminders") },
            selected = false,
            onClick = onRemindersClick,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.IncompleteCircle,
                    contentDescription = "Budget Planning"
                )
            },
            label = { Text("Budget Planning") },
            selected = false,
            onClick = onBudgetPlanningClick,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.ImportExport,
                    contentDescription = "Export Data"
                )
            },
            label = { Text("Export Data") },
            selected = false,
            onClick = onExportDataClick,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Sharing Settings"
                )
            },
            label = { Text("Sharing Settings") },
            selected = false,
            onClick = onSharingSettingsClick,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}
