package com.hendramarihot.newsreader.model

data class Article(
    val id: String,
    val title: String,
    val description: String?,
    val url: String,
    val content: String?,
    val imageUrl: String?,
    val publishedAt: String,
    val source: Source,
    val isBookmarked: Boolean = false,
)
