package com.hendramarihot.newsreader.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.hendramarihot.newsreader.data.repository.DataStoreUserPreferencesRepository
import com.hendramarihot.newsreader.data.repository.OfflineFirstArticleRepository
import com.hendramarihot.newsreader.domain.UserPreferencesRepository
import com.hendramarihot.newsreader.domain.repository.ArticleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences",
)

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindArticleRepository(
        impl: OfflineFirstArticleRepository,
    ): ArticleRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: DataStoreUserPreferencesRepository,
    ): UserPreferencesRepository

    companion object {

        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            context.dataStore
    }
}
