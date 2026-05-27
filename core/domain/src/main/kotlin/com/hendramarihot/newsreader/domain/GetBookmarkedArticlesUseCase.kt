package com.hendramarihot.newsreader.domain

import com.hendramarihot.newsreader.domain.repository.ArticleRepository
import com.hendramarihot.newsreader.model.Article
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarkedArticlesUseCase @Inject constructor(
    private val articleRepository: ArticleRepository,
) {
    operator fun invoke(): Flow<List<Article>> =
        articleRepository.getBookmarkedArticles()
}
