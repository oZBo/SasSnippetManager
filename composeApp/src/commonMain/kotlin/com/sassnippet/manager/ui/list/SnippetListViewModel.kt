package com.sassnippet.manager.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sassnippet.manager.model.Snippet
import com.sassnippet.manager.repository.SnippetRepository
import com.sassnippet.manager.ui.model.SnippetEvent
import com.sassnippet.manager.ui.model.SnippetEventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SnippetListUiState(
    val snippets: List<Snippet> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

class SnippetListViewModel(private val repository: SnippetRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SnippetListUiState())
    val uiState: StateFlow<SnippetListUiState> = _uiState

    init {
        loadSnippets()
        viewModelScope.launch {
            SnippetEventBus.events.collect { event ->
                when (event) {
                    is SnippetEvent.SnippetCreated -> loadSnippets()
                }
            }
        }
    }

    fun loadSnippets() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getAll()
                .onSuccess { snippetList ->
                    _uiState.value = _uiState.value.copy(
                        snippets = snippetList,
                        isLoading = false,
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun search(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch {
            if (query.isBlank()) {
                loadSnippets()
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                repository.search(query)
                    .onSuccess { snippets ->
                        _uiState.value = _uiState.value.copy(
                            snippets = snippets,
                            isLoading = false
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
            }
        }
    }

}