package com.mobilecomputing.myfinance.screens.contracts.data

import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractFilter
import java.util.Date

data class ContractsUiState(
    val contracts: List<Contract> = emptyList(),
    val filter: ContractFilter = ContractFilter.ALL,
    val selectedDate: Date = Date(),
    val activeCount: Int = 0,
    val expiringCount: Int = 0,
    val monthlyNetValue: Double = 0.0,
    val currency: String = "EUR (€)",
    val dateFormat: String = "dd.MM.yyyy"
)
