package com.example.document_reader_rag.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GenerateContentRequest(
    val model: String,
    @SerializedName("system_instruction")
    val systemInstruction: String,
    val input: String
)
