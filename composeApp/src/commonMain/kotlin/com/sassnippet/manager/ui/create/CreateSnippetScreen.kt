package com.sassnippet.manager.ui.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sassnippet.manager.model.SnippetType
import com.sassnippet.manager.repository.SnippetRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSnippetScreen(
    repository: SnippetRepository,
    onBack: () -> Unit,
    onCreated: () -> Unit
) {

    val viewModel = viewModel { CreateSnippetViewModel(repository) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isCreated) {
        if (uiState.isCreated) onCreated()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("New Snippet") }, navigationIcon = {
                TextButton(onClick = onBack) { Text("Back") }
            })
        }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            // Type dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = uiState.type.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = MaterialTheme.shapes.medium,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded, onDismissRequest = { expanded = false }) {
                    SnippetType.entries.forEach { type ->
                        DropdownMenuItem(text = { Text(type.name) }, onClick = {
                            viewModel.onTypeChange(type)
                            expanded = false
                        })
                    }
                }
            }

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = uiState.code,
                onValueChange = { viewModel.onCodeChange(it) },
                label = { Text("Code") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                maxLines = 15,
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = uiState.tagsInput,
                onValueChange = { viewModel.onTagsChange(it) },
                label = { Text("Tags (comma separated)") },
                placeholder = { Text("sql, join, basic") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { viewModel.create() }, modifier = Modifier.fillMaxWidth(), enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp), strokeWidth = 2.dp
                    )
                } else {
                    Text("Save Snippet")
                }
            }
        }
    }

}