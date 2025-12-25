package com.mobilecomputing.myfinance.screens.dashboard

import com.mobilecomputing.myfinance.MainDispatcherRule
import com.mobilecomputing.myfinance.data.category.Category
import com.mobilecomputing.myfinance.data.entry.Entry
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
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

class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var entryService: EntryService
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var viewModel: DashboardViewModel

    private val entriesFlow = MutableStateFlow<List<Entry>>(emptyList())
    private val categoriesFlow = MutableStateFlow<List<Category>>(emptyList())

    @Before
    fun setUp() {
        entryService = mockk()
        categoryRepository = mockk()

        every { entryService.getAllEntries() } returns entriesFlow
        every { categoryRepository.getAllCategories() } returns categoriesFlow

        viewModel = DashboardViewModel(entryService, categoryRepository)
    }

    @Test
    fun uiState_calculatesTotalsCorrectly() = runTest {
        val entry1 = Entry(id = "1", amount = 100.0, type = EntryType.INCOME, date = Date())
        val entry2 = Entry(id = "2", amount = 50.0, type = EntryType.EXPENSE, date = Date())

        entriesFlow.value = listOf(entry1, entry2)

        val state = viewModel.uiState.value

        val currentState =
            viewModel.uiState.first { it.totalIncome > 0.0 || it.transactions.isEmpty() }
        
        val loadedState = viewModel.uiState.first { it.transactions.isNotEmpty() }

        assertEquals(100.0, loadedState.totalIncome, 0.01)
        assertEquals(50.0, loadedState.totalExpenses, 0.01)
        assertEquals(50.0, loadedState.netGrowth, 0.01)
    }

    @Test
    fun uiState_mapsTransactionsWithCategories() = runTest {
        val category = Category(id = "cat1", title = "Food")
        val entry =
            Entry(
                id = "1",
                amount = 20.0,
                type = EntryType.EXPENSE,
                categoryId = "cat1",
                date = Date()
            )

        categoriesFlow.value = listOf(category)
        entriesFlow.value = listOf(entry)

        val loadedState = viewModel.uiState.first { it.transactions.isNotEmpty() }

        assertEquals("Food", loadedState.transactions[0].categoryName)
        assertEquals(20.0, loadedState.transactions[0].amount, 0.01)
    }
}
