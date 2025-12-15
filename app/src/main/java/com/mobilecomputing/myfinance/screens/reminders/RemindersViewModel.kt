package com.mobilecomputing.myfinance.screens.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.reminder.Reminder
import com.mobilecomputing.myfinance.data.repository.ReminderRepository
import com.mobilecomputing.myfinance.data.service.ContractService
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ReminderUiItem(
        val reminder: Reminder,
        val contractTitle: String,
        val contractAmount: Double,
        val daysUntil: Long
)

class RemindersViewModel(
        private val reminderRepository: ReminderRepository,
        private val contractService: ContractService
) : ViewModel() {

        private val _uiState = MutableStateFlow(RemindersUiState())

        val uiState: StateFlow<RemindersUiState> =
                combine(reminderRepository.getAllReminders(), contractService.getAllContracts()) {
                                reminders,
                                contracts ->
                                val activeReminders =
                                        reminders
                                                .mapNotNull { reminder ->
                                                        val contract =
                                                                contracts.find {
                                                                        it.id == reminder.contractId
                                                                }
                                                        if (contract != null) {
                                                                val daysUntil =
                                                                        ChronoUnit.DAYS.between(
                                                                                LocalDate.now(),
                                                                                reminder.reminderDate
                                                                        )
                                                                ReminderUiItem(
                                                                        reminder = reminder,
                                                                        contractTitle =
                                                                                contract.title,
                                                                        contractAmount =
                                                                                contract.amount,
                                                                        daysUntil = daysUntil
                                                                )
                                                        } else {
                                                                null
                                                        }
                                                }
                                                .sortedBy { it.daysUntil }

                                RemindersUiState(
                                        reminders = activeReminders,
                                        activeCount = activeReminders.size,
                                        nextAlertInDays = activeReminders.firstOrNull()?.daysUntil
                                )
                        }
                        .stateIn(
                                scope = viewModelScope,
                                started = SharingStarted.WhileSubscribed(5000),
                                initialValue = RemindersUiState()
                        )

        fun deleteReminder(id: String) {
                viewModelScope.launch { reminderRepository.deleteReminder(id) }
        }
}

data class RemindersUiState(
        val reminders: List<ReminderUiItem> = emptyList(),
        val activeCount: Int = 0,
        val nextAlertInDays: Long? = null
)
