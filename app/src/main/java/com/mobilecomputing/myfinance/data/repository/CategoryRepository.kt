package com.mobilecomputing.myfinance.data.repository

import com.mobilecomputing.myfinance.data.category.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getCategoryById(id: String): Category?
}
