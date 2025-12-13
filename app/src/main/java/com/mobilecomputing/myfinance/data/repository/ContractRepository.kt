package com.mobilecomputing.myfinance.data.repository

import com.mobilecomputing.myfinance.data.contract.Contract
import kotlinx.coroutines.flow.Flow

interface ContractRepository {
    fun getAllContracts(): Flow<List<Contract>>
    fun getContractById(id: String): Flow<Contract?>
    suspend fun addContract(contract: Contract)
    suspend fun updateContract(contract: Contract)
    suspend fun deleteContract(id: String)
}
