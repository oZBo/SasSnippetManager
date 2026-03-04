package com.sassnippet.manager.ui.list

import com.sassnippet.manager.model.Snippet

data class SnippetListState(
    val snippets: List<Snippet> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)
