package com.mobilecomputing.myfinance.data.repository.impl

import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractStatus
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.contract.PaymentCycle
import com.mobilecomputing.myfinance.data.repository.ContractRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class FakeContractRepository : ContractRepository {
        private fun localDateToDate(localDate: LocalDate): Date {
                return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        }

        private val _contracts =
                MutableStateFlow<List<Contract>>(
                        listOf(
                                // 1. Auto-Renew Test: Expired yesterday, should renew today/startup
                                Contract(
                                        id = "1",
                                        title = "Netflix (AutoRenew)",
                                        amount = 12.99,
                                        paymentCycle = PaymentCycle.MONTHLY,
                                        startDate = localDateToDate(LocalDate.now().minusMonths(6)),
                                        nextPaymentDate =
                                                localDateToDate(
                                                        LocalDate.now().minusDays(1)
                                                ), // Past due
                                        endDate =
                                                localDateToDate(
                                                        LocalDate.now().minusDays(1)
                                                ), // Expired yesterday
                                        autoRenewEnabled = true,
                                        status = ContractStatus.ACTIVE,
                                        type = ContractType.EXPENSE,
                                        totalAmount = null,
                                        userId = "s-svilla"
                                ),
                                // 2. Expiring Soon Test: Expires tomorrow
                                Contract(
                                        id = "2",
                                        title = "Gym (Expiring Soon)",
                                        amount = 45.00,
                                        paymentCycle = PaymentCycle.MONTHLY,
                                        startDate =
                                                localDateToDate(LocalDate.now().minusMonths(12)),
                                        nextPaymentDate =
                                                localDateToDate(LocalDate.now().plusDays(1)),
                                        endDate = localDateToDate(LocalDate.now().plusDays(1)),
                                        autoRenewEnabled = false,
                                        status = ContractStatus.ACTIVE,
                                        type = ContractType.EXPENSE,
                                        totalAmount = null,
                                        userId = "s-svilla"
                                ),
                                // 3. Expired Test: Expired yesterday, check status update to
                                // EXPIRED
                                Contract(
                                        id = "3",
                                        title = "Magazine (Expired)",
                                        amount = 5.00,
                                        paymentCycle = PaymentCycle.YEARLY,
                                        startDate = localDateToDate(LocalDate.now().minusYears(1)),
                                        nextPaymentDate =
                                                localDateToDate(LocalDate.now().minusDays(1)),
                                        endDate = localDateToDate(LocalDate.now().minusDays(1)),
                                        autoRenewEnabled = false,
                                        // EXPIRED
                                        type = ContractType.EXPENSE,
                                        totalAmount = null,
                                        userId = "s-svilla"
                                ),
                                // 4. Cancelled
                                Contract(
                                        id = "4",
                                        title = "Old Loan",
                                        amount = 100.00,
                                        paymentCycle = PaymentCycle.MONTHLY,
                                        startDate = localDateToDate(LocalDate.now().minusYears(2)),
                                        nextPaymentDate = localDateToDate(LocalDate.now()),
                                        endDate = localDateToDate(LocalDate.now().minusMonths(1)),
                                        status = ContractStatus.CANCELLED,
                                        type = ContractType.DEBT,
                                        totalAmount = 2400.0,
                                        userId = "villavicencioandrs"
                                )
                        )
                )

        override fun getAllContracts(): Flow<List<Contract>> = _contracts

        override fun getContractsForUser(userId: String): Flow<List<Contract>> {
                return _contracts.map { list -> list.filter { it.userId == userId } }
        }

        override fun getContractById(id: String): Flow<Contract?> =
                _contracts.map { list -> list.find { it.id == id } }

        override suspend fun addContract(contract: Contract) {
                _contracts.update { it + contract }
        }

        override suspend fun updateContract(contract: Contract) {
                _contracts.update { list ->
                        list.map { if (it.id == contract.id) contract else it }
                }
        }

        override suspend fun deleteContract(id: String) {
                _contracts.update { list -> list.filter { it.id != id } }
        }
}
