package com.example.document_reader_rag.data.remote.api

import com.example.document_reader_rag.data.remote.dto.EmbeddingRequest
import com.example.document_reader_rag.data.remote.dto.EmbeddingResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/** Used for both the URL path and the request body, so the two can never disagree. */
const val EMBEDDING_MODEL = "models/gemini-embedding-001"

interface GeminiEmbeddingApi {
    @POST("v1beta/{model}:embedContent")
    suspend fun embedContent(
        // encoded = true keeps the "/" in "models/..." from being escaped to %2F
        @Path("model", encoded = true) model: String,
        @Header("x-goog-api-key") apiKey: String,
        @Body request: EmbeddingRequest
    ): EmbeddingResponse
}
