package com.example.document_reader_rag.di

import com.example.document_reader_rag.BuildConfig
import com.example.document_reader_rag.data.local.knowledgeBase
import com.example.document_reader_rag.domain.model.DocumentChunk
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @GeminiApiKey
    fun provideGeminiApiKey(): String = BuildConfig.GEMINI_API_KEY

    @Provides
    @Singleton
    fun provideKnowledgeBase(): List<DocumentChunk> = knowledgeBase

}
