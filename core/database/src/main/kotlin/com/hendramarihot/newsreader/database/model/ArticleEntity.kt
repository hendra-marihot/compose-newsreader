package com.hendramarihot.newsreader.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "articles",
    indices = [
        Index("category"),
        Index("isBookmarked"),
        Index("cachedAt"),
    ],
)
data class ArticleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val url: String,
    val content: String?,
    val imageUrl: String?,
    val publishedAt: String,
    val sourceId: String?,
    val sourceName: String,
    val category: String = "",
    val isBookmarked: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis(),
)
