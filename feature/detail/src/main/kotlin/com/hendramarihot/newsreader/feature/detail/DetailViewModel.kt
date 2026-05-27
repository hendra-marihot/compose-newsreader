package com.hendramarihot.newsreader.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendramarihot.newsreader.domain.GetArticleUseCase
import com.hendramarihot.newsreader.domain.ToggleBookmarkUseCase
import com.hendramarihot.newsreader.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getArticleUseCase: GetArticleUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
) : ViewModel() {

    private val articleId: String = checkNotNull(savedStateHandle["articleId"])

    val uiState: StateFlow<DetailUiState> = getArticleUseCase(articleId)
        .map { article ->
            if (article != null) {
                DetailUiState.Success(article)
            } else {
                DetailUiState.Error("Article not found")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailUiState.Loading,
        )

    fun onBookmarkToggle() {
        viewModelScope.launch {
            toggleBookmarkUseCase(articleId)
        }
    }
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val article: Article) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
