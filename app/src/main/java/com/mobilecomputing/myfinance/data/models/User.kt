package com.mobilecomputing.myfinance.data.models

import com.mobilecomputing.myfinance.data.models.user.UserSettings

data class User(
        val id: String = "",
        val email: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val profileImageUrl: String? = null,
        val settings: UserSettings = UserSettings()
)

data class UserSettings(
        val currency: String = "USD",
        val language: String = "en",
        val dateFormat: String = "MM/DD/YYYY",
        val theme: String = "LIGHT",
        val notificationsEnabled: Boolean = true
)
