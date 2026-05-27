package com.hendramarihot.newsreader.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkResponse(
    val status: String = "",
    val totalResults: Int = 0,
    val articles: List<NetworkArticle> = emptyList(),
)
