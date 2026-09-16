package com.example.document_reader_rag.ui.common

import android.util.Log
import retrofit2.HttpException

/**
 * Turns a thrown error into something worth showing.
 *
 * For HTTP failures the useful part is Gemini's own JSON body — it names the
 * exact field or model that was rejected, which the status code alone does not.
 */
fun Throwable.readableMessage(): String = when (this) {
    is HttpException -> {
        Log.i("DebugTAG", "readableMessage:HttpException ")
        val body = runCatching { response()?.errorBody()?.string() }.getOrNull()
        "HTTP ${code()} ${message()}".trim() + if (!body.isNullOrBlank()) "\n\n$body" else ""
    }

    else ->{
        Log.i("DebugTAG", "readableMessage:else part ")
        message ?: this::class.simpleName ?: "Unknown error"
    }
}
