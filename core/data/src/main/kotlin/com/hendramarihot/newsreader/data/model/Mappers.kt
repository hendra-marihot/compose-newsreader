package com.hendramarihot.newsreader.data.model

import com.hendramarihot.newsreader.database.model.ArticleEntity
import com.hendramarihot.newsreader.model.Article
import com.hendramarihot.newsreader.model.Source
import com.hendramarihot.newsreader.network.model.NetworkArticle
import java.security.MessageDigest

fun NetworkArticle.toEntity(category: String = ""): ArticleEntity = ArticleEntity(
    id = url.toStableId(),
    title = title,
    description = description,
    url = url,
    content = content,
    imageUrl = urlToImage,
    publishedAt = publishedAt,
    sourceId = source.id,
    sourceName = source.name,
    category = category,
    cachedAt = System.currentTimeMillis(),
)

fun ArticleEntity.toArticle(): Article = Article(
    id = id,
    title = title,
    description = description,
    url = url,
    content = content,
    imageUrl = imageUrl,
    publishedAt = publishedAt,
    source = Source(id = sourceId, name = sourceName),
    isBookmarked = isBookmarked,
)

@OptIn(ExperimentalStdlibApi::class)
internal fun String.toStableId(): String =
    MessageDigest.getInstance("SHA-256")
        .digest(toByteArray())
        .toHexString()
        .take(12)
