package com.example.document_reader_rag.domain.usecase

import com.example.document_reader_rag.data.repository.EmbeddingRepository
import com.example.document_reader_rag.data.similarity.VectorMath
import com.example.document_reader_rag.domain.model.EmbeddedChunk
import com.example.document_reader_rag.domain.model.SimilarityScored
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RetrieveRelevantChunksUseCase @Inject constructor(
    val repository: EmbeddingRepository
) {
    suspend operator fun invoke(
        question: String,
        storedEmbedding: List<EmbeddedChunk>,
        topK: Int = 3
    ): List<SimilarityScored> {
        try {
            val queryEmbedding = repository.embed(question)
            return storedEmbedding.map { chunk ->
                SimilarityScored(
                    chunk,
                    VectorMath.cosineSimilarity(chunk.embedding, queryEmbedding)
                )
            }
                .sortedByDescending { it.score }
                .filter { (_, score) -> score >= 0.5f }
                .take(topK)
        } catch (e: Exception) {
            throw Exception("Retrieve Relevant Exception", e)
        }
    }
}