package com.mobilecomputing.myfinance.data.service

import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractStatus
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.contract.PaymentCycle
import com.mobilecomputing.myfinance.data.repository.ContractRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date

class ContractService(private val contractRepository: ContractRepository) {

    private val expiringThresholdDays = 30L

    fun getAllContracts(): Flow<List<Contract>> = contractRepository.getAllContracts()

    fun getContractsForUser(userId: String): Flow<List<Contract>> =
            contractRepository.getContractsForUser(userId)

    fun getContractById(id: String): Flow<Contract?> = contractRepository.getContractById(id)

    suspend fun addContract(contract: Contract) {
        contractRepository.addContract(contract)
    }

    suspend fun updateContract(contract: Contract) {
        contractRepository.updateContract(contract)
    }

    suspend fun deleteContract(id: String) {
        contractRepository.deleteContract(id)
    }

    fun getActiveCount(contracts: List<Contract>): Int {
        return contracts.count { it.status == ContractStatus.ACTIVE && !isExpiring(it) }
    }

    fun getExpiringCount(contracts: List<Contract>): Int {
        return contracts.count { it.status == ContractStatus.ACTIVE && isExpiring(it) }
    }

    fun getNetMonthlyValue(contracts: List<Contract>): Double {
        val income =
                contracts
                        .filter {
                            it.type == ContractType.INCOME && it.status == ContractStatus.ACTIVE
                        }
                        .sumOf { getMonthlyAmount(it) }
        val expenses =
                contracts
                        .filter {
                            it.type != ContractType.INCOME && it.status == ContractStatus.ACTIVE
                        }
                        .sumOf { getMonthlyAmount(it) }
        return income - expenses
    }

    fun getTotalMonthlyIncome(contracts: List<Contract>): Double {
        return contracts
                .filter { it.type == ContractType.INCOME && it.status == ContractStatus.ACTIVE }
                .sumOf { getMonthlyAmount(it) }
    }

    fun getTotalMonthlyCost(contracts: List<Contract>): Double {
        return contracts
                .filter {
                    (it.type == ContractType.EXPENSE || it.type == ContractType.DEBT) &&
                            it.status == ContractStatus.ACTIVE
                }
                .sumOf { getMonthlyAmount(it) }
    }

    private fun getMonthlyAmount(contract: Contract): Double {
        return when (contract.paymentCycle) {
            PaymentCycle.MONTHLY -> contract.amount
            PaymentCycle.YEARLY -> contract.amount / 12.0
            PaymentCycle.QUARTERLY -> contract.amount / 3.0
            PaymentCycle.WEEKLY -> contract.amount * 4.33
        }
    }

    private fun isExpiring(contract: Contract): Boolean {
        val endDate = contract.endDate ?: return false
        val daysUntil =
                ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                )
        return daysUntil in 0..expiringThresholdDays
    }

    fun checkContractStatus(contract: Contract): Contract {
        if (contract.endDate != null) {
            val endDate = contract.endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            if (endDate.isBefore(LocalDate.now())) {
                return if (contract.autoRenewEnabled) {
                    val monthsToAdd =
                            when (contract.paymentCycle) {
                                PaymentCycle.MONTHLY -> 1L
                                PaymentCycle.QUARTERLY -> 3L
                                PaymentCycle.YEARLY -> 12L
                                PaymentCycle.WEEKLY -> 0L
                            }

                    val newEndDate =
                            if (contract.paymentCycle == PaymentCycle.WEEKLY) {
                                endDate.plusWeeks(1)
                            } else {
                                endDate.plusMonths(monthsToAdd)
                            }

                    val newNextPaymentDate =
                            if (contract.paymentCycle == PaymentCycle.WEEKLY) {
                                contract.nextPaymentDate
                                        .toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                        .plusWeeks(1)
                            } else {
                                contract.nextPaymentDate
                                        .toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                        .plusMonths(monthsToAdd)
                            }

                    contract.copy(
                            endDate =
                                    Date.from(
                                            newEndDate
                                                    .atStartOfDay(ZoneId.systemDefault())
                                                    .toInstant()
                                    ),
                            nextPaymentDate =
                                    Date.from(
                                            newNextPaymentDate
                                                    .atStartOfDay(ZoneId.systemDefault())
                                                    .toInstant()
                                    )
                    )
                } else {
                    contract.copy(status = ContractStatus.CANCELLED)
                }
            }
        }
        return contract
    }
}
