package com.sassnippet.manager.ui.list

object SnippetListReducer {
    fun reduce(state: SnippetListState, intent: SnippetListIntent): SnippetListState =
        when (intent) {
            is SnippetListIntent.StartLoading -> state.copy(isLoading = true, error = null)
            // Update query + trigger loading immediately in state
            is SnippetListIntent.Search -> state.copy(searchQuery = intent.query, isLoading = true, error = null)
            is SnippetListIntent.SnippetsLoaded -> state.copy(snippets = intent.snippets, isLoading = false, error = null)
            is SnippetListIntent.LoadFailed -> state.copy(isLoading = false, error = intent.message ?: "Unknown error")
            // Trigger-only — Middleware intercepts these, state unchanged
            is SnippetListIntent.LoadSnippets -> state
        }
}