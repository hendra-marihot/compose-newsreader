package com.hendramarihot.newsreader.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendramarihot.newsreader.domain.GetUserPreferencesUseCase
import com.hendramarihot.newsreader.domain.SetDarkModeUseCase
import com.hendramarihot.newsreader.domain.ToggleCategoryUseCase
import com.hendramarihot.newsreader.model.Category
import com.hendramarihot.newsreader.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
    private val toggleCategoryUseCase: ToggleCategoryUseCase,
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = getUserPreferencesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences(),
        )

    fun onDarkModeToggle(enabled: Boolean) {
        viewModelScope.launch {
            setDarkModeUseCase(enabled)
        }
    }

    fun onCategoryToggle(category: Category, enabled: Boolean) {
        val currentCategories = preferences.value.selectedCategories
        val isCurrentlySelected = category in currentCategories
        if (isCurrentlySelected != enabled) {
            viewModelScope.launch {
                toggleCategoryUseCase(category)
            }
        }
    }
}
