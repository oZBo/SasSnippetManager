package com.sassnippet.manager.ui.detail

import com.sassnippet.manager.arch.Middleware
import com.sassnippet.manager.repository.SnippetRepository
import com.sassnippet.manager.ui.model.SnippetEvent
import com.sassnippet.manager.ui.model.SnippetEventBus

class SnippetDetailMiddleware(
    private val repository: SnippetRepository,
    private val snippetId: Int
) : Middleware<SnippetDetailState, SnippetDetailIntent> {

    override suspend fun process(
        intent: SnippetDetailIntent,
        state: SnippetDetailState,
        next: suspend (SnippetDetailIntent) -> Unit
    ) {
        when (intent) {
            is SnippetDetailIntent.Load -> {
                next(SnippetDetailIntent.StartLoading)
                repository.getById(snippetId)
                    .onSuccess { next(SnippetDetailIntent.LoadSucceeded(it)) }
                    .onFailure { next(SnippetDetailIntent.LoadFailed(it.message)) }
            }

            is SnippetDetailIntent.SaveEdit -> {
                if (state.editTitle.isBlank()) {
                    next(SnippetDetailIntent.EditValidationFailed("Title cannot be empty"))
                    return
                }
                next(SnippetDetailIntent.StartSaving)
                val tags = state.editTagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                repository.update(
                    id = snippetId,
                    title = state.editTitle,
                    type = state.editType,
                    description = state.editDescription,
                    code = state.editCode,
                    tags = tags
                )
                    .onSuccess { snippet ->
                        if (snippet != null) {
                            SnippetEventBus.emit(SnippetEvent.SnippetUpdateList)
                            next(SnippetDetailIntent.SaveSucceeded(snippet))
                        } else {
                            next(SnippetDetailIntent.SaveFailed("Server returned empty response"))
                        }
                    }
                    .onFailure { next(SnippetDetailIntent.SaveFailed(it.message)) }
            }

            is SnippetDetailIntent.ConfirmDelete -> {
                next(SnippetDetailIntent.StartLoading)
                repository.delete(snippetId)
                    .onSuccess { deleted ->
                        if (deleted) {
                            SnippetEventBus.emit(SnippetEvent.SnippetUpdateList)
                            next(SnippetDetailIntent.DeleteSucceeded)
                        } else {
                            next(SnippetDetailIntent.DeleteFailed("Server returned false"))
                        }
                    }
                    .onFailure { next(SnippetDetailIntent.DeleteFailed(it.message)) }
            }

            else -> next(intent)
        }
    }
}