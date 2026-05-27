package com.hendramarihot.newsreader.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.hendramarihot.newsreader.feature.bookmarks.navigation.BookmarksRoute
import com.hendramarihot.newsreader.feature.home.navigation.HomeRoute
import com.hendramarihot.newsreader.feature.settings.navigation.SettingsRoute

enum class TopLevelDestination(
    val route: Any,
    val icon: ImageVector,
    val label: String,
) {
    HOME(HomeRoute, Icons.Default.Home, "Home"),
    BOOKMARKS(BookmarksRoute, Icons.Default.Bookmarks, "Bookmarks"),
    SETTINGS(SettingsRoute, Icons.Default.Settings, "Settings"),
}
