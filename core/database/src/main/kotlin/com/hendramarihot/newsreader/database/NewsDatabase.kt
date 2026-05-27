package com.hendramarihot.newsreader.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hendramarihot.newsreader.database.dao.ArticleDao
import com.hendramarihot.newsreader.database.model.ArticleEntity

@Database(
    entities = [ArticleEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}
