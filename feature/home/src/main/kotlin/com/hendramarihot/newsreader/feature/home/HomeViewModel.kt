package com.hendramarihot.newsreader.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendramarihot.newsreader.model.result.Result
import com.hendramarihot.newsreader.domain.GetArticlesUseCase
import com.hendramarihot.newsreader.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getArticlesUseCase: GetArticlesUseCase,
) : ViewModel() {

    private val selectedCategory = MutableStateFlow(Category.GENERAL)

    val uiState: StateFlow<HomeUiState> = selectedCategory
        .flatMapLatest { category ->
            getArticlesUseCase(category).map { result ->
                when (result) {
                    is Result.Loading -> HomeUiState.Loading
                    is Result.Success -> HomeUiState.Success(
                        articles = result.data,
                        selectedCategory = category,
                    )
                    is Result.Error -> HomeUiState.Error(
                        message = result.exception.message ?: "Unknown error",
                        selectedCategory = category,
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading,
        )

    fun onCategorySelected(category: Category) {
        selectedCategory.value = category
    }
}
