package com.example.document_reader_rag.ui.ask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.document_reader_rag.data.local.InMemoryVectorStore
import com.example.document_reader_rag.domain.usecase.CreateAugmentationUseCase
import com.example.document_reader_rag.ui.common.readableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AskViewModel @Inject constructor(
    private val vectorStore: InMemoryVectorStore,
    private val createAugmentationUseCase: CreateAugmentationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AskUiState())
    val uiState: StateFlow<AskUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        // The corpus is embedded on the Home screen; mirror its size so this
        // screen can tell the user whether there is anything to search.
        viewModelScope.launch {
            vectorStore.chunks.collect { chunks ->
                _uiState.update { it.copy(corpusSize = chunks.size) }
            }
        }
    }

    fun onQuestionChange(question: String) {
        _uiState.update { it.copy(question = question) }
    }

    fun onSubmit() {
        val question = _uiState.value.question.trim()
        val storedEmbeddings = vectorStore.chunks.value
        if (question.isBlank() || storedEmbeddings.isEmpty()) return

        // Drop an in-flight search so a slow earlier answer can't overwrite a newer one.
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(search = SearchState.Searching) }
            try {
                val results = createAugmentationUseCase(
                    question = question,
                    storedEmbedding = storedEmbeddings
                )
                _uiState.update { it.copy(search = SearchState.Results(results)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(search = SearchState.Error(e.readableMessage())) }
            }
        }
    }
}
