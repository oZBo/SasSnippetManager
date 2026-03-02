package com.sassnippet.manager.repository

import com.sassnippet.manager.model.CreateSnippetRequest
import com.sassnippet.manager.model.Snippet
import com.sassnippet.manager.model.SnippetType
import com.sassnippet.manager.network.SnippetApiClient

class SnippetRepository(private val apiClient: SnippetApiClient) {

    suspend fun getAll(): Result<List<Snippet>> = runCatching {
        apiClient.getAll()
    }

    suspend fun getById(id: Int): Result<Snippet> = runCatching {
        apiClient.getById(id)
    }

    suspend fun search(query: String): Result<List<Snippet>> = runCatching {
        apiClient.search(query)
    }

    suspend fun create(
        title: String,
        type: SnippetType,
        description: String,
        code: String,
        tags: List<String>
    ): Result<Snippet> = runCatching {
        apiClient.create(
            CreateSnippetRequest(
                title = title,
                type = type,
                description = description,
                code = code,
                tags = tags
            )
        )
    }
}