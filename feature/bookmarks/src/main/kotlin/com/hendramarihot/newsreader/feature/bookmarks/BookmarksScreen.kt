package com.hendramarihot.newsreader.feature.bookmarks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hendramarihot.newsreader.ui.component.ArticleCard
import com.hendramarihot.newsreader.ui.component.EmptyView
import com.hendramarihot.newsreader.ui.component.LoadingIndicator

@Composable
fun BookmarksScreen(
    onArticleClick: (String) -> Unit,
    viewModel: BookmarksViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BookmarksContent(
        uiState = uiState,
        onArticleClick = onArticleClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookmarksContent(
    uiState: BookmarksUiState,
    onArticleClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LargeTopAppBar(title = { Text("Bookmarks") })
        },
    ) { innerPadding ->
        when (uiState) {
            is BookmarksUiState.Loading -> LoadingIndicator()
            is BookmarksUiState.Empty -> EmptyView(
                message = "No bookmarked articles yet",
                modifier = Modifier.padding(innerPadding),
            )
            is BookmarksUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        items = uiState.articles,
                        key = { it.id },
                    ) { article ->
                        ArticleCard(
                            article = article,
                            onClick = { onArticleClick(article.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        }
    }
}
