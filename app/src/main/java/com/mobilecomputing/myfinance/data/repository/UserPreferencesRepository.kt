package com.mobilecomputing.myfinance.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {
    private val CURRENT_USER_ID = stringPreferencesKey("current_user_id")

    val currentUserId: Flow<String?> =
        context.dataStore.data.map { preferences -> preferences[CURRENT_USER_ID] }

    suspend fun saveCurrentUserId(userId: String) {
        context.dataStore.edit { preferences -> preferences[CURRENT_USER_ID] = userId }
    }
}
