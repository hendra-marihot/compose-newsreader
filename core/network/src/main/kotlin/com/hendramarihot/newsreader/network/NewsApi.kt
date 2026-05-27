package com.hendramarihot.newsreader.network

import com.hendramarihot.newsreader.network.model.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
    ): NetworkResponse

    @GET("v2/everything")
    suspend fun searchArticles(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
    ): NetworkResponse
}
