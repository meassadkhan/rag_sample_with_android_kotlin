package com.example.document_reader_rag.data.remote.api

import com.example.document_reader_rag.data.remote.dto.GenerateContentRequest
import com.example.document_reader_rag.data.remote.dto.GenerateContentResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GeminiGenerationApi {
    @POST("v1beta/interactions")
    suspend fun generateContent(
        @Header("x-goog-api-key") api: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}