package com.mobilecomputing.myfinance.screens.analysis

import com.mobilecomputing.myfinance.MainDispatcherRule
import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.contract.PaymentCycle
import com.mobilecomputing.myfinance.data.entry.Entry
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.data.service.ContractService
import com.mobilecomputing.myfinance.data.service.EntryService
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

class AnalysisViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var entryService: EntryService
    private lateinit var contractService: ContractService
    private lateinit var viewModel: AnalysisViewModel

    private val entriesFlow = MutableStateFlow<List<Entry>>(emptyList())
    private val contractsFlow = MutableStateFlow<List<Contract>>(emptyList())

    @Before
    fun setUp() {
        entryService = mockk()
        contractService = mockk()

        every { entryService.getAllEntries() } returns entriesFlow
        every { contractService.getAllContracts() } returns contractsFlow

        every { contractService.getTotalMonthlyCost(any()) } answers
                {
                    val list = firstArg<List<Contract>>()
                    list
                        .filter {
                            it.type == ContractType.EXPENSE || it.type == ContractType.DEBT
                        }
                        .sumOf {
                            it.amount
                        }
                }
        every { contractService.getTotalMonthlyIncome(any()) } answers
                {
                    val list = firstArg<List<Contract>>()
                    list.filter { it.type == ContractType.INCOME }.sumOf { it.amount }
                }

        viewModel = AnalysisViewModel(entryService, contractService)
    }

    @Test
    fun uiState_calculatesCurrentMonthSpending() = runTest {
        // Entry in current month
        val entry1 = Entry(id = "1", amount = 100.0, type = EntryType.EXPENSE, date = Date())

        entriesFlow.value = listOf(entry1)
        contractsFlow.value = emptyList()

        // Wait for update
        val state = viewModel.uiState.first { it.currentMonthSpending > 0.0 }

        assertEquals(100.0, state.currentMonthSpending, 0.01)
    }

    @Test
    fun uiState_calculatesTotalMonthlyEarnings() = runTest {
        // Contract Income + Entry Income
        val contract =
            Contract(
                id = "1",
                amount = 500.0,
                type = ContractType.INCOME,
                paymentCycle = PaymentCycle.MONTHLY
            )
        val entry =
            Entry(
                id = "2",
                amount = 100.0,
                type = EntryType.INCOME,
                date = Date()
            ) // Current month

        contractsFlow.value = listOf(contract)
        entriesFlow.value = listOf(entry)

        val state = viewModel.uiState.first { it.totalMonthlyEarnings > 0.0 }

        // 500 + 100 = 600
        assertEquals(600.0, state.totalMonthlyEarnings, 0.01)
    }
}
