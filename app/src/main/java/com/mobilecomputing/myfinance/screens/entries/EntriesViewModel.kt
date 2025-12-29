package com.mobilecomputing.myfinance.screens.entries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.entry.EntryFilter
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
import com.mobilecomputing.myfinance.data.repository.UserRepository
import com.mobilecomputing.myfinance.data.service.EntryService
import com.mobilecomputing.myfinance.ui.models.EntryUiModel
import com.mobilecomputing.myfinance.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.Date

data class EntriesUiState(
    val transactions: List<EntryUiModel> = emptyList(),
    val filter: EntryFilter = EntryFilter.ALL,
    val selectedDate: Date = Date()
)

class EntriesViewModel(
    entryService: EntryService,
    categoryRepository: CategoryRepository,
    userRepository: UserRepository
) : ViewModel() {

    // UI state holders
    private val _filter = MutableStateFlow(EntryFilter.ALL)
    private val _selectedDate = MutableStateFlow(Date())

    val uiState: StateFlow<EntriesUiState> =
        combine(
            entryService.getAllEntries(),
            categoryRepository.getAllCategories(),
            userRepository.getCurrentUser(),
            _filter,
            _selectedDate
        ) { entries, categories, user, filter, selectedDate ->
            // Filter and map
            val filteredEntries =
                entries.filter { entry ->
                    val matchesFilter =
                        when (filter) {
                            EntryFilter.ALL -> true
                            EntryFilter.INCOME -> entry.type == EntryType.INCOME
                            EntryFilter.EXPENSE ->
                                entry.type == EntryType.EXPENSE
                        }

                    val matchesDate =
                        DateUtils.isSameMonth(entry.date, selectedDate)

                    matchesFilter && matchesDate
                }

            val uiTransactions =
                filteredEntries.sortedByDescending { it.date }.map { entry ->
                    val category = categories.find { it.id == entry.categoryId }
                    EntryUiModel(
                        id = entry.id,
                        amount = entry.amount,
                        description = entry.description ?: "No Description",
                        date = entry.date,
                        categoryName = category?.title ?: "Uncategorized",
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

            EntriesUiState(
                transactions = uiTransactions,
                filter = filter,
                selectedDate = selectedDate
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = EntriesUiState()
            )

    fun onFilterChanged(filter: EntryFilter) {
        _filter.update { filter }
    }

    fun updateMonth(increment: Long) {
        _selectedDate.update { DateUtils.addMonths(it, increment) }
    }
}
