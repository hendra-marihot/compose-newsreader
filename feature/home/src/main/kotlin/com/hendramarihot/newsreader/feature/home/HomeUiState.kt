package com.hendramarihot.newsreader.feature.home

import com.hendramarihot.newsreader.model.Article
import com.hendramarihot.newsreader.model.Category

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val articles: List<Article>,
        val selectedCategory: Category,
    ) : HomeUiState
    data class Error(
        val message: String,
        val selectedCategory: Category,
    ) : HomeUiState
}
