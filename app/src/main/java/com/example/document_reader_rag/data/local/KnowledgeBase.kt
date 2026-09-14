package com.example.document_reader_rag.data.local

import com.example.document_reader_rag.domain.model.DocumentChunk

val knowledgeBase = listOf(
    DocumentChunk(
        id = 1,
        text = "Kotlin coroutines allow you to write asynchronous code in a sequential style."
    ),
    DocumentChunk(
        id = 2,
        text = "A CoroutineScope defines the lifecycle of coroutines and helps manage their cancellation."
    ),
    DocumentChunk(
        id = 3,
        text = "Structured concurrency ensures that child coroutines are tied to the lifecycle of their parent coroutine."
    ),
    DocumentChunk(
        id = 4,
        text = "Kotlin Flow is an asynchronous stream that emits multiple values over time."
    ),
    DocumentChunk(
        id = 5,
        text = "StateFlow is a state-holder observable flow that emits the current and updated state to collectors."
    )
)
