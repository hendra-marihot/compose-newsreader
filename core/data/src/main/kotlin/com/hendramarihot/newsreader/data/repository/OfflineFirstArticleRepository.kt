package com.hendramarihot.newsreader.data.repository

import com.hendramarihot.newsreader.common.di.IoDispatcher
import com.hendramarihot.newsreader.domain.repository.ArticleRepository
import com.hendramarihot.newsreader.model.result.Result
import com.hendramarihot.newsreader.data.model.toArticle
import com.hendramarihot.newsreader.data.model.toEntity
import com.hendramarihot.newsreader.database.dao.ArticleDao
import com.hendramarihot.newsreader.model.Article
import com.hendramarihot.newsreader.model.Category
import com.hendramarihot.newsreader.network.NewsApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class OfflineFirstArticleRepository @Inject constructor(
    private val newsApi: NewsApi,
    private val articleDao: ArticleDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ArticleRepository {

    override fun getArticles(category: Category): Flow<Result<List<Article>>> = channelFlow {
        send(Result.Loading)
        launch { refreshArticles(category) }
        articleDao.getArticlesByCategory(category.apiName)
            .map { entities -> Result.Success(entities.map { it.toArticle() }) }
            .collect { send(it) }
    }.catch { exception ->
        Timber.e(exception, "Error loading articles")
        emit(Result.Error(exception))
    }

    override fun getArticle(id: String): Flow<Article?> =
        articleDao.getArticleById(id)
            .map { entity -> entity?.toArticle() }

    override fun searchArticles(query: String): Flow<Result<List<Article>>> = channelFlow {
        send(Result.Loading)
        val response = withContext(ioDispatcher) {
            newsApi.searchArticles(query = query)
        }
        val entities = response.articles
            .filter { it.url.isNotBlank() }
            .map { it.toEntity() }
        articleDao.upsertPreservingBookmarks(entities)
        send(Result.Success(entities.map { it.toArticle() }))
    }.catch { exception ->
        Timber.e(exception, "Error searching articles")
        emit(Result.Error(exception))
    }

    override suspend fun toggleBookmark(articleId: String) {
        withContext(ioDispatcher) {
            articleDao.toggleBookmark(articleId)
        }
    }

    override fun getBookmarkedArticles(): Flow<List<Article>> =
        articleDao.getBookmarkedArticles()
            .map { entities -> entities.map { it.toArticle() } }

    private suspend fun refreshArticles(category: Category) {
        withContext(ioDispatcher) {
            runCatching {
                val response = newsApi.getTopHeadlines(category = category.apiName)
                val entities = response.articles
                    .filter { it.url.isNotBlank() }
                    .map { it.toEntity(category.apiName) }
                articleDao.upsertPreservingBookmarks(entities)
                val staleThreshold = System.currentTimeMillis() - CACHE_EXPIRY_MS
                articleDao.deleteOldArticles(staleThreshold)
            }.onFailure { exception ->
                Timber.e(exception, "Error refreshing articles for category: ${category.apiName}")
            }
        }
    }

    private companion object {
        const val CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000L
    }
}
