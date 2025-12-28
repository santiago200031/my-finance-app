package com.mobilecomputing.myfinance.screens.export_data

import com.mobilecomputing.myfinance.data.entry.Entry
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
import com.mobilecomputing.myfinance.data.repository.EntryRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class ExportViewModelTest {
    private val entryRepository = mockk<EntryRepository>()
    private val categoryRepository = mockk<CategoryRepository>()
    private lateinit var viewModel: ExportViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock data
        val now = Date()
        val entries =
            listOf(
                Entry(
                    id = "1",
                    amount = 100.0,
                    type = EntryType.INCOME,
                    date = now,
                    description = "Salary",
                    categoryId = "cat1"
                ),
                Entry(
                    id = "2",
                    amount = 50.0,
                    type = EntryType.EXPENSE,
                    date = now,
                    description = "Food",
                    categoryId = "cat2"
                )
            )

        val categories =
            listOf(
                com.mobilecomputing.myfinance.data.category.Category(
                    id = "cat1",
                    title = "Work"
                ),
                com.mobilecomputing.myfinance.data.category.Category(
                    id = "cat2",
                    title = "Groceries"
                )
            )

        coEvery { entryRepository.getAllEntries() } returns flowOf(entries)
        coEvery { categoryRepository.getAllCategories() } returns flowOf(categories)

        viewModel = ExportViewModel(entryRepository, categoryRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        val state = viewModel.uiState.first { it.entriesCount > 0 }
        assertEquals(ExportFormat.CSV, state.selectedFormat)
        assertEquals(DateRange.THIS_MONTH, state.selectedDateRange)
        assertEquals(2, state.entriesCount)
    }

    @Test
    fun `generateCsv writes correct content`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.uiState.first { it.filteredEntries.isNotEmpty() }

        val outputStream = ByteArrayOutputStream()

        viewModel.generateCsv(outputStream)

        val csvContent = outputStream.toString()
        val lines = csvContent.split("\n")

        assertEquals("Date,Description,Category,Type,Amount", lines[0].trim())

        // Verify content roughly
        assert(csvContent.contains("Salary"))
        assert(csvContent.contains("Food"))
        assert(csvContent.contains("Income"))
        assert(csvContent.contains("Expense"))
        // Verify Category Name Resolution
        assert(csvContent.contains("Work"))
        assert(csvContent.contains("Groceries"))
    }
}
