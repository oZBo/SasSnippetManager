package com.sassnippet.manager.ui.create

import com.sassnippet.manager.arch.Middleware
import com.sassnippet.manager.repository.SnippetRepository
import com.sassnippet.manager.ui.model.SnippetEvent
import com.sassnippet.manager.ui.model.SnippetEventBus

class CreateSnippetMiddleware(
    private val repository: SnippetRepository
) : Middleware<CreateSnippetState, CreateSnippetIntent> {

    override suspend fun process(
        intent: CreateSnippetIntent,
        state: CreateSnippetState,
        next: suspend (CreateSnippetIntent) -> Unit
    ) {
        when (intent) {
            is CreateSnippetIntent.Submit -> {
                if (state.title.isBlank() || state.description.isBlank() || state.code.isBlank()) {
                    next(CreateSnippetIntent.ValidationFailed("Title, description and code are required"))
                    return
                }
                next(CreateSnippetIntent.StartLoading)
                val tags = state.tagsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                repository.create(
                    title = state.title,
                    type = state.type,
                    description = state.description,
                    code = state.code,
                    tags = tags
                )
                    .onSuccess { snippet ->
                        SnippetEventBus.emit(SnippetEvent.SnippetUpdateList)
                        next(CreateSnippetIntent.CreateSucceeded(snippet))
                    }
                    .onFailure { next(CreateSnippetIntent.CreateFailed(it.message)) }
            }

            else -> next(intent)
        }
    }
}