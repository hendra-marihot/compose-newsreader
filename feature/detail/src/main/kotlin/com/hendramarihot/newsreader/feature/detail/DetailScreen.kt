package com.hendramarihot.newsreader.feature.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.hendramarihot.newsreader.common.extensions.toFormattedDate
import com.hendramarihot.newsreader.model.Article
import com.hendramarihot.newsreader.ui.component.ErrorView
import com.hendramarihot.newsreader.ui.component.LoadingIndicator

@Composable
fun DetailScreen(
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onBookmarkToggle = viewModel::onBookmarkToggle,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailContent(
    uiState: DetailUiState,
    onBackClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            if (uiState is DetailUiState.Success) {
                FloatingActionButton(onClick = onBookmarkToggle) {
                    Icon(
                        imageVector = if (uiState.article.isBookmarked) {
                            Icons.Default.Bookmark
                        } else {
                            Icons.Default.BookmarkBorder
                        },
                        contentDescription = "Toggle bookmark",
                    )
                }
            }
        },
    ) { innerPadding ->
        when (uiState) {
            is DetailUiState.Loading -> LoadingIndicator()
            is DetailUiState.Error -> ErrorView(message = uiState.message)
            is DetailUiState.Success -> ArticleDetail(
                article = uiState.article,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun ArticleDetail(
    article: Article,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        article.imageUrl?.let { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
            )
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${article.source.name} · ${article.publishedAt.toFormattedDate()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            article.content?.let { content ->
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}
