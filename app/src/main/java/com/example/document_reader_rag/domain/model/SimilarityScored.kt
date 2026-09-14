package com.example.document_reader_rag.domain.model

data class SimilarityScored(val chunk: EmbeddedChunk, val score: Float)
