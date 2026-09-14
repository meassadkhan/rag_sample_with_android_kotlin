package com.example.document_reader_rag.di

import com.example.document_reader_rag.data.remote.api.GeminiEmbeddingApi
import com.example.document_reader_rag.data.remote.api.GeminiGenerationApi
import com.example.document_reader_rag.data.remote.interceptor.RetryOn429Interceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://generativelanguage.googleapis.com/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(RetryOn429Interceptor())
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideEmbeddingApi(retrofit: Retrofit): GeminiEmbeddingApi =
        retrofit.create(GeminiEmbeddingApi::class.java)

    @Provides
    @Singleton
    fun provideGenerationApi(retrofit: Retrofit): GeminiGenerationApi =
        retrofit.create(GeminiGenerationApi::class.java)

}
