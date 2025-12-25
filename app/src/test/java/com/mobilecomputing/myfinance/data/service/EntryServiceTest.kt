package com.mobilecomputing.myfinance.data.service

import com.mobilecomputing.myfinance.data.entry.Entry
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.data.repository.EntryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Date

class EntryServiceTest {

    private lateinit var entryRepository: EntryRepository
    private lateinit var entryService: EntryService

    @Before
    fun setUp() {
        entryRepository = mockk()
        entryService = EntryService(entryRepository)
    }

    @Test
    fun getAllEntries_delegatesToRepository() {
        coEvery { entryRepository.getAllEntries() } returns flowOf(emptyList())
        entryService.getAllEntries()
        coVerify { entryRepository.getAllEntries() }
    }

    @Test
    fun addEntry_delegatesToRepository() = runTest {
        val entry =
            Entry(
                id = "1",
                userId = "user1",
                categoryId = "cat1",
                amount = 100.0,
                date = Date(),
                description = "Test",
                type = EntryType.INCOME
            )
        coEvery { entryRepository.addEntry(entry) } returns Unit
        entryService.addEntry(entry)
        coVerify { entryRepository.addEntry(entry) }
    }

    @Test
    fun updateEntry_delegatesToRepository() = runTest {
        val entry =
            Entry(
                id = "1",
                userId = "user1",
                categoryId = "cat1",
                amount = 100.0,
                date = Date(),
                description = "Test",
                type = EntryType.INCOME
            )
        coEvery { entryRepository.updateEntry(entry) } returns Unit
        entryService.updateEntry(entry)
        coVerify { entryRepository.updateEntry(entry) }
    }

    @Test
    fun deleteEntry_delegatesToRepository() = runTest {
        val id = "1"
        coEvery { entryRepository.deleteEntry(id) } returns Unit
        entryService.deleteEntry(id)
        coVerify { entryRepository.deleteEntry(id) }
    }
}
