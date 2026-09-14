package com.example.document_reader_rag.data.repository

import com.example.document_reader_rag.data.remote.api.EMBEDDING_MODEL
import com.example.document_reader_rag.data.remote.api.GeminiEmbeddingApi
import com.example.document_reader_rag.data.remote.dto.Content
import com.example.document_reader_rag.data.remote.dto.EmbeddingRequest
import com.example.document_reader_rag.data.remote.dto.Part
import com.example.document_reader_rag.di.GeminiApiKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmbeddingRepository @Inject constructor(
    private val api: GeminiEmbeddingApi,
    @GeminiApiKey private val apiKey: String
) {
    /** Turns one piece of text into its embedding vector. */
    suspend fun embed(text: String): List<Float> {
        val request = EmbeddingRequest(
            model = EMBEDDING_MODEL,
            content = Content(parts = listOf(Part(text = text)))
        )
        return api.embedContent(
            model = EMBEDDING_MODEL,
            apiKey = apiKey,
            request = request
        ).embedding.values
    }
}
