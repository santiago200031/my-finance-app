package com.mobilecomputing.myfinance.data.repository

import com.mobilecomputing.myfinance.data.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>

    suspend fun updateUser(user: User)
}
