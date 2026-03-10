package com.sassnippet.manager.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sassnippet.manager.model.Snippet
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnippetListScreen(
    onSnippetClick: (Int) -> Unit,
    onCreateClick: () -> Unit,
    viewModel: SnippetListViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("SAS Snippets") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding)) {
            val isDesktop = maxWidth > 900.dp
            val contentModifier = if (isDesktop) {
                Modifier.widthIn(max = 900.dp).align(Alignment.TopCenter).fillMaxHeight()
            } else {
                Modifier.fillMaxSize()
            }
            Column(modifier = contentModifier) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.search(it) },
                    placeholder = { Text("Search by keyword or tag...") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
                when {
                    state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }

                    state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${state.error}")
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadSnippets() }) { Text("Retry") }
                        }
                    }

                    state.snippets.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No snippets found")
                    }

                    else -> LazyColumn(Modifier.fillMaxSize()) {
                        items(state.snippets) { snippet ->
                            SnippetListItem(snippet = snippet, onClick = { onSnippetClick(snippet.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SnippetListItem(snippet: Snippet, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = snippet.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                AssistChip(onClick = {}, label = { Text(snippet.type.name) })
            }
            Spacer(Modifier.height(4.dp))
            Text(text = snippet.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            if (snippet.tags.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    snippet.tags.forEach { tag -> SuggestionChip(onClick = {}, label = { Text(tag) }) }
                }
            }
        }
    }
}
