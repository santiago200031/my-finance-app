package com.mobilecomputing.myfinance.data.repository.impl

import com.mobilecomputing.myfinance.data.models.User
import com.mobilecomputing.myfinance.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeUserRepository : UserRepository {
    // Simulate single user
    private val _user = MutableStateFlow(
        User(
            id = "1",
            email = "s-svilla@haw-landshut.de",
            firstName = "Santiago",
            lastName = "Villavicencio"
        )
    )

    override fun getCurrentUser(): Flow<User?> = _user.asStateFlow()

    override suspend fun updateUser(user: User) {
        _user.value = user
    }
}
