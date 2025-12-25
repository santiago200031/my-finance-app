package com.mobilecomputing.myfinance.data.models.user

import com.google.firebase.firestore.DocumentId

data class UserSettings(
    @DocumentId val userId: String = "",
    val userName: String = "",
    val email: String = "",
    val currency: String = "EUR",
    val dateFormat: String = "dd.MM.yyyy",
    val isDarkTheme: Boolean = false,
    val language: String = "en",
    val pushNotifications: Boolean = true
)
