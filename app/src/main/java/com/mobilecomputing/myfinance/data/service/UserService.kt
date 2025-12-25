package com.mobilecomputing.myfinance.data.service

import com.mobilecomputing.myfinance.data.models.User
import com.mobilecomputing.myfinance.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserService(private val userRepository: UserRepository) {

    fun getCurrentUser(): Flow<User?> = userRepository.getCurrentUser()

    suspend fun updateUser(user: User) = userRepository.updateUser(user)
}
