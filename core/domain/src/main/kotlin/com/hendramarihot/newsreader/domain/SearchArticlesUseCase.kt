package com.hendramarihot.newsreader.domain

import com.hendramarihot.newsreader.model.result.Result
import com.hendramarihot.newsreader.domain.repository.ArticleRepository
import com.hendramarihot.newsreader.model.Article
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchArticlesUseCase @Inject constructor(
    private val articleRepository: ArticleRepository,
) {
    operator fun invoke(query: String): Flow<Result<List<Article>>> =
        articleRepository.searchArticles(query)
}
