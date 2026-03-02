package com.sassnippet.manager.ui.model

sealed class SnippetEvent {
    data object SnippetCreated : SnippetEvent()
}