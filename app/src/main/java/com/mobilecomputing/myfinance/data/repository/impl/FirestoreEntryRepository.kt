package com.mobilecomputing.myfinance.data.repository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mobilecomputing.myfinance.data.entry.Entry
import com.mobilecomputing.myfinance.data.repository.EntryRepository
import com.mobilecomputing.myfinance.data.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalCoroutinesApi::class)
class FirestoreEntryRepository(private val userPreferencesRepository: UserPreferencesRepository) :
    EntryRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val currentUserIdFlow = MutableStateFlow<String?>(null)

    init {
        scope.launch {
            userPreferencesRepository.currentUserId.collect { userId ->
                currentUserIdFlow.value = userId
            }
        }
    }

    override fun getAllEntries(): Flow<List<Entry>> =
        currentUserIdFlow.flatMapLatest { userId ->
            callbackFlow {
                if (userId == null) {
                    trySend(emptyList())
                    awaitClose {}
                    return@callbackFlow
                }

                val entriesCollection =
                    firestore.collection("users").document(userId).collection("entries")

                val listener =
                    entriesCollection.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("FirestoreEntryRepo", "Error fetching entries", error)
                            trySend(emptyList())
                            return@addSnapshotListener
                        }

                        if (snapshot != null) {
                            val entries =
                                snapshot.documents.mapNotNull { it.toObject(Entry::class.java) }
                            trySend(entries)
                        } else {
                            trySend(emptyList())
                        }
                    }
                awaitClose { listener.remove() }
            }
        }

    override fun getEntryById(id: String): Flow<Entry?> =
        currentUserIdFlow.flatMapLatest { userId ->
            callbackFlow {
                if (userId == null) {
                    trySend(null)
                    awaitClose {}
                    return@callbackFlow
                }

                val docRef =
                    firestore
                        .collection("users")
                        .document(userId)
                        .collection("entries")
                        .document(id)
                val listener =
                    docRef.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("FirestoreEntryRepo", "Error fetching entry $id", error)
                            trySend(null)
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            trySend(snapshot.toObject(Entry::class.java))
                        } else {
                            trySend(null)
                        }
                    }
                awaitClose { listener.remove() }
            }
        }

    override suspend fun addEntry(entry: Entry) {
        val userId = currentUserIdFlow.value
        if (userId != null) {
            try {
                firestore
                    .collection("users")
                    .document(userId)
                    .collection("entries")
                    .document(entry.id)
                    .set(entry)
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreEntryRepo", "Error adding entry", e)
            }
        }
    }

    override suspend fun updateEntry(entry: Entry) {
        addEntry(entry) // Firestore set acts as upsert/update
    }

    override suspend fun deleteEntry(id: String) {
        val userId = currentUserIdFlow.value
        if (userId != null) {
            try {
                firestore
                    .collection("users")
                    .document(userId)
                    .collection("entries")
                    .document(id)
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreEntryRepo", "Error deleting entry", e)
            }
        }
    }
}
