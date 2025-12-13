package com.mobilecomputing.myfinance.domain

import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractStatus
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.contract.PaymentCycle
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date

class ContractService {

    private val expiringThresholdDays = 30L

    fun getActiveCount(contracts: List<Contract>): Int {
        return contracts.count { it.status == ContractStatus.ACTIVE && !isExpiring(it) }
    }

    fun getExpiringCount(contracts: List<Contract>): Int {
        return contracts.count { isExpiring(it) }
    }

    fun getTotalMonthlyCost(contracts: List<Contract>): Double {
        return contracts
                .filter { it.status == ContractStatus.ACTIVE && it.type != ContractType.INCOME }
                .sumOf { calculateMonthlyCost(it) }
    }

    fun getTotalMonthlyIncome(contracts: List<Contract>): Double {
        return contracts
                .filter { it.status == ContractStatus.ACTIVE && it.type == ContractType.INCOME }
                .sumOf { calculateMonthlyCost(it) }
    }

    fun getNetMonthlyValue(contracts: List<Contract>): Double {
        val income = getTotalMonthlyIncome(contracts)
        val cost = getTotalMonthlyCost(contracts)
        return income - cost
    }

    fun isExpiring(contract: Contract): Boolean {
        if (contract.status != ContractStatus.ACTIVE || contract.endDate == null) return false
        val daysUntilExpiration =
                ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        contract.endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                )
        return daysUntilExpiration in 0..expiringThresholdDays
    }

    fun checkContractStatus(contract: Contract): Contract {
        if (contract.status != ContractStatus.ACTIVE || contract.endDate == null) {
            return contract
        }

        val today = LocalDate.now()

        if (today.isAfter(contract.endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
        ) {
            return if (contract.autoRenewEnabled) {
                val monthsToAdd =
                        when (contract.paymentCycle) {
                            PaymentCycle.MONTHLY -> 1L
                            PaymentCycle.QUARTERLY -> 3L
                            PaymentCycle.YEARLY -> 12L
                        }
                val newEndDate =
                        contract.endDate
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .plusMonths(monthsToAdd)
                val newNextPaymentDate =
                        contract.nextPaymentDate
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .plusMonths(monthsToAdd)
                contract.copy(
                        endDate =
                                Date.from(
                                        newEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                ),
                        nextPaymentDate =
                                Date.from(
                                        newNextPaymentDate
                                                .atStartOfDay(ZoneId.systemDefault())
                                                .toInstant()
                                )
                )
            } else {
                contract.copy(status = ContractStatus.OUTDATED)
            }
        }
        return contract
    }

    private fun calculateMonthlyCost(contract: Contract): Double {
        return when (contract.paymentCycle) {
            PaymentCycle.MONTHLY -> contract.amount
            PaymentCycle.QUARTERLY -> contract.amount / 3
            PaymentCycle.YEARLY -> contract.amount / 12
        }
    }
}
