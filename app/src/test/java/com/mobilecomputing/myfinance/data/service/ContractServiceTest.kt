package com.mobilecomputing.myfinance.data.service

import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractStatus
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.contract.PaymentCycle
import com.mobilecomputing.myfinance.data.repository.ContractRepository
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class ContractServiceTest {

    private lateinit var contractRepository: ContractRepository
    private lateinit var contractService: ContractService

    @Before
    fun setUp() {
        contractRepository = mockk()
        contractService = ContractService(contractRepository)
    }

    @Test
    fun getActiveCount_returnsCorrectCount() {
        val contracts =
            listOf(
                createContract(status = ContractStatus.ACTIVE),
                createContract(status = ContractStatus.ACTIVE),
                createContract(status = ContractStatus.CANCELLED)
            )
        val count = contractService.getActiveCount(contracts)
        assertEquals(2, count)
    }

    @Test
    fun getExpiringCount_returnsCorrectCount() {
        // Expiring in 10 days
        val expiringDate =
            Date.from(
                LocalDate.now()
                    .plusDays(10)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
            )
        // Not expiring (60 days)
        val notExpiringDate =
            Date.from(
                LocalDate.now()
                    .plusDays(60)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
            )

        val contracts =
            listOf(
                createContract(
                    status = ContractStatus.ACTIVE,
                    endDate = expiringDate
                ),
                createContract(
                    status = ContractStatus.ACTIVE,
                    endDate = notExpiringDate
                ),
                createContract(
                    status = ContractStatus.CANCELLED,
                    endDate = expiringDate
                ) // Cancelled shouldn't count
            )

        val count = contractService.getExpiringCount(contracts)
        assertEquals(1, count)
    }

    @Test
    fun getNetMonthlyValue_returnsIncomeMinusExpenses() {
        val contracts =
            listOf(
                createContract(
                    type = ContractType.INCOME,
                    amount = 100.0,
                    paymentCycle = PaymentCycle.MONTHLY
                ), // +100
                createContract(
                    type = ContractType.EXPENSE,
                    amount = 50.0,
                    paymentCycle = PaymentCycle.MONTHLY
                ), // -50
                createContract(
                    type = ContractType.DEBT,
                    amount = 20.0,
                    paymentCycle = PaymentCycle.MONTHLY
                ) // -20
            )
        val net = contractService.getNetMonthlyValue(contracts)
        assertEquals(30.0, net, 0.01)
    }

    @Test
    fun getTotalMonthlyIncome_returnsOnlyIncome() {
        val contracts =
            listOf(
                createContract(
                    type = ContractType.INCOME,
                    amount = 100.0,
                    paymentCycle = PaymentCycle.MONTHLY
                ),
                createContract(
                    type = ContractType.EXPENSE,
                    amount = 50.0,
                    paymentCycle = PaymentCycle.MONTHLY
                )
            )
        val income = contractService.getTotalMonthlyIncome(contracts)
        assertEquals(100.0, income, 0.01)
    }

    @Test
    fun getTotalMonthlyCost_returnsExpensesAndDebt() {
        val contracts =
            listOf(
                createContract(
                    type = ContractType.INCOME,
                    amount = 100.0,
                    paymentCycle = PaymentCycle.MONTHLY
                ),
                createContract(
                    type = ContractType.EXPENSE,
                    amount = 50.0,
                    paymentCycle = PaymentCycle.MONTHLY
                ),
                createContract(
                    type = ContractType.DEBT,
                    amount = 20.0,
                    paymentCycle = PaymentCycle.MONTHLY
                )
            )
        val cost = contractService.getTotalMonthlyCost(contracts)
        assertEquals(70.0, cost, 0.01)
    }

    @Test
    fun checkContractStatus_autoRenew_updatesDates() {
        // End date was yesterday
        val yesterday =
            Date.from(
                LocalDate.now()
                    .minusDays(1)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
            )
        val nextPayment =
            Date.from(
                LocalDate.now()
                    .minusDays(1)
                    .plusMonths(1)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
            )

        val contract =
            createContract(
                status = ContractStatus.ACTIVE,
                endDate = yesterday,
                nextPaymentDate = nextPayment,
                autoRenewEnabled = true,
                paymentCycle = PaymentCycle.MONTHLY
            )

        val updated = contractService.checkContractStatus(contract)

        // Should have new end date +1 month
        val expectedEndDate = LocalDate.now().minusDays(1).plusMonths(1)
        val actualEndDate =
            updated.endDate!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

        assertEquals(expectedEndDate, actualEndDate)
    }

    @Test
    fun checkContractStatus_noAutoRenew_cancelsContract() {
        // End date was yesterday
        val yesterday =
            Date.from(
                LocalDate.now()
                    .minusDays(1)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
            )

        val contract =
            createContract(
                status = ContractStatus.ACTIVE,
                endDate = yesterday,
                autoRenewEnabled = false
            )

        val updated = contractService.checkContractStatus(contract)
        assertEquals(ContractStatus.CANCELLED, updated.status)
    }

    private fun createContract(
        id: String = "1",
        userId: String = "user1",
        name: String = "Test Contract",
        amount: Double = 10.0,
        type: ContractType = ContractType.EXPENSE,
        paymentCycle: PaymentCycle = PaymentCycle.MONTHLY,
        startDate: Date = Date(),
        endDate: Date? = null,
        nextPaymentDate: Date = Date(),
        status: ContractStatus = ContractStatus.ACTIVE,
        autoRenewEnabled: Boolean = false,
        notificationEnabled: Boolean = false
    ): Contract {
        return Contract(
            id = id,
            userId = userId,
            title = name,
            amount = amount,
            type = type,
            paymentCycle = paymentCycle,
            startDate = startDate,
            endDate = endDate,
            nextPaymentDate = nextPaymentDate,
            status = status,
            autoRenewEnabled = autoRenewEnabled,
            isReminderActive = notificationEnabled
        )
    }
}
