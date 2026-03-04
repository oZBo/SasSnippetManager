package com.sassnippet.manager.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sassnippet.manager.arch.Store
import com.sassnippet.manager.repository.SnippetRepository
import kotlinx.coroutines.flow.StateFlow

class CreateSnippetViewModel(
    private val repository: SnippetRepository
) : ViewModel() {

    private val store = Store(
        initialState = CreateSnippetState(),
        reducer = CreateSnippetReducer::reduce,
        middlewares = listOf(CreateSnippetMiddleware(repository)),
        scope = viewModelScope
    )

    val state: StateFlow<CreateSnippetState> = store.state

    fun dispatch(intent: CreateSnippetIntent) = store.dispatch(intent)

}