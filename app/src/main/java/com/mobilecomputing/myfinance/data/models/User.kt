package com.mobilecomputing.myfinance.data.models

import com.google.firebase.firestore.DocumentId
import com.mobilecomputing.myfinance.data.models.user.UserSettings

data class User(
        @DocumentId val id: String = "",
        val email: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val profileImageUrl: String? = null,
        val settings: UserSettings = UserSettings()
)

data class UserSettings(
        val currency: String = "EUR",
        val language: String = "en",
        val dateFormat: String = "MM/DD/YYYY",
        val theme: String = "DARK",
        val notificationsEnabled: Boolean = true
)
