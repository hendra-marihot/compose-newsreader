package com.hendramarihot.newsreader.domain

import app.cash.turbine.test
import com.hendramarihot.newsreader.model.result.Result
import com.hendramarihot.newsreader.domain.repository.ArticleRepository
import com.hendramarihot.newsreader.model.Article
import com.hendramarihot.newsreader.model.Category
import com.hendramarihot.newsreader.model.Source
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

class GetArticlesUseCaseTest {

    private val repository: ArticleRepository = mockk()
    private val useCase = GetArticlesUseCase(repository)

    @Test
    fun `delegates to repository getArticles`() = runTest {
        val articles = listOf(testArticle())
        every { repository.getArticles(Category.TECHNOLOGY) } returns flowOf(
            Result.Success(articles),
        )

        useCase(Category.TECHNOLOGY).test {
            val result = awaitItem()
            assertInstanceOf(Result.Success::class.java, result)
            assertEquals(articles, (result as Result.Success).data)
            awaitComplete()
        }
    }
}

class SearchArticlesUseCaseTest {

    private val repository: ArticleRepository = mockk()
    private val useCase = SearchArticlesUseCase(repository)

    @Test
    fun `delegates to repository searchArticles`() = runTest {
        val articles = listOf(testArticle())
        every { repository.searchArticles("kotlin") } returns flowOf(
            Result.Success(articles),
        )

        useCase("kotlin").test {
            val result = awaitItem()
            assertInstanceOf(Result.Success::class.java, result)
            assertEquals(articles, (result as Result.Success).data)
            awaitComplete()
        }
    }
}

class ToggleBookmarkUseCaseTest {

    private val repository: ArticleRepository = mockk(relaxUnitFun = true)
    private val useCase = ToggleBookmarkUseCase(repository)

    @Test
    fun `delegates to repository toggleBookmark`() = runTest {
        useCase("article-123")

        coVerify { repository.toggleBookmark("article-123") }
    }
}

class GetBookmarkedArticlesUseCaseTest {

    private val repository: ArticleRepository = mockk()
    private val useCase = GetBookmarkedArticlesUseCase(repository)

    @Test
    fun `delegates to repository getBookmarkedArticles`() = runTest {
        val articles = listOf(testArticle(isBookmarked = true))
        every { repository.getBookmarkedArticles() } returns flowOf(articles)

        useCase().test {
            assertEquals(articles, awaitItem())
            awaitComplete()
        }
    }
}

private fun testArticle(isBookmarked: Boolean = false) = Article(
    id = "test-id",
    title = "Test Article",
    description = "Description",
    url = "https://example.com/article",
    content = "Content",
    imageUrl = null,
    publishedAt = "2025-01-01",
    source = Source(id = "src", name = "Source"),
    isBookmarked = isBookmarked,
)
