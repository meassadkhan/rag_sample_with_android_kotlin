package com.example.document_reader_rag.data.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val TAG = "GeminiHttp"
private const val MAX_REQUEST_CHARS = 2_000
private const val MAX_ERROR_BYTES = 8_192L

class GeminiCallLoggingInterceptor(
    private val enabled: Boolean
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!enabled) return chain.proceed(request)

        Log.d(TAG, "${request.method} ${request.url}")

        request.bodyAsText()?.let {
            Log.d(TAG, "request $it")
        }

        val startNs = System.nanoTime()

        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            Log.e(TAG, "FAILED ${request.url}", e)
            throw e
        }

        val tookMS = System.nanoTime() - startNs / 1000000

        if (response.isSuccessful) {
            val size = response.body?.contentLength() ?: -1L
            Log.d(TAG, "${response.code} in ${tookMS} ms (body $size bytes) ")
        } else {
            val body = response.peekBody(MAX_ERROR_BYTES).string()
            Log.e(TAG, "${response.code} ${response.message} in ${tookMS}ms\n $body")
        }
        return response
    }

    private fun Request.bodyAsText(
    ): String {
        return runCatching {
            okio.Buffer().also { body?.writeTo(it) }.readUtf8().take(MAX_REQUEST_CHARS)
        }.getOrElse {
            "<unreachable: ${it.message}>"
        }
    }
}

