package com.example.document_reader_rag.domain.usecase

import com.example.document_reader_rag.data.remote.dto.GenerateContentRequest
import com.example.document_reader_rag.data.repository.GenerationContentRepository
import com.example.document_reader_rag.utils.RagException
import java.net.SocketTimeoutException
import javax.inject.Inject


private const val SYSTEM_INSTRUCTION = """
    You are a helpful AI assistant.

    Answer the user question using the following context.

    Rules:
    - Use the provided context as the primary source of information.
    - Do not invent facts that are not supported by the context.
    - If the context does not contain enough information to answer the question,
      clearly say that the provided context does not contain enough information.
    - Give a clear and concise answer.
"""

class GenerateContentUseCase @Inject constructor(
    private val generationContentRepository: GenerationContentRepository
) {
    suspend operator fun invoke(
        context: String,
        question: String
    ): String {
        try {
            val request = GenerateContentRequest(
                model = "gemini-3.8-flash",
                systemInstruction = SYSTEM_INSTRUCTION,
                input = """
            CONTEXT:
            $context

            USER QUESTION:
            $question

            ANSWER:
        """.trimIndent()
            )

            val response = generationContentRepository.generateContent(request)

            return response.steps
                .firstOrNull { it.type == "model_output" }
                ?.content
                ?.firstOrNull { it.type == "text" }
                ?.text
                .orEmpty()

        } catch (e: SocketTimeoutException) {
            throw RagException.GenerationTimeout(e)
        }


    }

}