package com.example.document_reader_rag.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class RetryOn429Interceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)

        if (response.code == 429) {

            val retryAfter = response.header("Retry-After")?.toLongOrNull() ?: 1L

            Thread.sleep(retryAfter * 1000)

            response.close()
            response = chain.proceed(request)

        }
        return response

    }
}
