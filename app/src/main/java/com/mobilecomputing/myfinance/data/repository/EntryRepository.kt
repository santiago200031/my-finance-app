package com.mobilecomputing.myfinance.data.repository

import com.mobilecomputing.myfinance.data.entry.Entry
import kotlinx.coroutines.flow.Flow

interface EntryRepository {
    fun getAllEntries(): Flow<List<Entry>>
    fun getEntryById(id: String): Flow<Entry?>

    suspend fun addEntry(entry: Entry)
    suspend fun updateEntry(entry: Entry)
    suspend fun deleteEntry(id: String)
}
