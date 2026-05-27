package com.hendramarihot.newsreader.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.hendramarihot.newsreader.database.model.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles ORDER BY cachedAt DESC")
    fun getArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY cachedAt DESC")
    fun getArticlesByCategory(category: String): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :id")
    fun getArticleById(id: String): Flow<ArticleEntity?>

    @Query("SELECT * FROM articles WHERE isBookmarked = 1")
    fun getBookmarkedArticles(): Flow<List<ArticleEntity>>

    @Upsert
    suspend fun upsertArticles(articles: List<ArticleEntity>)

    @Query("SELECT id FROM articles WHERE id IN (:ids) AND isBookmarked = 1")
    suspend fun getBookmarkedIds(ids: List<String>): List<String>

    @Query("UPDATE articles SET isBookmarked = 1 WHERE id IN (:ids)")
    suspend fun restoreBookmarks(ids: List<String>)

    @Transaction
    suspend fun upsertPreservingBookmarks(articles: List<ArticleEntity>) {
        val ids = articles.map { it.id }
        val bookmarkedIds = getBookmarkedIds(ids)
        upsertArticles(articles)
        if (bookmarkedIds.isNotEmpty()) {
            restoreBookmarks(bookmarkedIds)
        }
    }

    @Query("UPDATE articles SET isBookmarked = NOT isBookmarked WHERE id = :id")
    suspend fun toggleBookmark(id: String)

    @Query("DELETE FROM articles WHERE isBookmarked = 0 AND cachedAt < :timestamp")
    suspend fun deleteOldArticles(timestamp: Long)
}
