package com.mobilecomputing.myfinance.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.models.User
import com.mobilecomputing.myfinance.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(val user: User? = null, val isLoading: Boolean = true)

class SettingsViewModel(private val userRepository: UserRepository) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> =
            userRepository
                    .getCurrentUser()
                    .map { user -> SettingsUiState(user = user, isLoading = false) }
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = SettingsUiState()
                    )

    fun updateTheme(isDark: Boolean) {
        viewModelScope.launch {
            val currentUser = uiState.value.user
            if (currentUser != null) {
                val newSettings = currentUser.settings.copy(isDarkTheme = isDark)
                userRepository.updateUser(currentUser.copy(settings = newSettings))
            }
        }
    }

    fun switchUser() {
        val currentId = uiState.value.user?.id
        val nextId = if (currentId == "villavicencioandrs") "s-svilla" else "villavicencioandrs"
        userRepository.setCurrentUser(nextId)
    }

    fun addTrustedEmail(email: String) {
        viewModelScope.launch { userRepository.addTrustedEmail(email) }
    }
}
