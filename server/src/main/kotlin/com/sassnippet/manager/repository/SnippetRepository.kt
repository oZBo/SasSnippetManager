package com.sassnippet.manager.repository

import com.sassnippet.manager.database.SnippetTable
import com.sassnippet.manager.models.CreateSnippetRequest
import com.sassnippet.manager.models.Snippet
import com.sassnippet.manager.models.SnippetType
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class SnippetRepository {

    fun getAll(): List<Snippet> = transaction {
        SnippetTable.selectAll()
            .orderBy(SnippetTable.id to SortOrder.DESC)
            .map { it.toSnippet() }
    }

    fun getById(id: Int): Snippet? = transaction {
        SnippetTable.selectAll()
            .where { SnippetTable.id eq id }
            .map { it.toSnippet() }
            .singleOrNull()
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
    }

    fun create(
        title: String,
        type: SnippetType,
        description: String,
        code: String,
        tags: List<String>
    ): Snippet = transaction {
        val insertedId = SnippetTable.insertAndGetId  {
            it[SnippetTable.title] = title
            it[SnippetTable.type] = type.name
            it[SnippetTable.description] = description
            it[SnippetTable.code] = code
            it[SnippetTable.tags] = Json.encodeToString(tags)
        }
        getById(insertedId.value)!!
    }

    fun update(id: Int, request: CreateSnippetRequest): Snippet? = transaction {
        val updated = SnippetTable.update({ SnippetTable.id eq id }) {
            it[title] = request.title
            it[type] = request.type.name
            it[description] = request.description
            it[code] = request.code
            it[tags] = Json.encodeToString(request.tags)
        }
        if (updated > 0) getById(id) else null
    }

    fun delete(id: Int): Boolean = transaction {
        SnippetTable.deleteWhere { SnippetTable.id eq id } > 0
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