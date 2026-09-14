package com.example.document_reader_rag.domain.model

data class EmbeddedChunk(
    val chunk: DocumentChunk,
    val embedding: List<Float>
)
