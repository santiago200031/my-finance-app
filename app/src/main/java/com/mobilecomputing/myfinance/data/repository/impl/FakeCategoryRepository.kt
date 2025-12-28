package com.mobilecomputing.myfinance.data.repository.impl

import com.mobilecomputing.myfinance.data.category.Category
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCategoryRepository : CategoryRepository {
    override fun getAllCategories(): Flow<List<Category>> {
        return flowOf(Category.defaultCategories)
    }

    override suspend fun getCategoryById(id: String): Category? {
        return Category.defaultCategories.find { it.id == id }
    }

    override suspend fun updateCategory(category: Category) {
        TODO("Not yet implemented")
    }
}
