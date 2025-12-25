package com.mobilecomputing.myfinance.screens.add_entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.category.Category
import com.mobilecomputing.myfinance.data.entry.Entry
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
import com.mobilecomputing.myfinance.data.service.EntryService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

data class AddEntryUiState(
    val amount: String = "",
    val description: String = "",
    val selectedCategory: Category? = null,
    val selectedType: EntryType = EntryType.EXPENSE,
    val categories: List<Category> = emptyList(),
    val isSaved: Boolean = false,
    val entryId: String? = null
)

class AddEntryViewModel(
    private val entryService: EntryService,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEntryUiState())
    val uiState: StateFlow<AddEntryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }

                if (_uiState.value.selectedCategory == null && categories.isNotEmpty()) {
                    _uiState.update { it.copy(selectedCategory = categories.first()) }
                }
            }
        }
    }

    fun onAmountChange(newAmount: String) {
        _uiState.update { it.copy(amount = newAmount) }
    }

    fun onDescriptionChange(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun onCategorySelect(category: Category) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onTypeSelect(type: EntryType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun loadEntry(entryId: String) {
        viewModelScope.launch {
            val entry = entryService.getEntryById(entryId).first()
            if (entry != null) {
                _uiState.update {
                    it.copy(
                        amount = entry.amount.toString(),
                        description = entry.description ?: "",
                        selectedCategory =
                            _uiState.value.categories.find { cat ->
                                cat.id == entry.categoryId
                            },
                        selectedType = entry.type,
                        entryId = entry.id
                    )
                }
            }
        }
    }

    fun saveEntry() {
        val currentState = _uiState.value
        val amountValue = currentState.amount.toDoubleOrNull()

        if (amountValue != null && currentState.selectedCategory != null) {
            viewModelScope.launch {
                val transactionId = currentState.entryId ?: UUID.randomUUID().toString()

                val newEntry =
                    Entry(
                        id = transactionId,
                        amount = amountValue,
                        description = currentState.description,
                        categoryId = currentState.selectedCategory.id,
                        type = currentState.selectedType,
                        date = Date()
                    )

                if (currentState.entryId != null) {
                    entryService.updateEntry(newEntry)
                } else {
                    entryService.addEntry(newEntry)
                }
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }

    fun deleteEntry() {
        val entryId = _uiState.value.entryId
        if (entryId != null) {
            viewModelScope.launch {
                entryService.deleteEntry(entryId)
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }

    fun resetSaveState() {
        _uiState.update { it.copy(isSaved = false) }
    }
}
