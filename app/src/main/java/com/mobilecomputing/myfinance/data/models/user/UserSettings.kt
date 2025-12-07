package com.mobilecomputing.myfinance.data.models.user

import com.google.firebase.firestore.DocumentId

data class UserSettings(
    @DocumentId
    val userId: String,
    val userName: String,
    val email: String,
    val currency: String,
    val dateFormat: String,
    val isDarkTheme: Boolean,
    val pushNotifications: Boolean
)