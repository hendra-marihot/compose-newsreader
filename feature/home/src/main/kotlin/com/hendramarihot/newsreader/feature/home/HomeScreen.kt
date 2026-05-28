package com.hendramarihot.newsreader.feature.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hendramarihot.newsreader.model.Category
import com.hendramarihot.newsreader.ui.component.ArticleCard
import com.hendramarihot.newsreader.ui.component.EmptyView
import com.hendramarihot.newsreader.ui.component.ErrorView
import com.hendramarihot.newsreader.ui.component.LoadingIndicator

@Composable
fun HomeScreen(
    onArticleClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onArticleClick = onArticleClick,
        onCategorySelected = viewModel::onCategorySelected,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onArticleClick: (String) -> Unit,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("NewsReader") },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CategoryChips(
                selectedCategory = when (uiState) {
                    is HomeUiState.Success -> uiState.selectedCategory
                    is HomeUiState.Error -> uiState.selectedCategory
                    is HomeUiState.Loading -> Category.GENERAL
                },
                onCategorySelected = onCategorySelected,
            )

            when (uiState) {
                is HomeUiState.Loading -> LoadingIndicator()
                is HomeUiState.Error -> ErrorView(
                    message = uiState.message,
                    onRetry = { onCategorySelected(uiState.selectedCategory) },
                )
                is HomeUiState.Success -> if (uiState.articles.isEmpty()) {
                    EmptyView(message = "No articles available")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
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
}

@Composable
private fun CategoryChips(
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Category.entries.forEach { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                label = { Text(category.name.lowercase().replaceFirstChar { it.uppercase() }) },
            )
        }
    }
}
