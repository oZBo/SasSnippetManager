package com.sassnippet.manager.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sassnippet.manager.model.Snippet
import com.sassnippet.manager.repository.SnippetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SnippetDetailUiState(
    val snippet: Snippet? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class SnippetDetailViewModel(
    private val repository: SnippetRepository,
    private val snippetId: Int
) : ViewModel(){

    private val _uiState = MutableStateFlow(SnippetDetailUiState())
    val uiState: StateFlow<SnippetDetailUiState> = _uiState

    init {
        loadSnippet()
    }

    fun loadSnippet() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getById(snippetId)
                .onSuccess { snippet ->
                    _uiState.value = _uiState.value.copy(
                        snippet = snippet,
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