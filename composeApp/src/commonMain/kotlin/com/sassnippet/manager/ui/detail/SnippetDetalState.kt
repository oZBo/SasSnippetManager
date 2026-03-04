package com.sassnippet.manager.ui.detail

import com.sassnippet.manager.model.Snippet
import com.sassnippet.manager.model.SnippetType

data class SnippetDetailState(
    val snippet: Snippet? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isDeleted: Boolean = false,
    // Edit mode
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val editTitle: String = "",
    val editType: SnippetType = SnippetType.MACRO,
    val editDescription: String = "",
    val editCode: String = "",
    val editTagsInput: String = "",
    val editError: String? = null,
    // Delete confirmation
    val showDeleteDialog: Boolean = false
)