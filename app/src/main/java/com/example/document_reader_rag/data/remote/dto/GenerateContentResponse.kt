package com.example.document_reader_rag.data.remote.dto

data class GenerateContentResponse(
    val id: String,
    val status: String,
    val steps: List<Step>,
    val model: String
)

data class Step(
    val type: String,
    val content: List<GenerationContent>?
)

data class GenerationContent(
    val type: String,
    val text: String?
)
