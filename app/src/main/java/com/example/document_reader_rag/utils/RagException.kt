package com.example.document_reader_rag.utils

sealed class RagException(message: String, cause: Throwable) : Exception(message, cause) {

    class EmbeddingTimeout(
        cause: Throwable
    ) : RagException(
        message = "Embedding request timeout",
        cause = cause
        )

    class GenerationTimeout(
        cause: Throwable
    ) : RagException(
        message = "Generation request timeout",
        cause = cause
    )

    class NetworkError(
        cause: Throwable
    ) : RagException(
        message = "Network Error ",
        cause = cause
    )
}