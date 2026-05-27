package com.hendramarihot.newsreader.domain

import com.hendramarihot.newsreader.domain.repository.ArticleRepository
import com.hendramarihot.newsreader.model.Article
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArticleUseCase @Inject constructor(
    private val articleRepository: ArticleRepository,
) {
    operator fun invoke(articleId: String): Flow<Article?> =
        articleRepository.getArticle(articleId)
}
