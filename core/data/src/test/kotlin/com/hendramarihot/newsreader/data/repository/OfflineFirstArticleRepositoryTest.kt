package com.hendramarihot.newsreader.data.repository

import app.cash.turbine.test
import com.hendramarihot.newsreader.model.result.Result
import com.hendramarihot.newsreader.database.dao.ArticleDao
import com.hendramarihot.newsreader.database.model.ArticleEntity
import com.hendramarihot.newsreader.model.Category
import com.hendramarihot.newsreader.network.NewsApi
import com.hendramarihot.newsreader.network.model.NetworkArticle
import com.hendramarihot.newsreader.network.model.NetworkResponse
import com.hendramarihot.newsreader.network.model.NetworkSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFirstArticleRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val newsApi: NewsApi = mockk()
    private val articleDao: ArticleDao = mockk(relaxUnitFun = true)

    private lateinit var repository: OfflineFirstArticleRepository

    @BeforeEach
    fun setUp() {
        repository = OfflineFirstArticleRepository(
            newsApi = newsApi,
            articleDao = articleDao,
            ioDispatcher = testDispatcher,
        )
        coEvery { articleDao.getBookmarkedIds(any()) } returns emptyList()
    }

    @Test
    fun `getArticles emits Loading then forwards cached data by category`() = runTest(testDispatcher) {
        val entities = listOf(testEntity(category = "general"))
        every { articleDao.getArticlesByCategory("general") } returns flowOf(entities)
        coEvery { newsApi.getTopHeadlines(category = "general") } returns NetworkResponse()

        repository.getArticles(Category.GENERAL).test {
            val loading = awaitItem()
            assertInstanceOf(Result.Loading::class.java, loading)

            val success = awaitItem()
            assertInstanceOf(Result.Success::class.java, success)
            assertEquals(1, (success as Result.Success).data.size)
            assertEquals("Test Article", success.data.first().title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getArticles still emits cached data when network refresh fails`() = runTest(testDispatcher) {
        val entities = listOf(testEntity(category = "general"))
        every { articleDao.getArticlesByCategory("general") } returns flowOf(entities)
        coEvery { newsApi.getTopHeadlines(category = any()) } throws RuntimeException("Network error")

        repository.getArticles(Category.GENERAL).test {
            assertInstanceOf(Result.Loading::class.java, awaitItem())

            val success = awaitItem()
            assertInstanceOf(Result.Success::class.java, success)
            assertEquals(1, (success as Result.Success).data.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getArticle returns mapped article from dao`() = runTest(testDispatcher) {
        val entity = testEntity()
        every { articleDao.getArticleById("test-id") } returns flowOf(entity)

        repository.getArticle("test-id").test {
            val article = checkNotNull(awaitItem()) { "Expected non-null article" }
            assertEquals("Test Article", article.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleBookmark uses atomic toggle query`() = runTest(testDispatcher) {
        repository.toggleBookmark("test-id")

        coVerify { articleDao.toggleBookmark("test-id") }
    }

    @Test
    fun `getBookmarkedArticles returns mapped articles`() = runTest(testDispatcher) {
        val entities = listOf(testEntity(isBookmarked = true))
        every { articleDao.getBookmarkedArticles() } returns flowOf(entities)

        repository.getBookmarkedArticles().test {
            val articles = awaitItem()
            assertEquals(1, articles.size)
            assertEquals(true, articles.first().isBookmarked)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchArticles emits results from network`() = runTest(testDispatcher) {
        val networkArticles = listOf(
            NetworkArticle(
                source = NetworkSource(id = "src", name = "Source"),
                title = "Search Result",
                url = "https://example.com/search/1",
                publishedAt = "2025-01-01",
            ),
        )
        coEvery { newsApi.searchArticles(query = "test") } returns NetworkResponse(
            articles = networkArticles,
        )

        repository.searchArticles("test").test {
            assertInstanceOf(Result.Loading::class.java, awaitItem())

            val success = awaitItem()
            assertInstanceOf(Result.Success::class.java, success)
            assertEquals("Search Result", (success as Result.Success).data.first().title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchArticles emits Error on network failure`() = runTest(testDispatcher) {
        coEvery { newsApi.searchArticles(query = "fail") } throws RuntimeException("Timeout")

        repository.searchArticles("fail").test {
            assertInstanceOf(Result.Loading::class.java, awaitItem())

            val error = awaitItem()
            assertInstanceOf(Result.Error::class.java, error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchArticles filters out articles with empty URLs`() = runTest(testDispatcher) {
        val networkArticles = listOf(
            NetworkArticle(
                source = NetworkSource(id = "src", name = "Source"),
                title = "Valid",
                url = "https://example.com/1",
                publishedAt = "2025-01-01",
            ),
            NetworkArticle(
                source = NetworkSource(id = "src", name = "Source"),
                title = "Removed",
                url = "",
                publishedAt = "2025-01-01",
            ),
        )
        coEvery { newsApi.searchArticles(query = "test") } returns NetworkResponse(
            articles = networkArticles,
        )

        repository.searchArticles("test").test {
            awaitItem() // Loading
            val success = awaitItem() as Result.Success
            assertEquals(1, success.data.size)
            assertEquals("Valid", success.data.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchArticles reflects persisted bookmark state in emitted results`() = runTest(testDispatcher) {
        val networkArticles = listOf(
            NetworkArticle(
                source = NetworkSource(id = "src", name = "Source"),
                title = "Bookmarked Result",
                url = "https://example.com/bookmarked",
                publishedAt = "2025-01-01",
            ),
        )
        coEvery { newsApi.searchArticles(query = "test") } returns NetworkResponse(
            articles = networkArticles,
        )
        // Simulate that every upserted article is already bookmarked in the DB.
        coEvery { articleDao.getBookmarkedIds(any()) } answers { firstArg() }

        repository.searchArticles("test").test {
            awaitItem() // Loading
            val success = awaitItem() as Result.Success
            assertEquals(true, success.data.first().isBookmarked)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

private fun testEntity(
    isBookmarked: Boolean = false,
    category: String = "",
) = ArticleEntity(
    id = "test-id",
    title = "Test Article",
    description = "Description",
    url = "https://example.com/article",
    content = "Content",
    imageUrl = null,
    publishedAt = "2025-01-01T00:00:00Z",
    sourceId = "src-1",
    sourceName = "Source",
    category = category,
    isBookmarked = isBookmarked,
    cachedAt = System.currentTimeMillis(),
)
