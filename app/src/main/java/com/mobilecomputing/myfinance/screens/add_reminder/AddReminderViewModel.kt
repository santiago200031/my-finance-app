package com.mobilecomputing.myfinance.screens.add_reminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.reminder.Reminder
import com.mobilecomputing.myfinance.data.repository.ReminderRepository
import com.mobilecomputing.myfinance.data.service.ContractService
import com.mobilecomputing.myfinance.utils.NotificationHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class AddReminderViewModel(
    application: Application,
    private val reminderRepository: ReminderRepository,
    private val contractService: ContractService
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AddReminderUiState())
    val uiState: StateFlow<AddReminderUiState> = _uiState.asStateFlow()

    init {
        loadContracts()
    }

    private fun loadContracts() {
        viewModelScope.launch {
            val contracts = contractService.getAllContracts().first()
            _uiState.update { it.copy(availableContracts = contracts) }
        }
    }

    fun onContractSelect(contract: Contract) {
        _uiState.update { it.copy(selectedContract = contract) }
    }

    fun onDateChange(date: String) {
        _uiState.update { it.copy(reminderDate = date) }
    }

    fun saveReminder() {
        val currentState = _uiState.value
        val contract = currentState.selectedContract ?: return

        val parsedDate =
            try {
                val parts = currentState.reminderDate.split(".")
                LocalDate.of(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
            } catch (_: Exception) {
                LocalDate.now().plusDays(1)
            }

        val date = Date.from(parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val reminder = Reminder(contractId = contract.id, reminderDate = date)

        viewModelScope.launch {
            reminderRepository.addReminder(reminder)

            val notificationTime = parsedDate.atTime(9, 0)
            val zoneId = ZoneId.systemDefault()
            val epochMillis = notificationTime.atZone(zoneId).toInstant().toEpochMilli()

            NotificationHandler.scheduleNotification(
                getApplication(),
                reminder.id,
                epochMillis,
                "Contract Reminder: ${contract.title}",
                "Don't forget about your ${contract.title} contract!"
            )

            _uiState.update { it.copy(isSaved = true, showNotification = true) }
        }
    }

    fun resetSaveState() {
        _uiState.update { it.copy(isSaved = false, showNotification = false) }
    }
}

data class AddReminderUiState(
    val availableContracts: List<Contract> = emptyList(),
    val selectedContract: Contract? = null,
    val reminderDate: String = "",
    val isSaved: Boolean = false,
    val showNotification: Boolean = false
)
