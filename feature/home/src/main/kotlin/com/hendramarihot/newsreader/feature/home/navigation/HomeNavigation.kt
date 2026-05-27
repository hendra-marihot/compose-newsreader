package com.hendramarihot.newsreader.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hendramarihot.newsreader.feature.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(onArticleClick: (String) -> Unit) {
    composable<HomeRoute> {
        HomeScreen(onArticleClick = onArticleClick)
    }
}
