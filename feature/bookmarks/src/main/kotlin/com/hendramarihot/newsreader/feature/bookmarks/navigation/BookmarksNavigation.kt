package com.hendramarihot.newsreader.feature.bookmarks.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hendramarihot.newsreader.feature.bookmarks.BookmarksScreen
import kotlinx.serialization.Serializable

@Serializable
data object BookmarksRoute

fun NavGraphBuilder.bookmarksScreen(onArticleClick: (String) -> Unit) {
    composable<BookmarksRoute> {
        BookmarksScreen(onArticleClick = onArticleClick)
    }
}
