package com.example.document_reader_rag.data.repository

import com.example.document_reader_rag.data.remote.api.GeminiGenerationApi
import com.example.document_reader_rag.data.remote.dto.GenerateContentRequest
import com.example.document_reader_rag.data.remote.dto.GenerateContentResponse
import com.example.document_reader_rag.di.GeminiApiKey
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GenerationContentRepository @Inject constructor(
    @GeminiApiKey private val apiKey: String,
    private val geminiGenerationApi: GeminiGenerationApi
) {
    suspend fun generateContent(
        generateContentRequest: GenerateContentRequest
    ): GenerateContentResponse {
        return geminiGenerationApi.generateContent(
            api = apiKey,
            request = generateContentRequest
        )
    }
}