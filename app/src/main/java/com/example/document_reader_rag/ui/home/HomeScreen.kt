package com.example.document_reader_rag.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.document_reader_rag.domain.model.EmbeddedChunk
import kotlin.math.sqrt

@Composable
fun HomeScreen(
    onAskClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val state = uiState

    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            when (state) {
                is HomeUiState.Loading -> LoadingState(state)
                is HomeUiState.Error -> ErrorState(state, onRetry = viewModel::retry)
                is HomeUiState.Ready -> EmbeddingList(state.chunks)
            }
        }

        Button(
            onClick = onAskClick,
            // Nothing to search until the corpus has been embedded.
            enabled = state is HomeUiState.Ready,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Ask a question")
        }
    }
}

@Composable
private fun LoadingState(state: HomeUiState.Loading, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text(
            text = if (state.total > 0) {
                "Embedding ${state.completed} / ${state.total} chunks"
            } else {
                "Starting..."
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun ErrorState(
    state: HomeUiState.Error,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Embedding failed",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = state.message,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun EmbeddingList(chunks: List<EmbeddedChunk>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "${chunks.size} chunks embedded - " +
                    "${chunks.firstOrNull()?.embedding?.size ?: 0} dimensions each",
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(chunks) { embedded -> EmbeddedChunkCard(embedded) }
    }
}

@Composable
private fun EmbeddedChunkCard(embedded: EmbeddedChunk, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "#${embedded.chunk.id}  ${embedded.chunk.text}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "dims ${embedded.embedding.size} - norm %.4f".format(embedded.embedding.norm()),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = embedded.embedding.take(6).joinToString(
                    separator = ", ",
                    prefix = "[",
                    postfix = ", ...]"
                ) { "%.4f".format(it) },
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Vector length. ~1.0 means the API already returns unit vectors. */
private fun List<Float>.norm(): Float =
    sqrt(sumOf { (it * it).toDouble() }).toFloat()
