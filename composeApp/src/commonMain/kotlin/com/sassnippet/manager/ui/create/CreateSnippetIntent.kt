package com.sassnippet.manager.ui.create

import com.sassnippet.manager.model.Snippet
import com.sassnippet.manager.model.SnippetType

sealed class CreateSnippetIntent {
    // User-initiated field changes
    data class TitleChanged(val value: String) : CreateSnippetIntent()
    data class TypeChanged(val value: SnippetType) : CreateSnippetIntent()
    data class DescriptionChanged(val value: String) : CreateSnippetIntent()
    data class CodeChanged(val value: String) : CreateSnippetIntent()
    data class TagsChanged(val value: String) : CreateSnippetIntent()
    // User-initiated action
    data object Submit : CreateSnippetIntent()
    // Internal — dispatched by Middleware
    data object StartLoading : CreateSnippetIntent()
    data class ValidationFailed(val message: String) : CreateSnippetIntent()
    data class CreateSucceeded(val snippet: Snippet) : CreateSnippetIntent()
    data class CreateFailed(val message: String?) : CreateSnippetIntent()
}