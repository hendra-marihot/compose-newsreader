package com.hendramarihot.newsreader.network.di

import com.hendramarihot.newsreader.network.BuildConfig
import com.hendramarihot.newsreader.network.NewsApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://newsapi.org/"
    private const val API_KEY_HEADER = "X-Api-Key"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader(API_KEY_HEADER, BuildConfig.NEWS_API_KEY)
                .build()
            chain.proceed(request)
        }
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): NewsApi = retrofit.create(NewsApi::class.java)
}
