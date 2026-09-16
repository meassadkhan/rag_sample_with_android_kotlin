package com.example.document_reader_rag.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.document_reader_rag.data.local.InMemoryVectorStore
import com.example.document_reader_rag.domain.model.DocumentChunk
import com.example.document_reader_rag.domain.usecase.CreateEmbeddingsUseCase
import com.example.document_reader_rag.ui.common.readableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val createEmbeddingsUseCase: CreateEmbeddingsUseCase,
    private val vectorStore: InMemoryVectorStore,
    private val knowledgeBase: List<DocumentChunk>
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        embedKnowledgeBase()
    }

    fun retry() = embedKnowledgeBase()

    private fun embedKnowledgeBase() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading(completed = 0, total = knowledgeBase.size)
            try {
                // Publish once, so the Ask screen can search without re-embedding.
                val embeddedChunks = createEmbeddingsUseCase.invoke { completedCount ->
                    _uiState.value =
                        HomeUiState.Loading(completed = completedCount, total = knowledgeBase.size)
                }
                vectorStore.save(embeddedChunks)
                _uiState.value = HomeUiState.Ready(embeddedChunks.toList())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.readableMessage())
            }
        }
    }
}
