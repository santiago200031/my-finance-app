package com.mobilecomputing.myfinance.data.repository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mobilecomputing.myfinance.data.category.Category
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
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
class FirestoreCategoryRepository(
    private val userPreferencesRepository: UserPreferencesRepository
) : CategoryRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val currentUserIdFlow = MutableStateFlow<String?>(null)

    init {
        scope.launch {
            userPreferencesRepository.currentUserId.collect { userId ->
                currentUserIdFlow.value = userId
                if (userId != null) {
                    initializeCategoriesIfEmpty(userId)
                }
            }
        }
    }

    private suspend fun initializeCategoriesIfEmpty(userId: String) {
        try {
            val categoriesCollection =
                firestore.collection("users").document(userId).collection("categories")
            val snapshot = categoriesCollection.get().await()
            if (snapshot.isEmpty) {
                Log.d("FirestoreCategoryRepo", "Initializing categories for user: $userId")
                for (category in Category.defaultCategories) {
                    categoriesCollection.document(category.id).set(category).await()
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreCategoryRepo", "Error initializing categories", e)
        }
    }

    override fun getAllCategories(): Flow<List<Category>> =
        currentUserIdFlow.flatMapLatest { userId ->
            callbackFlow {
                if (userId == null) {
                    trySend(emptyList())
                    awaitClose {}
                    return@callbackFlow
                }

                val categoriesCollection =
                    firestore.collection("users").document(userId).collection("categories")

                val listener =
                    categoriesCollection.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e(
                                "FirestoreCategoryRepo",
                                "Error fetching categories",
                                error
                            )
                            trySend(emptyList())
                            return@addSnapshotListener
                        }

                        if (snapshot != null) {
                            val categories =
                                snapshot.documents.mapNotNull {
                                    it.toObject(Category::class.java)
                                }
                            trySend(categories)
                        } else {
                            trySend(emptyList())
                        }
                    }
                awaitClose { listener.remove() }
            }
        }

    override suspend fun getCategoryById(id: String): Category? {
        val userId = currentUserIdFlow.value ?: return null
        return try {
            firestore
                .collection("users")
                .document(userId)
                .collection("categories")
                .document(id)
                .get()
                .await()
                .toObject(Category::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreCategoryRepo", "Error getting category $id", e)
            null
        }
    }

    override suspend fun updateCategory(category: Category) {
        val userId = currentUserIdFlow.value ?: return
        try {
            firestore
                .collection("users")
                .document(userId)
                .collection("categories")
                .document(category.id)
                .set(category)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreCategoryRepo", "Error updating category", e)
        }
    }
}
