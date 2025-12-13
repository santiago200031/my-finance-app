package com.mobilecomputing.myfinance.screens.add_reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.reminder.Reminder
import com.mobilecomputing.myfinance.data.repository.ContractRepository
import com.mobilecomputing.myfinance.data.repository.ReminderRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddReminderViewModel(
        private val reminderRepository: ReminderRepository,
        private val contractRepository: ContractRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddReminderUiState())
    val uiState: StateFlow<AddReminderUiState> = _uiState.asStateFlow()

    init {
        loadContracts()
    }

    private fun loadContracts() {
        viewModelScope.launch {
            val contracts = contractRepository.getAllContracts().first()
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
                } catch (e: Exception) {
                    LocalDate.now().plusDays(1) // Default or error fallback
                }

        val reminder = Reminder(contractId = contract.id, reminderDate = parsedDate)

        viewModelScope.launch {
            reminderRepository.addReminder(reminder)
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
