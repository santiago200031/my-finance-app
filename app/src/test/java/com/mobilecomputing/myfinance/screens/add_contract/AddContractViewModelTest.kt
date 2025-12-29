package com.mobilecomputing.myfinance.screens.add_contract

import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractStatus
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.models.User
import com.mobilecomputing.myfinance.data.models.user.UserSettings
import com.mobilecomputing.myfinance.data.repository.ReminderRepository
import com.mobilecomputing.myfinance.data.repository.UserRepository
import com.mobilecomputing.myfinance.data.service.ContractService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddContractViewModelTest {

    private lateinit var viewModel: AddContractViewModel
    private val contractService = mockk<ContractService>(relaxed = true)
    private val reminderRepository = mockk<ReminderRepository>(relaxed = true)
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // 1. Configure Mocks BEFORE using them in ViewModel
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
        coEvery { contractService.checkContractStatus(any()) } answers { firstArg() }

        // 2. Instantiate ViewModel
        viewModel = AddContractViewModel(contractService, reminderRepository, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveContract saves provider field correctly`() = runTest {
        // Given
        val providerName = "Test Provider"
        viewModel.onTitleChange("Test Contract")
        viewModel.onAmountChange("100")
        viewModel.onProviderChange(providerName)

        val slot = slot<Contract>()

        // When
        viewModel.saveContract()

        // Then
        coVerify { contractService.addContract(capture(slot)) }
        assertEquals(providerName, slot.captured.provider)
    }

    @Test
    fun `ensureDebtConsistency calculates End Date from Total Debt correctly`() {
        // Given
        viewModel.onTypeSelect(ContractType.DEBT)
        viewModel.onAmountChange("100")
        viewModel.onTotalAmountChange("350") // 350 / 100 = 3.5 -> 4 months
        viewModel.onStartDateChange("01.01.2025")

        // When
        // Calculation happens in onTotalAmountChange

        // Then
        // Start: 01.01.2025. 4 months later -> 01.05.2025
        val expectedDate = "01.05.2025"
        assertEquals(expectedDate, viewModel.uiState.value.expirationDate)
    }

    @Test
    fun `ensureDebtConsistency calculates Total Debt from End Date correctly`() {
        // Given
        viewModel.onTypeSelect(ContractType.DEBT)
        viewModel.onAmountChange("100")
        viewModel.onStartDateChange("01.01.2025")

        // When
        viewModel.onExpirationDateChange("01.05.2025") // 4 months diff

        // Then
        // 4 months * 100 = 400
        val expectedTotal = "400.0"
        assertEquals(expectedTotal, viewModel.uiState.value.totalAmount)
    }

    @Test
    fun `saveContract sets status to OUTDATED if end date is in past`() = runTest {
        // Given
        viewModel.onTypeSelect(ContractType.DEBT)
        viewModel.onTitleChange("Past Debt")
        viewModel.onAmountChange("100")
        viewModel.onTotalAmountChange("200")
        viewModel.onStartDateChange("01.01.2020")

        // Expected End Date: 01.03.2020 (2 months later)
        // In the past
        val slot = slot<Contract>()

        // When
        viewModel.saveContract()

        // Then
        coVerify { contractService.addContract(capture(slot)) }
        assertEquals(ContractStatus.OUTDATED, slot.captured.status)
    }

    @Test
    fun `saveContract does NOT override manual status if end date is in past`() = runTest {
        // Given
        viewModel.onTypeSelect(ContractType.DEBT)
        viewModel.onTitleChange("Past Debt")
        viewModel.onAmountChange("100")
        viewModel.onTotalAmountChange("200")
        viewModel.onStartDateChange("01.01.2020")

        // User manually sets it to CANCELLED locally (even if dates imply outdated)
        viewModel.onStatusChange(ContractStatus.CANCELLED)

        val slot = slot<Contract>()

        // When
        viewModel.saveContract()

        // Then
        coVerify { contractService.addContract(capture(slot)) }
        // Should respect manual override
        assertEquals(ContractStatus.CANCELLED, slot.captured.status)
    }
}
