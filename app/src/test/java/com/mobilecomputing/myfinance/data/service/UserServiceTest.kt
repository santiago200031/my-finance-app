package com.mobilecomputing.myfinance.data.service

import com.mobilecomputing.myfinance.data.models.User
import com.mobilecomputing.myfinance.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var userService: UserService

    @Before
    fun setUp() {
        userRepository = mockk()
        userService = UserService(userRepository)
    }

    @Test
    fun getCurrentUser_delegatesToRepository() {
        coEvery { userRepository.getCurrentUser() } returns flowOf(null)
        userService.getCurrentUser()
        coVerify { userRepository.getCurrentUser() }
    }

    @Test
    fun updateUser_delegatesToRepository() = runTest {
        val user = User("1", "test@test.com", "Test User")
        coEvery { userRepository.updateUser(user) } returns Unit
        userService.updateUser(user)
        coVerify { userRepository.updateUser(user) }
    }
}
