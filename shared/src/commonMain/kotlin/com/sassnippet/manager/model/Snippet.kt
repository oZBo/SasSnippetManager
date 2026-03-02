package com.sassnippet.manager.model

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
)

@Serializable
enum class SnippetType {
    MACRO,
    DATA_STEP,
    PROC_SQL,
    REPORT,
    OTHER
}