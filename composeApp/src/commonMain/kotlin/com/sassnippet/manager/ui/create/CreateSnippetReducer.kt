package com.sassnippet.manager.ui.create

object CreateSnippetReducer {
    fun reduce(state: CreateSnippetState, intent: CreateSnippetIntent): CreateSnippetState =
        when (intent) {
            is CreateSnippetIntent.TitleChanged       -> state.copy(title = intent.value)
            is CreateSnippetIntent.TypeChanged        -> state.copy(type = intent.value)
            is CreateSnippetIntent.DescriptionChanged -> state.copy(description = intent.value)
            is CreateSnippetIntent.CodeChanged        -> state.copy(code = intent.value)
            is CreateSnippetIntent.TagsChanged        -> state.copy(tagsInput = intent.value)
            is CreateSnippetIntent.StartLoading       -> state.copy(isLoading = true, error = null)
            is CreateSnippetIntent.ValidationFailed   -> state.copy(error = intent.message)
            is CreateSnippetIntent.CreateSucceeded    -> state.copy(isLoading = false, isCreated = true)
            is CreateSnippetIntent.CreateFailed       -> state.copy(isLoading = false, error = intent.message ?: "Failed to create")
            is CreateSnippetIntent.Submit             -> state  // handled only by Middleware
        }
}