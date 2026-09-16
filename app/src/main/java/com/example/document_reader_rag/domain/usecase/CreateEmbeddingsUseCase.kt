package com.example.document_reader_rag.domain.usecase

import com.example.document_reader_rag.data.repository.EmbeddingRepository
import com.example.document_reader_rag.domain.model.DocumentChunk
import com.example.document_reader_rag.domain.model.EmbeddedChunk
import com.example.document_reader_rag.utils.RagException
import java.net.SocketTimeoutException
import javax.inject.Inject

class CreateEmbeddingsUseCase @Inject constructor(
    val embeddingRepository: EmbeddingRepository,
    private val knowledgeBase: List<DocumentChunk>
) {
    suspend operator fun invoke(onCompleted: (count: Int) -> Unit): List<EmbeddedChunk> {
        try {
            val embeddingList: MutableList<EmbeddedChunk> = arrayListOf()
            knowledgeBase.onEachIndexed { index, chunk ->
                val embedding = embeddingRepository.embed(text = chunk.text)
                embeddingList.add(
                    EmbeddedChunk(
                        chunk = chunk,
                        embedding = embedding
                    )
                )
                onCompleted(index)
            }
            return embeddingList
        } catch (e: SocketTimeoutException) {
            throw RagException.EmbeddingTimeout(e)

        } catch (e: Exception) {
            throw Exception()
        }
    }
}