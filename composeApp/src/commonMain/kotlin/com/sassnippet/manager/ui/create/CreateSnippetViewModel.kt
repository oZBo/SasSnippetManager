package com.sassnippet.manager.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sassnippet.manager.model.SnippetType
import com.sassnippet.manager.repository.SnippetRepository
import com.sassnippet.manager.ui.model.SnippetEvent
import com.sassnippet.manager.ui.model.SnippetEventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class CreateSnippetUiState(
    val title: String = "",
    val type: SnippetType = SnippetType.OTHER,
    val description: String = "",
    val code: String = "",
    val tagsInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCreated: Boolean = false
)

class CreateSnippetViewModel(private val repository: SnippetRepository) : ViewModel() {

    private val  _uiState = MutableStateFlow(CreateSnippetUiState())
    val uiState: StateFlow<CreateSnippetUiState> = _uiState

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(title = value)
    }

    fun onTypeChange(value: SnippetType) {
        _uiState.value = _uiState.value.copy(type = value)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun onCodeChange(value: String) {
        _uiState.value = _uiState.value.copy(code = value)
    }

    fun onTagsChange(value: String) {
        _uiState.value = _uiState.value.copy(tagsInput = value)
    }

    fun create() {
        val state = _uiState.value
        if (state.title.isBlank() || state.description.isBlank() || state.code.isBlank()) {
            _uiState.value = state.copy(error = "Title, description and code are required")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            val tags = state.tagsInput
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
            repository.create(
                title = state.title,
                type = state.type,
                description = state.description,
                code = state.code,
                tags = tags
            )
                .onSuccess {
                    viewModelScope.launch {
                        SnippetEventBus.emit(SnippetEvent.SnippetCreated)
                    }

                    _uiState.value = _uiState.value.copy(isLoading = false, isCreated = true)
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