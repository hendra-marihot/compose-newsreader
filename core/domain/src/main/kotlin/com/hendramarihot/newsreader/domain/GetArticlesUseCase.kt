package com.hendramarihot.newsreader.domain

import com.hendramarihot.newsreader.model.result.Result
import com.hendramarihot.newsreader.domain.repository.ArticleRepository
import com.hendramarihot.newsreader.model.Article
import com.hendramarihot.newsreader.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(
    private val articleRepository: ArticleRepository,
) {
    operator fun invoke(category: Category): Flow<Result<List<Article>>> =
        articleRepository.getArticles(category)
}
