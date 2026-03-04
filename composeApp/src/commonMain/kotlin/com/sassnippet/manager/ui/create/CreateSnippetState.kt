package com.sassnippet.manager.ui.create

import com.sassnippet.manager.model.SnippetType

data class CreateSnippetState(
    val title: String = "",
    val type: SnippetType = SnippetType.OTHER,
    val description: String = "",
    val code: String = "",
    val tagsInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCreated: Boolean = false
)