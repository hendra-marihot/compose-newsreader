package com.hendramarihot.newsreader.feature.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hendramarihot.newsreader.feature.detail.DetailScreen
import kotlinx.serialization.Serializable

@Serializable
data class DetailRoute(val articleId: String)

fun NavController.navigateToDetail(articleId: String) {
    navigate(DetailRoute(articleId))
}

fun NavGraphBuilder.detailScreen(onBackClick: () -> Unit) {
    composable<DetailRoute> {
        DetailScreen(onBackClick = onBackClick)
    }
}
