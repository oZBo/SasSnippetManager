package com.sassnippet.manager.repository

import com.sassnippet.manager.database.SnippetTable
import com.sassnippet.manager.models.CreateSnippetRequest
import com.sassnippet.manager.models.PagedSnippetResponse
import com.sassnippet.manager.models.Snippet
import com.sassnippet.manager.models.SnippetType
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class SnippetRepository {

    private val logger = LoggerFactory.getLogger(SnippetRepository::class.java)

    fun getAll(): List<Snippet> = transaction {
        SnippetTable.selectAll()
            .orderBy(SnippetTable.id to SortOrder.DESC)
            .map { it.toSnippet() }
            .also { logger.info("getAll → ${it.size} snippets") }
    }

    fun getById(id: Int): Snippet? = transaction {
        SnippetTable.selectAll()
            .where { SnippetTable.id eq id }
            .map { it.toSnippet() }
            .singleOrNull()
            .also { logger.info("getById($id) → ${if (it != null) "found" else "not found"}") }
    }

    fun getPaged(page: Int, pageSize: Int): PagedSnippetResponse<Snippet> = transaction {
        val total = SnippetTable.selectAll().count().toInt()
        val items = SnippetTable.selectAll()
            .orderBy(SnippetTable.id to SortOrder.DESC)
            .limit(pageSize).offset(((page - 1) * pageSize).toLong())
            .map { it.toSnippet() }
        val totalPages = (total + pageSize - 1) /pageSize
        PagedSnippetResponse(data = items, total = total, page = page, pageSize = pageSize, totalPages = totalPages)
            .also { logger.info("getPaged(page=$page, pageSize=$pageSize) → ${items.size}/$total snippets") }
    }

    fun search(query: String): List<Snippet> = transaction {
        SnippetTable.selectAll()
            .where {
                (SnippetTable.description.lowerCase() like "%${query.lowercase()}%") or
                        (SnippetTable.tags.lowerCase() like "%${query.lowercase()}%") or
                        (SnippetTable.title.lowerCase() like "%${query.lowercase()}%")
            }
            .orderBy(SnippetTable.id to SortOrder.DESC)
            .map { it.toSnippet() }
            .also { logger.info("search('$query') → ${it.size} snippets") }
    }

    fun create(
        title: String,
        type: SnippetType,
        description: String,
        code: String,
        tags: List<String>
    ): Snippet = transaction {
        logger.info("create → title='$title', type=$type")
        val insertedId = SnippetTable.insertAndGetId {
            it[SnippetTable.title] = title
            it[SnippetTable.type] = type.name
            it[SnippetTable.description] = description
            it[SnippetTable.code] = code
            it[SnippetTable.tags] = Json.encodeToString(tags)
        }
        getById(insertedId.value)!!
            .also { logger.info("create → done, id=${it.id}") }
    }

    fun update(id: Int, request: CreateSnippetRequest): Snippet? = transaction {
        logger.info("update($id) → title='${request.title}', type=${request.type}")
        val updated = SnippetTable.update({ SnippetTable.id eq id }) {
            it[title] = request.title
            it[type] = request.type.name
            it[description] = request.description
            it[code] = request.code
            it[tags] = Json.encodeToString(request.tags)
        }
        if (updated > 0) getById(id) else null
            .also { logger.info("update($id) → ${"not found"}") }
    }

    fun delete(id: Int): Boolean = transaction {
        logger.info("delete($id)")
        SnippetTable.deleteWhere { SnippetTable.id eq id } > 0
            .also { logger.info("delete($id)") }
    }

    private fun ResultRow.toSnippet() = Snippet(
        id = this[SnippetTable.id].value,
        title = this[SnippetTable.title],
        type = SnippetType.valueOf(this[SnippetTable.type]),
        description = this[SnippetTable.description],
        code = this[SnippetTable.code],
        tags = Json.decodeFromString(this[SnippetTable.tags])
    )

}