package com.sassnippet.manager.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sassnippet.manager.arch.Store
import com.sassnippet.manager.repository.SnippetRepository
import com.sassnippet.manager.ui.model.SnippetEvent
import com.sassnippet.manager.ui.model.SnippetEventBus
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SnippetListViewModel(
    private val repository: SnippetRepository
) : ViewModel() {

    private val store = Store(
        initialState = SnippetListState(),
        reducer = SnippetListReducer::reduce,
        middlewares = listOf(SnippetListMiddleware(repository)),
        scope = viewModelScope
    )

    val state: StateFlow<SnippetListState> = store.state

    init {
        loadSnippets()
        // React to global events (snippet created / updated / deleted in another screen)
        viewModelScope.launch {
            SnippetEventBus.events.collect { event ->
                when (event) {
                    is SnippetEvent.SnippetUpdateList -> loadSnippets()
                }
            }
        }
    }

    fun loadSnippets() = store.dispatch(SnippetListIntent.LoadSnippets)
    fun search(query: String) = store.dispatch(SnippetListIntent.Search(query))

}