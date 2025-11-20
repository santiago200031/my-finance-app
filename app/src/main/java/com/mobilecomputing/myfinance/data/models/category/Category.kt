package com.mobilecomputing.myfinance.data.models.category

import com.google.firebase.firestore.DocumentId

data class Category(
    @DocumentId
    val id: String = "",

    val name: String = "",
    val iconName: String = "default_icon",
    val colorHex: String = "#FFFFFF",
    val isCustom: Boolean = true
)
