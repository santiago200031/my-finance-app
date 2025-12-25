package com.mobilecomputing.myfinance.data.repository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.repository.ContractRepository
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
class FirestoreContractRepository(
    private val userPreferencesRepository: UserPreferencesRepository
) : ContractRepository {

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

    override fun getAllContracts(): Flow<List<Contract>> =
        currentUserIdFlow.flatMapLatest { userId ->
            callbackFlow {
                if (userId == null) {
                    trySend(emptyList())
                    awaitClose {}
                    return@callbackFlow
                }

                val contractsCollection =
                    firestore.collection("users").document(userId).collection("contracts")

                val listener =
                    contractsCollection.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("FirestoreContractRepo", "Error fetching contracts", error)
                            trySend(emptyList())
                            return@addSnapshotListener
                        }

                        if (snapshot != null) {
                            val contracts =
                                snapshot.documents.mapNotNull { doc ->
                                    try {
                                        doc.toObject(Contract::class.java)
                                    } catch (e: Exception) {
                                        Log.e(
                                            "FirestoreContractRepo",
                                            "Error deserializing contract ${doc.id}",
                                            e
                                        )
                                        null
                                    }
                                }
                            trySend(contracts)
                        } else {
                            trySend(emptyList())
                        }
                    }
                awaitClose { listener.remove() }
            }
        }

    override fun getContractsForUser(userId: String): Flow<List<Contract>> {

        return callbackFlow {
            val contractsCollection =
                firestore.collection("users").document(userId).collection("contracts")

            val listener =
                contractsCollection.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(
                            "FirestoreContractRepo",
                            "Error fetching contracts for user $userId",
                            error
                        )
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val contracts =
                            snapshot.documents.mapNotNull { doc ->
                                try {
                                    doc.toObject(Contract::class.java)
                                } catch (e: Exception) {
                                    Log.e(
                                        "FirestoreContractRepo",
                                        "Error deserializing contract ${doc.id}",
                                        e
                                    )
                                    null
                                }
                            }
                        trySend(contracts)
                    } else {
                        trySend(emptyList())
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    override fun getContractById(id: String): Flow<Contract?> =
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
                        .collection("contracts")
                        .document(id)
                val listener =
                    docRef.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("FirestoreContractRepo", "Error fetching contract $id", error)
                            trySend(null)
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            trySend(snapshot.toObject(Contract::class.java))
                        } else {
                            trySend(null)
                        }
                    }
                awaitClose { listener.remove() }
            }
        }

    override suspend fun addContract(contract: Contract) {
        val userId = currentUserIdFlow.value
        if (userId != null) {
            try {
                // Ensure contract has the correct userId set, although path determines ownership usually
                val contractWithUser = contract.copy(userId = userId)

                firestore
                    .collection("users")
                    .document(userId)
                    .collection("contracts")
                    .document(contract.id)
                    .set(contractWithUser)
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreContractRepo", "Error adding contract", e)
            }
        }
    }

    override suspend fun updateContract(contract: Contract) {
        addContract(contract)
    }

    override suspend fun deleteContract(id: String) {
        val userId = currentUserIdFlow.value
        if (userId != null) {
            try {
                firestore
                    .collection("users")
                    .document(userId)
                    .collection("contracts")
                    .document(id)
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.e("FirestoreContractRepo", "Error deleting contract", e)
            }
        }
    }
}
