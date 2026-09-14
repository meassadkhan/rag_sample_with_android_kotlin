package com.example.document_reader_rag.data.remote.dto

data class EmbeddingRequest(
    val model: String,
    val content: Content
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)
