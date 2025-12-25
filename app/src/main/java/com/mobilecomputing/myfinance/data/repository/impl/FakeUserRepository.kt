package com.mobilecomputing.myfinance.data.repository.impl

import com.mobilecomputing.myfinance.data.models.User
import com.mobilecomputing.myfinance.data.repository.UserPreferencesRepository
import com.mobilecomputing.myfinance.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FakeUserRepository(private val userPreferencesRepository: UserPreferencesRepository) :
    UserRepository {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _user =
        MutableStateFlow(
            User(
                id = "s-svilla",
                email = "s-svilla@haw-landshut.de",
                firstName = "Santiago",
                lastName = "Villavicencio"
            )
        )

    init {
        scope.launch {
            userPreferencesRepository.currentUserId.collect { userId ->
                if (userId != null) {
                    updateUserParams(userId)
                }
            }
        }
    }

    private fun updateUserParams(userId: String) {
        if (userId == "villavicencioandrs") {
            _user.value =
                User(
                    id = "villavicencioandrs",
                    email = "villavicencioandrs@gmail.com",
                    firstName = "Santiago",
                    lastName = "Villavicencio"
                )
        } else {
            _user.value =
                User(
                    id = "s-svilla",
                    email = "s-svilla@haw-landshut.de",
                    firstName = "Santiago",
                    lastName = "Villavicencio"
                )
        }
    }

    override fun setCurrentUser(userId: String) {
        scope.launch { userPreferencesRepository.saveCurrentUserId(userId) }
    }

    override fun getCurrentUser(): Flow<User?> = _user.asStateFlow()

    override suspend fun updateUser(user: User) {
        _user.value = user
    }

    override suspend fun getUserByEmail(email: String): User? {
        if (email == "villavicencioandrs@gmail.com") {
            return User(
                id = "villavicencioandrs",
                email = "villavicencioandrs@gmail.com",
                firstName = "Santiago",
                lastName = "Villavicencio"
            )
        }
        return if (_user.value.email == email) _user.value else null
    }

    override suspend fun addTrustedEmail(email: String) {
        val currentUser = _user.value
        val newTrustedEmails = currentUser.trustedEmails + email
        _user.value = currentUser.copy(trustedEmails = newTrustedEmails)
    }

    override suspend fun getUserById(userId: String): User? {
        if (userId == "villavicencioandrs") {
            return User(
                id = "villavicencioandrs",
                email = "villavicencioandrs@gmail.com",
                firstName = "Santiago",
                lastName = "Villavicencio",
                trustedEmails =
                    listOf("s-svilla@haw-landshut.de") // Mock trust for testing
            )
        } else if (userId == "s-svilla") {
            return User(
                id = "s-svilla",
                email = "s-svilla@haw-landshut.de",
                firstName = "Santiago",
                lastName = "Villavicencio",
                trustedEmails = listOf("villavicencioandrs@gmail.com")
            )
        }
        return if (_user.value.id == userId) _user.value else null
    }
}
