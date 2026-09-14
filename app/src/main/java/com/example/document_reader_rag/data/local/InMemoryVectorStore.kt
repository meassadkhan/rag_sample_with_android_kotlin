package com.example.document_reader_rag.data.local

import com.example.document_reader_rag.domain.model.EmbeddedChunk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Holds the embedded corpus in memory so every screen sees the same vectors.
 *
 * Singleton-scoped: Home embeds and saves, Ask reads. Swap this for a
 * Room-backed store later and neither screen has to change.
 */
@Singleton
class InMemoryVectorStore @Inject constructor() {

    private val _chunks = MutableStateFlow<List<EmbeddedChunk>>(emptyList())
    val chunks: StateFlow<List<EmbeddedChunk>> = _chunks.asStateFlow()

    fun save(chunks: List<EmbeddedChunk>) {
        _chunks.value = chunks
    }
}
