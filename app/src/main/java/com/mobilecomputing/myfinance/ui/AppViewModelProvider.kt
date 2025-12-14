package com.mobilecomputing.myfinance.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mobilecomputing.myfinance.MyFinanceApplication
import com.mobilecomputing.myfinance.screens.add_contract.AddContractViewModel
import com.mobilecomputing.myfinance.screens.add_entry.AddEntryViewModel
import com.mobilecomputing.myfinance.screens.add_reminder.AddReminderViewModel
import com.mobilecomputing.myfinance.screens.analysis.AnalysisViewModel
import com.mobilecomputing.myfinance.screens.contracts.ContractsViewModel
import com.mobilecomputing.myfinance.screens.dashboard.DashboardViewModel
import com.mobilecomputing.myfinance.screens.entries.EntriesViewModel
import com.mobilecomputing.myfinance.screens.reminders.RemindersViewModel
import com.mobilecomputing.myfinance.screens.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DashboardViewModel(
                    myFinanceApplication().container.entryRepository,
                    myFinanceApplication().container.categoryRepository
            )
        }
        initializer {
            EntriesViewModel(
                    myFinanceApplication().container.entryRepository,
                    myFinanceApplication().container.categoryRepository
            )
        }
        initializer {
            AddEntryViewModel(
                    myFinanceApplication().container.entryRepository,
                    myFinanceApplication().container.categoryRepository
            )
        }
        initializer {
            ContractsViewModel(
                    myFinanceApplication().container.contractRepository,
                    myFinanceApplication().container.contractService,
                    myFinanceApplication().container.userRepository
            )
        }
        initializer {
            AddContractViewModel(
                    myFinanceApplication().container.contractRepository,
                    myFinanceApplication().container.contractService,
                    myFinanceApplication().container.reminderRepository,
                    myFinanceApplication().container.userRepository
            )
        }
        initializer {
            RemindersViewModel(
                    myFinanceApplication().container.reminderRepository,
                    myFinanceApplication().container.contractRepository
            )
        }
        initializer {
            AddReminderViewModel(
                    myFinanceApplication().container.reminderRepository,
                    myFinanceApplication().container.contractRepository
            )
        }
        initializer {
            AnalysisViewModel(
                    myFinanceApplication().container.entryRepository,
                    myFinanceApplication().container.contractRepository,
                    myFinanceApplication().container.contractService
            )
        }
        initializer { SettingsViewModel(myFinanceApplication().container.userRepository) }
    }
}

fun CreationExtras.myFinanceApplication(): MyFinanceApplication =
        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyFinanceApplication)
