package com.example.document_reader_rag.ui.ask

import com.example.document_reader_rag.domain.model.SimilarityScored

data class AskUiState(
    val question: String = "",
    val corpusSize: Int = 0,
    val search: SearchState = SearchState.Idle
)

sealed interface SearchState {

    data object Idle : SearchState

    data object Searching : SearchState

    data class Results(val context: String) : SearchState

    data class Error(val message: String) : SearchState
}
