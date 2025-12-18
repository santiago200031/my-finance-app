package com.mobilecomputing.myfinance.data.repository.impl

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mobilecomputing.myfinance.data.models.User
import com.mobilecomputing.myfinance.data.repository.UserPreferencesRepository
import com.mobilecomputing.myfinance.data.repository.UserRepository
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
class FirestoreUserRepository(
        private val initialUsers: List<User>,
        private val userPreferencesRepository: UserPreferencesRepository
) : UserRepository {
  private val firestore = FirebaseFirestore.getInstance()
  private val usersCollection = firestore.collection("users")
  private val scope = CoroutineScope(Dispatchers.IO)

  // Use a MutableStateFlow for the current user ID. Default to the first user in the list.
  private val currentUserIdFlow = MutableStateFlow(initialUsers.first().id)

  init {
    scope.launch {
      userPreferencesRepository.currentUserId.collect { userId ->
        if (userId != null) {
          currentUserIdFlow.value = userId
          checkAndCreateUser(userId)
        } else {
          checkAndCreateUser(currentUserIdFlow.value)
        }
      }
    }
  }

  private fun checkAndCreateUser(userId: String) {
    usersCollection
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
              if (!document.exists()) {
                val userToCreate = initialUsers.find { it.id == userId }

                if (userToCreate != null) {
                  usersCollection.document(userId).set(userToCreate).addOnFailureListener { e ->
                    Log.e("FirestoreUserRepository", "Error creating user $userId", e)
                  }
                }
              }
            }
            .addOnFailureListener { e ->
              Log.e("FirestoreUserRepository", "Error checking user existence", e)
            }
  }

  override fun getCurrentUser(): Flow<User?> =
          currentUserIdFlow.flatMapLatest { userId ->
            callbackFlow {
              val docRef = usersCollection.document(userId)
              val listener =
                      docRef.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                          Log.e("FirestoreUserRepository", "Error fetching user", error)
                          trySend(null)
                          return@addSnapshotListener
                        }

                        if (snapshot != null && snapshot.exists()) {
                          val user = snapshot.toObject(User::class.java)
                          trySend(user)
                        } else {
                          trySend(null)
                        }
                      }
              awaitClose { listener.remove() }
            }
          }

  override fun setCurrentUser(userId: String) {
    scope.launch { userPreferencesRepository.saveCurrentUserId(userId) }
  }

  override suspend fun updateUser(user: User) {
    try {
      usersCollection.document(currentUserIdFlow.value).set(user).await()
    } catch (e: Exception) {
      Log.e("FirestoreUserRepository", "Error updating user", e)
    }
  }

  override suspend fun getUserByEmail(email: String): User? {
    return try {
      val snapshot = usersCollection.whereEqualTo("email", email).get().await()
      if (snapshot.isEmpty) return null
      snapshot.documents.firstOrNull()?.toObject(User::class.java)
    } catch (e: Exception) {
      Log.e("FirestoreUserRepository", "Error fetching user by email", e)
      null
    }
  }

  override suspend fun addTrustedEmail(email: String) {
    try {
      usersCollection
              .document(currentUserIdFlow.value)
              .update("trustedEmails", FieldValue.arrayUnion(email))
              .await()
    } catch (e: Exception) {
      Log.e("FirestoreUserRepository", "Error adding trusted email", e)
    }
  }

  override suspend fun getUserById(userId: String): User? {
    return try {
      val snapshot = usersCollection.document(userId).get().await()
      if (snapshot.exists()) {
        snapshot.toObject(User::class.java)
      } else {
        null
      }
    } catch (e: Exception) {
      Log.e("FirestoreUserRepository", "Error fetching user by id", e)
      null
    }
  }
}
