package com.example.document_reader_rag.ui.ask

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.document_reader_rag.domain.model.DocumentChunk
import com.example.document_reader_rag.domain.model.EmbeddedChunk
import com.example.document_reader_rag.domain.model.SimilarityScored
import com.example.document_reader_rag.ui.theme.Document_reader_ragTheme

@Composable
fun AskScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AskContent(
        state = uiState,
        onQuestionChange = viewModel::onQuestionChange,
        onSubmit = viewModel::onSubmit,
        onBack = onBack,
        modifier = modifier
    )
}

/**
 * Stateless, so it renders in @Preview with no Hilt graph, Activity or network.
 */
@Composable
fun AskContent(
    state: AskUiState,
    onQuestionChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val canSubmit = state.question.isNotBlank() &&
        state.corpusSize > 0 &&
        state.search !is SearchState.Searching

    val submit = {
        focusManager.clearFocus()
        onSubmit()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
            Text("Back")
        }

        OutlinedTextField(
            value = state.question,
            onValueChange = onQuestionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Ask a question") },
            placeholder = { Text("What is structured concurrency?") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { if (canSubmit) submit() })
        )

        Button(
            onClick = submit,
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        Text(
            text = if (state.corpusSize == 0) {
                "No embedded chunks yet. Embed the knowledge base on Home first."
            } else {
                "Searching across ${state.corpusSize} embedded chunks."
            },
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        when (val search = state.search) {
            SearchState.Idle -> Text(
                text = "Ask something to see which chunks are closest in meaning.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            SearchState.Searching -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                Text(
                    text = "Embedding question and ranking chunks",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            is SearchState.Error -> Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "Search failed",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = search.message,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
            }

            is SearchState.Results -> LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
               item {
                   ResultCard(rank = 1, context = state.search.context)
               }
            }


        }
    }
}

@Composable
private fun ResultCard(rank: Int, context: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          /*  Text(
                text = "#$rank  score %.4f".format(scored.score),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )*/
            Text(
                text = context,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ------------------------------------------------------------------ previews

private fun sampleScored(text: String, score: Float) = SimilarityScored(
    chunk = EmbeddedChunk(
        chunk = DocumentChunk(id = 1, text = text),
        embedding = List(8) { 0.1f }
    ),
    score = score
)

@Preview(name = "Idle", showBackground = true)
@Composable
private fun AskIdlePreview() {
    Document_reader_ragTheme {
        AskContent(
            state = AskUiState(corpusSize = 5),
            onQuestionChange = {}, onSubmit = {}, onBack = {}
        )
    }
}

@Preview(name = "Empty corpus", showBackground = true)
@Composable
private fun AskEmptyCorpusPreview() {
    Document_reader_ragTheme {
        AskContent(
            state = AskUiState(question = "what is a flow?", corpusSize = 0),
            onQuestionChange = {}, onSubmit = {}, onBack = {}
        )
    }
}

@Preview(name = "Searching", showBackground = true)
@Composable
private fun AskSearchingPreview() {
    Document_reader_ragTheme {
        AskContent(
            state = AskUiState(
                question = "what is structured concurrency?",
                corpusSize = 5,
                search = SearchState.Searching
            ),
            onQuestionChange = {}, onSubmit = {}, onBack = {}
        )
    }
}

@Preview(name = "Results", showBackground = true)
@Composable
private fun AskResultsPreview() {
    Document_reader_ragTheme {
        AskContent(
            state = AskUiState(
                question = "what is structured concurrency?",
                corpusSize = 5,
                search = SearchState.Results(
                    "Context for LLM will be show here"
                )
            ),
            onQuestionChange = {}, onSubmit = {}, onBack = {}
        )
    }
}

@Preview(name = "Error", showBackground = true)
@Composable
private fun AskErrorPreview() {
    Document_reader_ragTheme {
        AskContent(
            state = AskUiState(
                question = "what is a flow?",
                corpusSize = 5,
                search = SearchState.Error("HTTP 429 Too Many Requests")
            ),
            onQuestionChange = {}, onSubmit = {}, onBack = {}
        )
    }
}
