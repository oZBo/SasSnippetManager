package com.sassnippet.manager.ui.detail

import com.sassnippet.manager.model.Snippet
import com.sassnippet.manager.model.SnippetType

sealed class SnippetDetailIntent {
    // User actions
    data object Load : SnippetDetailIntent()
    data object StartEdit : SnippetDetailIntent()
    data object CancelEdit : SnippetDetailIntent()
    data object SaveEdit : SnippetDetailIntent()
    data object RequestDelete : SnippetDetailIntent()
    data object CancelDelete : SnippetDetailIntent()
    data object ConfirmDelete : SnippetDetailIntent()
    // Field changes
    data class EditTitleChanged(val value: String) : SnippetDetailIntent()
    data class EditTypeChanged(val value: SnippetType) : SnippetDetailIntent()
    data class EditDescriptionChanged(val value: String) : SnippetDetailIntent()
    data class EditCodeChanged(val value: String) : SnippetDetailIntent()
    data class EditTagsChanged(val value: String) : SnippetDetailIntent()
    // Internal — dispatched by Middleware
    data object StartLoading : SnippetDetailIntent()
    data object StartSaving : SnippetDetailIntent()
    data class LoadSucceeded(val snippet: Snippet) : SnippetDetailIntent()
    data class LoadFailed(val message: String?) : SnippetDetailIntent()
    data class SaveSucceeded(val snippet: Snippet) : SnippetDetailIntent()
    data class SaveFailed(val message: String?) : SnippetDetailIntent()
    data class EditValidationFailed(val message: String) : SnippetDetailIntent()
    data object DeleteSucceeded : SnippetDetailIntent()
    data class DeleteFailed(val message: String?) : SnippetDetailIntent()
    // Convert to R
    data object ConvertToR : SnippetDetailIntent()
    data object ConvertToRLoading : SnippetDetailIntent()
    data class ConvertToRSucceeded(val rCode: String) : SnippetDetailIntent()
    data class ConvertToRFailed(val message: String?) : SnippetDetailIntent()
    data object DismissConvertResult : SnippetDetailIntent()
}