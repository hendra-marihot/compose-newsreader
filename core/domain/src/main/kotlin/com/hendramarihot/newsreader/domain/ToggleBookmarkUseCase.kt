package com.hendramarihot.newsreader.domain

import com.hendramarihot.newsreader.domain.repository.ArticleRepository
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val articleRepository: ArticleRepository,
) {
    suspend operator fun invoke(articleId: String) {
        articleRepository.toggleBookmark(articleId)
    }
}
