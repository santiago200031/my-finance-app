package com.mobilecomputing.myfinance.data.repository.impl

import com.mobilecomputing.myfinance.data.category.Category
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeCategoryRepository : CategoryRepository {
    private val _categories = MutableStateFlow(Category.defaultCategories)

    override fun getAllCategories(): Flow<List<Category>> = _categories

    override suspend fun getCategoryById(id: String): Category? {
        return _categories.value.find { it.id == id }
    }

    override suspend fun updateCategory(category: Category) {
        _categories.update { list -> list.map { if (it.id == category.id) category else it } }
    }

    override suspend fun addCategory(category: Category) {
        _categories.update { list -> list + category }
    }

    override suspend fun deleteCategory(categoryId: String) {
        _categories.update { list -> list.filter { it.id != categoryId } }
    }
}
