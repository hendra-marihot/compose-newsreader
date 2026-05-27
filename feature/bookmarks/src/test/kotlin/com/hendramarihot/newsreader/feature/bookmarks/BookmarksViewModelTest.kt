package com.hendramarihot.newsreader.feature.bookmarks

import app.cash.turbine.test
import com.hendramarihot.newsreader.domain.GetBookmarkedArticlesUseCase
import com.hendramarihot.newsreader.model.Article
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
class BookmarksViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getBookmarkedArticlesUseCase: GetBookmarkedArticlesUseCase = mockk()

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
        every { getBookmarkedArticlesUseCase() } returns flowOf(emptyList())

        val viewModel = BookmarksViewModel(getBookmarkedArticlesUseCase)

        assertEquals(BookmarksUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `emits Empty when no bookmarked articles`() = runTest(testDispatcher) {
        every { getBookmarkedArticlesUseCase() } returns flowOf(emptyList())

        val viewModel = BookmarksViewModel(getBookmarkedArticlesUseCase)

        viewModel.uiState.test {
            skipItems(1)
            assertEquals(BookmarksUiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Success with articles when bookmarks exist`() = runTest(testDispatcher) {
        val articles = listOf(testArticle())
        every { getBookmarkedArticlesUseCase() } returns flowOf(articles)

        val viewModel = BookmarksViewModel(getBookmarkedArticlesUseCase)

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()
            assertInstanceOf(BookmarksUiState.Success::class.java, state)
            assertEquals(articles, (state as BookmarksUiState.Success).articles)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

private fun testArticle() = Article(
    id = "test-id",
    title = "Bookmarked Article",
    description = "Description",
    url = "https://example.com/article",
    content = "Content",
    imageUrl = null,
    publishedAt = "2025-01-01T00:00:00Z",
    source = Source(id = "src-1", name = "Source"),
    isBookmarked = true,
)
