package com.mobilecomputing.myfinance.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
import com.mobilecomputing.myfinance.data.repository.UserRepository
import com.mobilecomputing.myfinance.data.service.ContractService
import com.mobilecomputing.myfinance.data.service.EntryService
import com.mobilecomputing.myfinance.ui.models.EntryUiModel
import com.mobilecomputing.myfinance.utils.DateUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

data class DashboardUiState(
    val transactions: List<EntryUiModel> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netGrowth: Double = 0.0,
    val currency: String = "EUR (€)"
)

class DashboardViewModel(
    entryService: EntryService,
    categoryRepository: CategoryRepository,
    userRepository: UserRepository,
    contractService: ContractService,
    sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(5000)
) : ViewModel() {

    private val _notifications = Channel<String>()
    val notifications = _notifications.receiveAsFlow()

    private val notifiedContractIds = mutableSetOf<String>()

    init {
        contractService
            .getAllContracts()
            .distinctUntilChanged()
            .onEach { contracts ->
                val today = LocalDate.now()
                contracts.forEach { contract ->
                    val nextPayment =
                        contract.nextPaymentDate
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                    if (nextPayment.isEqual(today) && !notifiedContractIds.contains(contract.id)
                    ) {
                        notifiedContractIds.add(contract.id)
                        viewModelScope.launch {
                            _notifications.send(
                                "Today is the next payment for your contract ${contract.title}"
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    val uiState: StateFlow<DashboardUiState> =
        combine(
            entryService.getAllEntries(),
            categoryRepository.getAllCategories(),
            userRepository.getCurrentUser()
        ) { entries, categories, user ->
            val totalIncome = entryService.calculateTotalIncome(entries)
            val totalExpenses = entryService.calculateTotalExpenses(entries)
            val netGrowth = entryService.calculateNetGrowth(entries)

            val uiTransactions =
                entries
                    .sortedByDescending { it.date }
                    .map { entry ->
                        val category =
                            categories.find { it.id == entry.categoryId }
                        EntryUiModel(
                            id = entry.id,
                            amount = entry.amount,
                            description = entry.description
                                ?: "No Description",
                            date = entry.date,
                            categoryName = category?.title
                                ?: "Uncategorized",
                            type = entry.type,
                            categoryId = entry.categoryId,
                            formattedDate =
                                DateUtils.formatDate(
                                    entry.date,
                                    user?.settings?.dateFormat
                                        ?: "dd.MM.yyyy"
                                ),
                            currency = user?.settings?.currency ?: "EUR (€)"
                        )
                    }
                    .take(3)

            DashboardUiState(
                transactions = uiTransactions,
                totalIncome = totalIncome,
                totalExpenses = totalExpenses,
                netGrowth = netGrowth,
                currency = user?.settings?.currency ?: "EUR (€)"
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = sharingStarted,
                initialValue = DashboardUiState()
            )
}
