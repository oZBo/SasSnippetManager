// composeApp/src/commonMain/kotlin/com/sassnippet/manager/ui/detail/SnippetDetailScreen.kt

package com.sassnippet.manager.ui.detail

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.sassnippet.manager.model.SnippetType
import com.sassnippet.manager.ui.util.RFormatter
import com.sassnippet.manager.ui.util.SasFormatter
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnippetDetailScreen(
    snippetId: Int,
    onBack: () -> Unit,
    // SnippetDetailViewModel gets snippetId injected via Koin parametersOf
    viewModel: SnippetDetailViewModel = koinViewModel(parameters = { parametersOf(snippetId) })
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) onBack()
    }

    val clipboardManager = LocalClipboardManager.current
    var rCopied by remember { mutableStateOf(false) }
    val formattedRCode = remember(state.convertedRCode) {
        state.convertedRCode?.let { RFormatter.formatRCode(it) }
    }

    // Convert to R dialog (loading / result / error)
    if (state.isConverting || state.convertedRCode != null || state.convertError != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dispatch(SnippetDetailIntent.DismissConvertResult) },
            title = { Text("Convert to R") },
            text = {
                when {
                    state.isConverting -> Box(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(8.dp))
                            Text("Converting with AI...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    state.convertError != null -> Text(
                        text = "Error: ${state.convertError}",
                        color = MaterialTheme.colorScheme.error
                    )
                    state.convertedRCode != null -> {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("R Code", style = MaterialTheme.typography.labelLarge)
                                TextButton(onClick = {
                                    clipboardManager.setText(AnnotatedString(state.convertedRCode!!))
                                    rCopied = true
                                }) {
                                    Text(if (rCopied) "Copied!" else "Copy")
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = formattedRCode!!,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                        .padding(12.dp),
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.bodySmall,
                                    softWrap = false
                                )
                            }
                            state.rCodeSaveError?.let {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (state.convertedRCode != null) {
                    TextButton(
                        onClick = { viewModel.dispatch(SnippetDetailIntent.SaveRCode) },
                        enabled = !state.isSavingRCode
                    ) {
                        if (state.isSavingRCode) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Save")
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dispatch(SnippetDetailIntent.DismissConvertResult) }) {
                    Text("Close")
                }
            }
        )
    }

    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dispatch(SnippetDetailIntent.CancelDelete) },
            title = { Text("Delete Snippet") },
            text = { Text("Are you sure you want to delete \"${state.snippet?.title}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.dispatch(SnippetDetailIntent.ConfirmDelete) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dispatch(SnippetDetailIntent.CancelDelete) }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isEditing) "Edit Snippet" else state.snippet?.title ?: "Detail",
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    TextButton(onClick = {
                        if (state.isEditing) viewModel.dispatch(SnippetDetailIntent.CancelEdit) else onBack()
                    }) {
                        Text(if (state.isEditing) "Cancel" else "Back")
                    }
                },
                actions = {
                    if (!state.isLoading && state.snippet != null) {
                        if (state.isEditing) {
                            TextButton(
                                onClick = { viewModel.dispatch(SnippetDetailIntent.SaveEdit) },
                                enabled = !state.isSaving
                            ) {
                                if (state.isSaving) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("Save")
                                }
                            }
                        } else {
                            IconButton(onClick = { viewModel.dispatch(SnippetDetailIntent.StartEdit) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit snippet")
                            }
                            IconButton(onClick = { viewModel.dispatch(SnippetDetailIntent.RequestDelete) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete snippet",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding)) {
            val isDesktop = maxWidth > 900.dp
            val contentModifier = if (isDesktop) {
                Modifier.widthIn(max = 900.dp).align(Alignment.TopCenter).fillMaxHeight()
            } else {
                Modifier.fillMaxSize()
            }
            when {
                state.isLoading -> Box(contentModifier, contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.error != null -> Box(contentModifier, contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${state.error}")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.dispatch(SnippetDetailIntent.Load) }) { Text("Retry") }
                    }
                }
                state.isEditing -> EditSnippetForm(
                    state = state,
                    onIntent = viewModel::dispatch,
                    modifier = contentModifier
                )
                state.snippet != null -> SnippetDetailContent(
                    state = state,
                    onIntent = viewModel::dispatch,
                    modifier = contentModifier
                )
            }
        }
    }
}

@Composable
private fun SnippetDetailContent(
    state: SnippetDetailState,
    onIntent: (SnippetDetailIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val snippet = state.snippet ?: return
    val formattedCode = remember(snippet.code) { SasFormatter.formatSasCode(snippet.code) }
    val formattedSavedRCode = remember(snippet.rCode) {
        snippet.rCode?.let { RFormatter.formatRCode(it) }
    }
    var copied by remember { mutableStateOf(false) }
    var rCopied by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AssistChip(onClick = {}, label = { Text(snippet.type.name) })
        Spacer(Modifier.height(8.dp))
        Text(text = snippet.description, style = MaterialTheme.typography.bodyLarge)
        if (snippet.tags.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                snippet.tags.forEach { tag -> SuggestionChip(onClick = {}, label = { Text(tag) }) }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SAS Code", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onIntent(SnippetDetailIntent.ConvertToR) }) {
                    Text("Convert to R")
                }
                Button(onClick = {
                    clipboardManager.setText(AnnotatedString(snippet.code))
                    copied = true
                }) {
                    Text(if (copied) "Copied!" else "Copy")
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium) {
            Text(
                text = formattedCode,
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(16.dp),
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodySmall,
                softWrap = false
            )
        }

        if (snippet.rCode != null && formattedSavedRCode != null) {
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("R Code", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = {
                    clipboardManager.setText(AnnotatedString(snippet.rCode!!))
                    rCopied = true
                }) {
                    Text(if (rCopied) "Copied!" else "Copy")
                }
            }
            Spacer(Modifier.height(8.dp))
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium) {
                Text(
                    text = formattedSavedRCode,
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(16.dp),
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall,
                    softWrap = false
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSnippetForm(
    state: SnippetDetailState,
    onIntent: (SnippetDetailIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = state.editTitle,
            onValueChange = { onIntent(SnippetDetailIntent.EditTitleChanged(it)) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = state.editType.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                shape = MaterialTheme.shapes.medium,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                SnippetType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            onIntent(SnippetDetailIntent.EditTypeChanged(type))
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = state.editDescription,
            onValueChange = { onIntent(SnippetDetailIntent.EditDescriptionChanged(it)) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2, maxLines = 4,
            shape = MaterialTheme.shapes.medium
        )

        OutlinedTextField(
            value = state.editCode,
            onValueChange = { onIntent(SnippetDetailIntent.EditCodeChanged(it)) },
            label = { Text("Code") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5, maxLines = 15,
            shape = MaterialTheme.shapes.medium,
            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
        )

        OutlinedTextField(
            value = state.editTagsInput,
            onValueChange = { onIntent(SnippetDetailIntent.EditTagsChanged(it)) },
            label = { Text("Tags (comma separated)") },
            placeholder = { Text("sql, join, basic") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        state.editError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}