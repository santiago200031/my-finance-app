package com.mobilecomputing.myfinance.data.repository.impl

import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.entry.Entry
import com.mobilecomputing.myfinance.data.repository.EntryRepository
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeEntryRepository : EntryRepository {
        // Seed data
        private val _entries =
                MutableStateFlow<List<Entry>>(
                        listOf(
                                Entry(
                                        id = "1",
                                        categoryId = "1", // Salary
                                        amount = 2500.0,
                                        description = "Monthly Salary",
                                        date = Date(),
                                        type = ContractType.INCOME
                                ),
                                Entry(
                                        id = "2",
                                        categoryId = "3", // Food
                                        amount = 15.50,
                                        description = "Lunch at Burger King",
                                        date = Date(),
                                        type = ContractType.EXPENSE
                                ),
                                Entry(
                                        id = "3",
                                        categoryId = "4", // Transport
                                        amount = 50.0,
                                        description = "Fuel",
                                        date = Date(),
                                        type = ContractType.EXPENSE
                                )
                        )
                )

        override fun getAllEntries(): Flow<List<Entry>> = _entries

        override fun getEntryById(id: String): Flow<Entry?> =
                _entries.map { list -> list.find { it.id == id } }

        override suspend fun addEntry(entry: Entry) {
                _entries.update { it + entry }
        }

        override suspend fun updateEntry(entry: Entry) {
                _entries.update { list -> list.map { if (it.id == entry.id) entry else it } }
        }

        override suspend fun deleteEntry(id: String) {
                _entries.update { list -> list.filter { it.id != id } }
        }
}
