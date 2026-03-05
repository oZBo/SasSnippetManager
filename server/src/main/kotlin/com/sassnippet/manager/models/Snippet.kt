package com.sassnippet.manager.models

import kotlinx.serialization.Serializable

@Serializable
data class Snippet(
    val id: Int,
    val title: String,
    val type: SnippetType,
    val description: String,
    val code: String,
    val tags: List<String> = emptyList()
)

@Serializable
data class CreateSnippetRequest(
    val title: String,
    val type: SnippetType,
    val description: String,
    val code: String,
    val tags: List<String> = emptyList()
) {
    fun validate() {
        require(title.isNotBlank()) { "title cannot be blank" }
        require(code.isNotBlank()) { "code cannot be blank" }
        require(title.length <= 255) { "title too long" }
    }
}

@Serializable
data class PagedSnippetResponse<T>(
    val data: List<T>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)

@Serializable
enum class SnippetType {
    MACRO,
    DATA_STEP,
    PROC_SQL,
    REPORT,
    OTHER
}