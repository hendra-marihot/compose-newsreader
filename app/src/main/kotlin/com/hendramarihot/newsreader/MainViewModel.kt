package com.hendramarihot.newsreader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendramarihot.newsreader.domain.GetUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getUserPreferencesUseCase: GetUserPreferencesUseCase,
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = getUserPreferencesUseCase()
        .map { it.isDarkMode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )
}
