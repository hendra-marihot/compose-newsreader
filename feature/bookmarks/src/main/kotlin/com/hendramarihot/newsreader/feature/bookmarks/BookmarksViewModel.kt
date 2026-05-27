package com.hendramarihot.newsreader.feature.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendramarihot.newsreader.domain.GetBookmarkedArticlesUseCase
import com.hendramarihot.newsreader.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    getBookmarkedArticlesUseCase: GetBookmarkedArticlesUseCase,
) : ViewModel() {

    val uiState: StateFlow<BookmarksUiState> = getBookmarkedArticlesUseCase()
        .map { articles ->
            if (articles.isEmpty()) {
                BookmarksUiState.Empty
            } else {
                BookmarksUiState.Success(articles)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BookmarksUiState.Loading,
        )
}

sealed interface BookmarksUiState {
    data object Loading : BookmarksUiState
    data object Empty : BookmarksUiState
    data class Success(val articles: List<Article>) : BookmarksUiState
}
