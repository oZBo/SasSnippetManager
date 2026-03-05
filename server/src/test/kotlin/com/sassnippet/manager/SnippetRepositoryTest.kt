package com.sassnippet.manager

import com.sassnippet.manager.models.CreateSnippetRequest
import com.sassnippet.manager.models.SnippetType
import com.sassnippet.manager.repository.SnippetRepository
import kotlin.test.*

class SnippetRepositoryTest {

    private lateinit var repository: SnippetRepository

    @BeforeTest
    fun setup() {
        TestDatabaseFactory.init()
        repository = SnippetRepository()
    }

    @AfterTest
    fun teardown() {
        TestDatabaseFactory.cleanup()
    }

    @Test
    fun `getAll returns empty list when no snippets`() {
        val result = repository.getAll()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `create snippet and getAll returns it`() {
        repository.create("Test Title", SnippetType.MACRO, "desc", "code %macro;", listOf("tag1"))
        val result = repository.getAll()
        assertEquals(1, result.size)
        assertEquals("Test Title", result.first().title)
    }

    @Test
    fun `getById returns correct snippet`() {
        val created = repository.create("My Snippet", SnippetType.PROC_SQL, "desc", "proc sql;", emptyList())
        val found = repository.getById(created.id)
        assertNotNull(found)
        assertEquals(created.id, found.id)
        assertEquals("My Snippet", found.title)
    }

    @Test
    fun `getById returns null for non-existing id`() {
        val result = repository.getById(999)
        assertNull(result)
    }

    @Test
    fun `search finds snippet by title`() {
        repository.create("Proc SQL snippet", SnippetType.PROC_SQL, "desc", "proc sql;", emptyList())
        repository.create("Data Step snippet", SnippetType.DATA_STEP, "desc", "data work;", emptyList())
        val result = repository.search("Proc")
        assertEquals(1, result.size)
        assertEquals("Proc SQL snippet", result.first().title)
    }

    @Test
    fun `search finds snippet by tag`() {
        repository.create("Title", SnippetType.MACRO, "desc", "code", listOf("reporting", "proc"))
        val result = repository.search("reporting")
        assertEquals(1, result.size)
    }

    @Test
    fun `search returns empty when no match`() {
        repository.create("Title", SnippetType.MACRO, "desc", "code", emptyList())
        val result = repository.search("zzznomatch")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `update modifies existing snippet`() {
        val created = repository.create("Old Title", SnippetType.MACRO, "old desc", "old code", emptyList())
        val request = CreateSnippetRequest("New Title", SnippetType.DATA_STEP, "new desc", "new code", listOf("tag1"))
        val updated = repository.update(created.id, request)
        assertNotNull(updated)
        assertEquals("New Title", updated.title)
        assertEquals(SnippetType.DATA_STEP, updated.type)
    }

    @Test
    fun `update returns null for non-existing id`() {
        val request = CreateSnippetRequest("Title", SnippetType.MACRO, "desc", "code", emptyList())
        val result = repository.update(999, request)
        assertNull(result)
    }

    @Test
    fun `delete removes existing snippet`() {
        val created = repository.create("To Delete", SnippetType.OTHER, "desc", "code", emptyList())
        val deleted = repository.delete(created.id)
        assertTrue(deleted)
        assertNull(repository.getById(created.id))
    }

    @Test
    fun `delete returns false for non-existing id`() {
        val result = repository.delete(999)
        assertFalse(result)
    }

    @Test
    fun `getPaged returns correct page`() {
        repeat(5) { i ->
            repository.create("Snippet $i", SnippetType.OTHER, "desc", "code", emptyList())
        }
        val result = repository.getPaged(page = 1, pageSize = 3)
        assertEquals(3, result.data.size)
        assertEquals(5, result.total)
        assertEquals(2, result.totalPages)
    }
}