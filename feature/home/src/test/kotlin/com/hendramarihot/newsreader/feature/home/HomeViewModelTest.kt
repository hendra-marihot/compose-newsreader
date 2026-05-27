package com.hendramarihot.newsreader.feature.home

import app.cash.turbine.test
import com.hendramarihot.newsreader.model.result.Result
import com.hendramarihot.newsreader.domain.GetArticlesUseCase
import com.hendramarihot.newsreader.model.Article
import com.hendramarihot.newsreader.model.Category
import com.hendramarihot.newsreader.model.Source
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getArticlesUseCase: GetArticlesUseCase = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        every { getArticlesUseCase(any()) } returns flowOf(Result.Loading)

        val viewModel = HomeViewModel(getArticlesUseCase)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Success when use case returns articles`() = runTest(testDispatcher) {
        val articles = listOf(testArticle())
        every { getArticlesUseCase(Category.GENERAL) } returns flowOf(
            Result.Success(articles),
        )

        val viewModel = HomeViewModel(getArticlesUseCase)

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()
            assertInstanceOf(HomeUiState.Success::class.java, state)
            assertEquals(articles, (state as HomeUiState.Success).articles)
            assertEquals(Category.GENERAL, state.selectedCategory)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Error when use case returns error`() = runTest(testDispatcher) {
        every { getArticlesUseCase(Category.GENERAL) } returns flowOf(
            Result.Error(RuntimeException("Network error")),
        )

        val viewModel = HomeViewModel(getArticlesUseCase)

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()
            assertInstanceOf(HomeUiState.Error::class.java, state)
            assertEquals("Network error", (state as HomeUiState.Error).message)
            assertEquals(Category.GENERAL, state.selectedCategory)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `changing category triggers new use case call`() = runTest(testDispatcher) {
        val generalArticles = listOf(testArticle(id = "1"))
        val techArticles = listOf(testArticle(id = "2"))

        every { getArticlesUseCase(Category.GENERAL) } returns flowOf(
            Result.Success(generalArticles),
        )
        every { getArticlesUseCase(Category.TECHNOLOGY) } returns flowOf(
            Result.Success(techArticles),
        )

        val viewModel = HomeViewModel(getArticlesUseCase)

        viewModel.uiState.test {
            skipItems(1)
            val generalState = awaitItem() as HomeUiState.Success
            assertEquals(generalArticles, generalState.articles)

            viewModel.onCategorySelected(Category.TECHNOLOGY)

            val techState = awaitItem() as HomeUiState.Success
            assertEquals(techArticles, techState.articles)
            assertEquals(Category.TECHNOLOGY, techState.selectedCategory)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

private fun testArticle(id: String = "test-id") = Article(
    id = id,
    title = "Test Article",
    description = "Test description",
    url = "https://example.com/article",
    content = "Test content",
    imageUrl = null,
    publishedAt = "2025-01-01T00:00:00Z",
    source = Source(id = "src-1", name = "Test Source"),
)
