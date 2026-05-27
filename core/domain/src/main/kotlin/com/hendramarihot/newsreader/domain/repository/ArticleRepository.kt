package com.hendramarihot.newsreader.domain.repository

import com.hendramarihot.newsreader.model.Article
import com.hendramarihot.newsreader.model.Category
import com.hendramarihot.newsreader.model.result.Result
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {

    fun getArticles(category: Category): Flow<Result<List<Article>>>

    fun getArticle(id: String): Flow<Article?>

    fun searchArticles(query: String): Flow<Result<List<Article>>>

    suspend fun toggleBookmark(articleId: String)

    fun getBookmarkedArticles(): Flow<List<Article>>
}
