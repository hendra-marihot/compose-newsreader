package com.hendramarihot.newsreader.domain

import com.hendramarihot.newsreader.model.Category
import com.hendramarihot.newsreader.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun toggleCategory(category: Category)
}
