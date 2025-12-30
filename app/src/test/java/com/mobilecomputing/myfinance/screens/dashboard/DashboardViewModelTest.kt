package com.mobilecomputing.myfinance.screens.dashboard

import com.mobilecomputing.myfinance.MainDispatcherRule
import com.mobilecomputing.myfinance.data.category.Category
import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.entry.Entry
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.data.models.User
import com.mobilecomputing.myfinance.data.models.user.UserSettings
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
import com.mobilecomputing.myfinance.data.repository.UserRepository
import com.mobilecomputing.myfinance.data.service.ContractService
import com.mobilecomputing.myfinance.data.service.EntryService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val entryService: EntryService = mockk()
    private val categoryRepository: CategoryRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val contractService: ContractService = mockk()

    @Before
    fun setUp() {
        val userSettings = UserSettings("EUR (€)", "dd.MM.yyyy")
        val testUser =
            User(
                "s-svilla",
                "s-svilla@haw-landshut.de",
                "Santiago",
                "Villavicencio",
                null,
                emptyList(),
                userSettings
            )
        every { userRepository.getCurrentUser() } returns kotlinx.coroutines.flow.flowOf(testUser)
        every { contractService.getAllContracts() } returns flowOf(emptyList())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `uiState emits correct data when entries and categories are loaded`() = runTest {
        // Arrange
        val now = Instant.now()
        val date1 = Date.from(now)
        val date2 = Date.from(now.minus(5, ChronoUnit.DAYS))
        val date3 = Date.from(now.minus(10, ChronoUnit.DAYS))
        val date4 = Date.from(now.minus(15, ChronoUnit.DAYS))

        val category =
            Category(
                id = "cat1",
                title = "Food",
                iconKey = "food_icon",
                colorHex = "#000000",
                type = ContractType.EXPENSE
            )
        val category2 =
            Category(
                id = "cat2",
                title = "Salary",
                iconKey = "salary_icon",
                colorHex = "#000000",
                type = ContractType.INCOME
            )

        val entries =
            listOf(
                Entry(
                    id = "1",
                    amount = 100.0,
                    description = "Lunch",
                    date = date1,
                    type = EntryType.EXPENSE,
                    categoryId = "cat1"
                ),
                Entry(
                    id = "2",
                    amount = 500.0,
                    description = "Salary",
                    date = date2,
                    type = EntryType.INCOME,
                    categoryId = "cat2"
                ),
                Entry(
                    id = "3",
                    amount = 50.0,
                    description = "Dinner",
                    date = date3,
                    type = EntryType.EXPENSE,
                    categoryId = "cat1"
                ),
                Entry(
                    id = "4",
                    amount = 20.0,
                    description = "Snack",
                    date = date4,
                    type = EntryType.EXPENSE,
                    categoryId = "cat1"
                )
            )

        val categories = listOf(category, category2)

        coEvery { entryService.getAllEntries() } returns flowOf(entries)
        coEvery { categoryRepository.getAllCategories() } returns flowOf(categories)

        // Mock calculations
        every { entryService.calculateTotalIncome(any()) } returns 500.0
        every { entryService.calculateTotalExpenses(any()) } returns 170.0
        every { entryService.calculateNetGrowth(any()) } returns 330.0

        val viewModel =
            DashboardViewModel(
                entryService,
                categoryRepository,
                userRepository,
                contractService,
                SharingStarted.WhileSubscribed(0)
            )

        // Act
        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value

        // Assert
        assertEquals(500.0, state.totalIncome, 0.001)
        assertEquals(170.0, state.totalExpenses, 0.001)
        assertEquals(330.0, state.netGrowth, 0.001)

        assertEquals(3, state.transactions.size)
        // Sorted by date descending: date1 (Lunch), date2 (Salary), date3 (Dinner)
        assertEquals("Lunch", state.transactions[0].description)
        assertEquals("Salary", state.transactions[1].description)
        assertEquals("Dinner", state.transactions[2].description)

        assertEquals("Food", state.transactions[0].categoryName)
        assertEquals("Salary", state.transactions[1].categoryName)

        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `uiState calculates totals correctly`() = runTest {
        val entries =
            listOf(
                Entry(
                    id = "1",
                    amount = 100.0,
                    type = EntryType.INCOME,
                    date = Date(),
                    categoryId = "1"
                ),
                Entry(
                    id = "2",
                    amount = 50.0,
                    type = EntryType.EXPENSE,
                    date = Date(),
                    categoryId = "1"
                )
            )
        val categories = emptyList<Category>()

        coEvery { entryService.getAllEntries() } returns flowOf(entries)
        coEvery { categoryRepository.getAllCategories() } returns flowOf(categories)
        every { entryService.calculateTotalIncome(any()) } returns 100.0
        every { entryService.calculateTotalExpenses(any()) } returns 50.0
        every { entryService.calculateNetGrowth(any()) } returns 50.0

        val viewModel =
            DashboardViewModel(
                entryService,
                categoryRepository,
                userRepository,
                contractService,
                SharingStarted.WhileSubscribed(0)
            )

        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(100.0, state.totalIncome, 0.001)
        assertEquals(50.0, state.totalExpenses, 0.001)
        assertEquals(50.0, state.netGrowth, 0.001)

        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `uiState sorts transactions and limits to 3`() = runTest {
        val now = Instant.now()
        val d1 = Date.from(now.minus(1, ChronoUnit.DAYS))
        val d2 = Date.from(now.minus(2, ChronoUnit.DAYS))
        val d3 = Date.from(now.minus(3, ChronoUnit.DAYS))
        val d4 = Date.from(now.minus(4, ChronoUnit.DAYS))
        val today = Date.from(now)

        val entries =
            listOf(
                Entry(
                    id = "1",
                    amount = 10.0,
                    description = "d4",
                    date = d4,
                    type = EntryType.EXPENSE,
                    categoryId = "c1"
                ),
                Entry(
                    id = "2",
                    amount = 10.0,
                    description = "d2",
                    date = d2,
                    type = EntryType.EXPENSE,
                    categoryId = "c1"
                ),
                Entry(
                    id = "3",
                    amount = 10.0,
                    description = "d1",
                    date = d1,
                    type = EntryType.EXPENSE,
                    categoryId = "c1"
                ),
                Entry(
                    id = "4",
                    amount = 10.0,
                    description = "today",
                    date = today,
                    type = EntryType.EXPENSE,
                    categoryId = "c1"
                )
            )

        val categories =
            listOf(
                Category(
                    id = "c1",
                    title = "Cat1",
                    type = ContractType.EXPENSE,
                    colorHex = "#000"
                )
            )

        coEvery { entryService.getAllEntries() } returns flowOf(entries)
        coEvery { categoryRepository.getAllCategories() } returns flowOf(categories)
        every { entryService.calculateTotalIncome(any()) } returns 0.0
        every { entryService.calculateTotalExpenses(any()) } returns 0.0
        every { entryService.calculateNetGrowth(any()) } returns 0.0

        val viewModel =
            DashboardViewModel(
                entryService,
                categoryRepository,
                userRepository,
                contractService,
                SharingStarted.WhileSubscribed(0)
            )

        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(3, state.transactions.size)
        assertEquals("today", state.transactions[0].description)
        assertEquals("d1", state.transactions[1].description)
        assertEquals("d2", state.transactions[2].description)

        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `uiState handles empty data`() = runTest {
        coEvery { entryService.getAllEntries() } returns flowOf(emptyList())
        coEvery { categoryRepository.getAllCategories() } returns flowOf(emptyList())
        every { entryService.calculateTotalIncome(any()) } returns 0.0
        every { entryService.calculateTotalExpenses(any()) } returns 0.0
        every { entryService.calculateNetGrowth(any()) } returns 0.0

        val viewModel =
            DashboardViewModel(
                entryService,
                categoryRepository,
                userRepository,
                contractService,
                SharingStarted.WhileSubscribed(0)
            )

        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(0.0, state.totalIncome, 0.001)
        assertEquals(0, state.transactions.size)

        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `notifications emitted when contract is due today`() = runTest {
        // Arrange
        val today = Date()
        val contract = Contract(id = "c1", title = "Gym", nextPaymentDate = today)
        every { contractService.getAllContracts() } returns flowOf(listOf(contract))
        coEvery { entryService.getAllEntries() } returns flowOf(emptyList())
        coEvery { categoryRepository.getAllCategories() } returns flowOf(emptyList())
        every { entryService.calculateTotalIncome(any()) } returns 0.0
        every { entryService.calculateTotalExpenses(any()) } returns 0.0
        every { entryService.calculateNetGrowth(any()) } returns 0.0

        val viewModel =
            DashboardViewModel(
                entryService,
                categoryRepository,
                userRepository,
                contractService,
                SharingStarted.WhileSubscribed(0)
            )

        val notifications = mutableListOf<String>()
        val job = launch { viewModel.notifications.collect { notifications.add(it) } }
        val uiJob = launch { viewModel.uiState.collect {} }

        advanceUntilIdle()

        // Assert
        assertEquals(1, notifications.size)
        assertEquals("Today is the next payment for your contract Gym", notifications[0])

        job.cancel()
        uiJob.cancel()
    }
}
