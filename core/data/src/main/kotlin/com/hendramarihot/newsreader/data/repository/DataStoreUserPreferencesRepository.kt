package com.hendramarihot.newsreader.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.hendramarihot.newsreader.domain.UserPreferencesRepository
import com.hendramarihot.newsreader.model.Category
import com.hendramarihot.newsreader.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreUserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    override val userPreferences: Flow<UserPreferences> = dataStore.data.map { preferences ->
        UserPreferences(
            isDarkMode = preferences[DARK_MODE_KEY] ?: false,
            selectedCategories = preferences[SELECTED_CATEGORIES_KEY]
                ?.mapNotNull { name -> Category.entries.find { it.apiName == name } }
                ?.toSet()
                ?: setOf(Category.GENERAL),
        )
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    override suspend fun toggleCategory(category: Category) {
        dataStore.edit { preferences ->
            val current = preferences[SELECTED_CATEGORIES_KEY] ?: setOf(Category.GENERAL.apiName)
            preferences[SELECTED_CATEGORIES_KEY] = if (category.apiName in current) {
                (current - category.apiName).ifEmpty { setOf(Category.GENERAL.apiName) }
            } else {
                current + category.apiName
            }
        }
    }

    private companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val SELECTED_CATEGORIES_KEY = stringSetPreferencesKey("selected_categories")
    }
}
