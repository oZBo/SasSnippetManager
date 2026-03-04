package com.sassnippet.manager.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sassnippet.manager.arch.Store
import com.sassnippet.manager.model.Snippet
import com.sassnippet.manager.model.SnippetType
import com.sassnippet.manager.repository.SnippetRepository
import com.sassnippet.manager.ui.model.SnippetEvent
import com.sassnippet.manager.ui.model.SnippetEventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SnippetDetailViewModel(
    private val repository: SnippetRepository,
    private val snippetId: Int
) : ViewModel() {

    private val store = Store(
        initialState = SnippetDetailState(isLoading = true),
        reducer = SnippetDetailReducer::reduce,
        middlewares = listOf(SnippetDetailMiddleware(repository, snippetId)),
        scope = viewModelScope
    )

    val state: StateFlow<SnippetDetailState> = store.state

    init {
        store.dispatch(SnippetDetailIntent.Load)
    }

    fun dispatch(intent: SnippetDetailIntent) = store.dispatch(intent)
}