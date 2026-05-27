package com.hendramarihot.newsreader.feature.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.hendramarihot.newsreader.domain.GetArticleUseCase
import com.hendramarihot.newsreader.domain.ToggleBookmarkUseCase
import com.hendramarihot.newsreader.model.Article
import com.hendramarihot.newsreader.model.Source
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getArticleUseCase: GetArticleUseCase = mockk()
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase = mockk(relaxUnitFun = true)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createSavedStateHandle(articleId: String): SavedStateHandle =
        SavedStateHandle(mapOf("articleId" to articleId))

    @Test
    fun `emits Success when article is found`() = runTest(testDispatcher) {
        val article = testArticle()
        every { getArticleUseCase("test-id") } returns flowOf(article)

        val viewModel = DetailViewModel(
            savedStateHandle = createSavedStateHandle("test-id"),
            getArticleUseCase = getArticleUseCase,
            toggleBookmarkUseCase = toggleBookmarkUseCase,
        )

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()
            assertInstanceOf(DetailUiState.Success::class.java, state)
            assertEquals(article, (state as DetailUiState.Success).article)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Error when article is not found`() = runTest(testDispatcher) {
        every { getArticleUseCase("missing-id") } returns flowOf(null)

        val viewModel = DetailViewModel(
            savedStateHandle = createSavedStateHandle("missing-id"),
            getArticleUseCase = getArticleUseCase,
            toggleBookmarkUseCase = toggleBookmarkUseCase,
        )

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()
            assertInstanceOf(DetailUiState.Error::class.java, state)
            assertEquals("Article not found", (state as DetailUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBookmarkToggle calls use case with correct id`() = runTest(testDispatcher) {
        every { getArticleUseCase("test-id") } returns flowOf(testArticle())

        val viewModel = DetailViewModel(
            savedStateHandle = createSavedStateHandle("test-id"),
            getArticleUseCase = getArticleUseCase,
            toggleBookmarkUseCase = toggleBookmarkUseCase,
        )

        viewModel.onBookmarkToggle()
        advanceUntilIdle()

        coVerify { toggleBookmarkUseCase("test-id") }
    }
}

private fun testArticle() = Article(
    id = "test-id",
    title = "Detail Article",
    description = "Description",
    url = "https://example.com/article",
    content = "Full content",
    imageUrl = "https://example.com/image.jpg",
    publishedAt = "2025-01-01T00:00:00Z",
    source = Source(id = "src-1", name = "Source"),
)
