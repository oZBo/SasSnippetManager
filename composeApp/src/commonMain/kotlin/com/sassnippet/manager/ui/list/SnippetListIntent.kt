package com.sassnippet.manager.ui.list

import com.sassnippet.manager.model.Snippet

sealed class SnippetListIntent {
    data object LoadSnippets : SnippetListIntent()
    data class Search(val query: String) : SnippetListIntent()
    // Internal — dispatched by Middleware after async work
    data object StartLoading : SnippetListIntent()
    data class SnippetsLoaded(val snippets: List<Snippet>) : SnippetListIntent()
    data class LoadFailed(val message: String?) : SnippetListIntent()
}