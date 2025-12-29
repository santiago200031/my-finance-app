package com.mobilecomputing.myfinance.screens.contracts.data

import com.mobilecomputing.myfinance.data.contract.ContractFilter
import com.mobilecomputing.myfinance.data.models.User
import java.util.Date

data class ContractsData(
    val userId: String?,
    val filter: ContractFilter,
    val selectedDate: Date,
    val currentUser: User?
)
