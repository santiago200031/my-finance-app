package com.mobilecomputing.myfinance.data.service

import com.mobilecomputing.myfinance.data.entry.Entry
import com.mobilecomputing.myfinance.data.repository.EntryRepository
import kotlinx.coroutines.flow.Flow

class EntryService(private val entryRepository: EntryRepository) {

    fun getAllEntries(): Flow<List<Entry>> = entryRepository.getAllEntries()

    fun getEntryById(id: String): Flow<Entry?> = entryRepository.getEntryById(id)

    suspend fun addEntry(entry: Entry) {
        entryRepository.addEntry(entry)
    }

    suspend fun updateEntry(entry: Entry) {
        entryRepository.updateEntry(entry)
    }

    suspend fun deleteEntry(id: String) {
        entryRepository.deleteEntry(id)
    }
}
