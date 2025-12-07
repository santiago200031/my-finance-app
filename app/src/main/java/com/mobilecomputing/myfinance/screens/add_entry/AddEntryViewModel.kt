package com.mobilecomputing.myfinance.screens.add_entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mobilecomputing.myfinance.data.models.category.Category
import com.mobilecomputing.myfinance.data.models.contract.Contract
import com.mobilecomputing.myfinance.data.models.debt.Debt
import com.mobilecomputing.myfinance.data.models.transaction.Transaction
import com.mobilecomputing.myfinance.data.models.transaction.TransactionType
import com.mobilecomputing.myfinance.data.services.AccountService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddEntryViewModel(private val accountService: AccountService = AccountService()) :
    ViewModel() {

    var uiState by mutableStateOf(AddEntryUiState())
        private set

    fun onTitleChanged(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun onAmountChanged(amount: String) {
        uiState = uiState.copy(amount = amount)
    }

    fun onTypeChanged(type: EntryType) {
        uiState = uiState.copy(type = type)
    }

    fun onCategoryChanged(categoryName: String) {
        uiState = uiState.copy(
            category = (uiState.category ?: Category())
                .copy(name = categoryName)
        )
    }

    fun onDateChanged(date: String) {
        uiState = uiState.copy(date = date)
    }

    fun onDescriptionChanged(description: String) {
        uiState = uiState.copy(description = description)
    }

    fun onProviderChanged(provider: String) {
        uiState = uiState.copy(provider = provider)
    }

    fun onSave() {
        val date = try {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(uiState.date)
        } catch (e: Exception) {
            Date()
        }

        when (uiState.type) {
            EntryType.INCOME -> {
                val transaction = Transaction(
                    amount = uiState.amount.toDouble(),
                    description = uiState.description,
                    date = date,
                    categoryId = uiState.category?.id ?: "",
                    categoryName = uiState.category?.name ?: "",
                    type = TransactionType.INCOME
                )
                accountService.addTransaction(transaction)
            }

            EntryType.EXPENSE -> {
                val transaction = Transaction(
                    amount = uiState.amount.toDouble(),
                    description = uiState.description,
                    date = date,
                    categoryId = uiState.category?.id ?: "",
                    categoryName = uiState.category?.name ?: "",
                    type = TransactionType.EXPENSE
                )
                accountService.addTransaction(transaction)
            }

            EntryType.DEBT -> {
                val debt = Debt(
                    totalAmount = uiState.amount.toDouble(),
                    creditor = uiState.title
                )
                accountService.addDebt(debt)
            }

            EntryType.CONTRACT -> {
                val contract = Contract(
                    title = uiState.title,
                    amount = uiState.amount.toDouble(),
                    provider = uiState.provider,
                    startDate = date,
                    paymentCycle = uiState.paymentCycle,
                    nextPaymentDate = date
                )
                accountService.addContract(contract)
            }
        }
    }
}