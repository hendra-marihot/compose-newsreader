package com.hendramarihot.newsreader.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hendramarihot.newsreader.feature.bookmarks.navigation.bookmarksScreen
import com.hendramarihot.newsreader.feature.detail.navigation.detailScreen
import com.hendramarihot.newsreader.feature.detail.navigation.navigateToDetail
import com.hendramarihot.newsreader.feature.home.navigation.HomeRoute
import com.hendramarihot.newsreader.feature.home.navigation.homeScreen
import com.hendramarihot.newsreader.feature.settings.navigation.settingsScreen

@Composable
fun NewsReaderNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
    ) {
        homeScreen(
            onArticleClick = navController::navigateToDetail,
        )
        detailScreen(
            onBackClick = navController::popBackStack,
        )
        bookmarksScreen(
            onArticleClick = navController::navigateToDetail,
        )
        settingsScreen()
    }
}
