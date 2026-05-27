package com.hendramarihot.newsreader.data.model

import com.hendramarihot.newsreader.database.model.ArticleEntity
import com.hendramarihot.newsreader.network.model.NetworkArticle
import com.hendramarihot.newsreader.network.model.NetworkSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class MappersTest {

    @Test
    fun `toEntity generates stable hash id from url`() {
        val article = NetworkArticle(
            source = NetworkSource(id = "src", name = "Source"),
            title = "Title",
            url = "https://example.com/article?id=1&ref=home",
            publishedAt = "2025-01-01",
        )

        val entity = article.toEntity()

        assertNotEquals(article.url, entity.id)
        assertEquals(12, entity.id.length)
        assertEquals(article.url, entity.url)
    }

    @Test
    fun `toEntity produces same id for same url`() {
        val url = "https://example.com/article"
        val article1 = NetworkArticle(url = url)
        val article2 = NetworkArticle(url = url, title = "Different Title")

        assertEquals(article1.toEntity().id, article2.toEntity().id)
    }

    @Test
    fun `toEntity produces different ids for different urls`() {
        val article1 = NetworkArticle(url = "https://example.com/1")
        val article2 = NetworkArticle(url = "https://example.com/2")

        assertNotEquals(article1.toEntity().id, article2.toEntity().id)
    }

    @Test
    fun `toArticle maps all fields correctly`() {
        val entity = ArticleEntity(
            id = "hash123",
            title = "Title",
            description = "Desc",
            url = "https://example.com",
            content = "Content",
            imageUrl = "https://img.com/1.jpg",
            publishedAt = "2025-01-01",
            sourceId = "src-1",
            sourceName = "Source",
            isBookmarked = true,
            cachedAt = 1000L,
        )

        val article = entity.toArticle()

        assertEquals("hash123", article.id)
        assertEquals("Title", article.title)
        assertEquals("Desc", article.description)
        assertEquals("https://example.com", article.url)
        assertEquals("Content", article.content)
        assertEquals("https://img.com/1.jpg", article.imageUrl)
        assertEquals("2025-01-01", article.publishedAt)
        assertEquals("src-1", article.source.id)
        assertEquals("Source", article.source.name)
        assertEquals(true, article.isBookmarked)
    }

    @Test
    fun `toStableId id is navigation-safe (no special chars)`() {
        val url = "https://example.com/path/to/article?q=test&page=1#section"
        val id = url.toStableId()

        assert(id.all { it.isLetterOrDigit() }) {
            "ID '$id' contains non-alphanumeric characters"
        }
    }
}
