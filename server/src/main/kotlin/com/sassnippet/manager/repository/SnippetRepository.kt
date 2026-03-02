package com.sassnippet.manager.repository

import com.sassnippet.manager.database.SnippetTable
import com.sassnippet.manager.models.Snippet
import com.sassnippet.manager.models.SnippetType
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
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

    private fun ResultRow.toSnippet() = Snippet(
        id = this[SnippetTable.id].value,
        title = this[SnippetTable.title],
        type = SnippetType.valueOf(this[SnippetTable.type]),
        description = this[SnippetTable.description],
        code = this[SnippetTable.code],
        tags = Json.decodeFromString(this[SnippetTable.tags])
    )

}