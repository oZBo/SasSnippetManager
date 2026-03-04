package com.sassnippet.manager.ui.list

import com.sassnippet.manager.arch.Middleware
import com.sassnippet.manager.repository.SnippetRepository

class SnippetListMiddleware(
    private val repository: SnippetRepository
) : Middleware<SnippetListState, SnippetListIntent> {
    override suspend fun process(
        intent: SnippetListIntent,
        state: SnippetListState,
        next: suspend (SnippetListIntent) -> Unit
    ) {
        when (intent) {
            is SnippetListIntent.LoadSnippets -> {
                next(SnippetListIntent.StartLoading)
                repository.getAll()
                    .onSuccess { next(SnippetListIntent.SnippetsLoaded(it)) }
                    .onFailure { next(SnippetListIntent.LoadFailed(it.message)) }
            }

            is SnippetListIntent.Search -> {
                // Let reducer update searchQuery + isLoading first
                next(intent)
                val result = if (intent.query.isBlank()) repository.getAll()
                else repository.search(intent.query)
                result
                    .onSuccess { next(SnippetListIntent.SnippetsLoaded(it)) }
                    .onFailure { next(SnippetListIntent.LoadFailed(it.message)) }
            }

            else -> next(intent)
        }
    }

}