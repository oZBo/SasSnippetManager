package com.sassnippet.manager.ui.model

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SnippetEventBus {
    private val _events = MutableSharedFlow<SnippetEvent>()
    val events = _events.asSharedFlow()

    suspend fun emit(event: SnippetEvent) {
        _events.emit(event)
    }
}