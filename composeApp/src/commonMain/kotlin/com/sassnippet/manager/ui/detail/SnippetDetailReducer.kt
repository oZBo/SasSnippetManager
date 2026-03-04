package com.sassnippet.manager.ui.detail

object SnippetDetailReducer {
    fun reduce(state: SnippetDetailState, intent: SnippetDetailIntent): SnippetDetailState =
        when (intent) {
            is SnippetDetailIntent.StartLoading ->
                state.copy(isLoading = true, error = null)

            is SnippetDetailIntent.LoadSucceeded ->
                state.copy(isLoading = false, snippet = intent.snippet, error = null)

            is SnippetDetailIntent.LoadFailed ->
                state.copy(isLoading = false, error = intent.message ?: "Failed to load")

            is SnippetDetailIntent.StartEdit -> {
                val s = state.snippet ?: return state
                state.copy(
                    isEditing = true,
                    editTitle = s.title,
                    editType = s.type,
                    editDescription = s.description,
                    editCode = s.code,
                    editTagsInput = s.tags.joinToString(", "),
                    editError = null
                )
            }

            is SnippetDetailIntent.CancelEdit ->
                state.copy(isEditing = false, editError = null)

            is SnippetDetailIntent.EditTitleChanged       -> state.copy(editTitle = intent.value)
            is SnippetDetailIntent.EditTypeChanged        -> state.copy(editType = intent.value)
            is SnippetDetailIntent.EditDescriptionChanged -> state.copy(editDescription = intent.value)
            is SnippetDetailIntent.EditCodeChanged        -> state.copy(editCode = intent.value)
            is SnippetDetailIntent.EditTagsChanged        -> state.copy(editTagsInput = intent.value)

            is SnippetDetailIntent.EditValidationFailed ->
                state.copy(editError = intent.message)

            is SnippetDetailIntent.StartSaving ->
                state.copy(isSaving = true, editError = null)

            is SnippetDetailIntent.SaveSucceeded ->
                state.copy(isSaving = false, isEditing = false, snippet = intent.snippet)

            is SnippetDetailIntent.SaveFailed ->
                state.copy(isSaving = false, editError = intent.message ?: "Failed to save")

            is SnippetDetailIntent.RequestDelete ->
                state.copy(showDeleteDialog = true)

            is SnippetDetailIntent.CancelDelete ->
                state.copy(showDeleteDialog = false)

            is SnippetDetailIntent.DeleteSucceeded ->
                state.copy(isLoading = false, isDeleted = true, showDeleteDialog = false)

            is SnippetDetailIntent.DeleteFailed ->
                state.copy(isLoading = false, error = intent.message ?: "Failed to delete", showDeleteDialog = false)

            // Trigger-only — handled exclusively by Middleware
            is SnippetDetailIntent.Load,
            is SnippetDetailIntent.SaveEdit,
            is SnippetDetailIntent.ConfirmDelete -> state
        }
}