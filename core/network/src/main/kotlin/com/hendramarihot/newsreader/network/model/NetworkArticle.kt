package com.hendramarihot.newsreader.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkArticle(
    val source: NetworkSource = NetworkSource(),
    val author: String? = null,
    val title: String = "",
    val description: String? = null,
    val url: String = "",
    val urlToImage: String? = null,
    val publishedAt: String = "",
    val content: String? = null,
)
