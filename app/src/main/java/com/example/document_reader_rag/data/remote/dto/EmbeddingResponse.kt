package com.example.document_reader_rag.data.remote.dto

data class EmbeddingResponse(
    val embedding: Embedding
)

data class Embedding(
    val values: List<Float>
)
