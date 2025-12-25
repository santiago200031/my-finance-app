package com.mobilecomputing.myfinance.data.repository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mobilecomputing.myfinance.data.reminder.Reminder
import com.mobilecomputing.myfinance.data.repository.ReminderRepository
import com.mobilecomputing.myfinance.data.repository.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await

class FirestoreReminderRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    userPreferencesRepository: UserPreferencesRepository
) : ReminderRepository {

    private val userIdFlow: Flow<String?> = userPreferencesRepository.currentUserId

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllReminders(): Flow<List<Reminder>> {
        return userIdFlow.flatMapLatest { userId ->
            callbackFlow {
                if (userId == null) {
                    trySend(emptyList())
                    awaitClose {}
                    return@callbackFlow
                }

                val collection = firestore
                    .collection("users")
                    .document(userId)
                    .collection("reminders")

                val listener = collection.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FirestoreReminderRepo", "Error fetching reminders", error)
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val reminders = snapshot.documents.mapNotNull { doc ->
                            try {
                                doc.toObject(Reminder::class.java)
                            } catch (e: Exception) {
                                Log.e(
                                    "FirestoreReminderRepo",
                                    "Error deserializing reminder ${doc.id}",
                                    e
                                )
                                null
                            }
                        }
                        trySend(reminders)
                    } else {
                        trySend(emptyList())
                    }
                }
                awaitClose { listener.remove() }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getReminderById(id: String): Flow<Reminder?> {
        return userIdFlow.flatMapLatest { userId ->
            callbackFlow {
                if (userId == null) {
                    trySend(null)
                    awaitClose {}
                    return@callbackFlow
                }

                val docRef = firestore
                    .collection("users")
                    .document(userId)
                    .collection("reminders")
                    .document(id)

                val listener = docRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FirestoreReminderRepo", "Error fetching reminder $id", error)
                        trySend(null)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        trySend(snapshot.toObject(Reminder::class.java))
                    } else {
                        trySend(null)
                    }
                }
                awaitClose { listener.remove() }
            }
        }
    }

    override suspend fun addReminder(reminder: Reminder) {
        try {
            userIdFlow.first()?.let { userId ->
                firestore
                    .collection("users")
                    .document(userId)
                    .collection("reminders")
                    .document(reminder.id)
                    .set(reminder)
                    .await()
            }
        } catch (e: Exception) {
            Log.e("FirestoreReminderRepo", "Error adding reminder", e)
        }
    }

    override suspend fun updateReminder(reminder: Reminder) {
        addReminder(reminder)
    }

    override suspend fun deleteReminder(id: String) {
        try {
            userIdFlow.first()?.let { userId ->
                firestore
                    .collection("users")
                    .document(userId)
                    .collection("reminders")
                    .document(id)
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            Log.e("FirestoreReminderRepo", "Error deleting reminder", e)
        }
    }

    override suspend fun deleteRemindersForContract(contractId: String) {
        try {
            userIdFlow.first()?.let { userId ->
                val snapshot = firestore
                    .collection("users")
                    .document(userId)
                    .collection("reminders")
                    .whereEqualTo("contractId", contractId)
                    .get()
                    .await()

                val batch = firestore.batch()
                for (doc in snapshot.documents) {
                    batch.delete(doc.reference)
                }
                batch.commit().await()
            }
        } catch (e: Exception) {
            Log.e("FirestoreReminderRepo", "Error deleting reminders for contract", e)
        }
    }
}
