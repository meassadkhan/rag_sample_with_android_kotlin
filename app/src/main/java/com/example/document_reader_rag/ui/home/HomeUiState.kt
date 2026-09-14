package com.example.document_reader_rag.ui.home

import com.example.document_reader_rag.domain.model.EmbeddedChunk

sealed interface HomeUiState {

    /** Embedding in progress; [completed] of [total] chunks done. */
    data class Loading(
        val completed: Int = 0,
        val total: Int = 0
    ) : HomeUiState

    data class Ready(val chunks: List<EmbeddedChunk>) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
