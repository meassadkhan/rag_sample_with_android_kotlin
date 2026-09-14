package com.example.document_reader_rag.domain.usecase

import android.util.Log
import com.example.document_reader_rag.data.remote.dto.GenerateContentRequest
import com.example.document_reader_rag.domain.model.EmbeddedChunk
import javax.inject.Inject

class CreateAugmentationUseCase @Inject constructor(
    val retrieveRelevantChunksUseCase: RetrieveRelevantChunksUseCase,
    val generateContentUseCase: GenerateContentUseCase
) {
    suspend operator fun invoke(
        question: String,
        storedEmbedding: List<EmbeddedChunk>
    ): String {

        //Retreive Chunks
        val listOfRetrievedData = retrieveRelevantChunksUseCase(question, storedEmbedding, topK = 3)


        //Create context
        val context = listOfRetrievedData.joinToString(separator = "\n\n") { result ->
            """"
                [chunk] ${result.chunk.chunk.id}]
                 ${result.chunk.chunk.text}""".trimIndent()
        }
        Log.i("GenerateContentUseCasetag", "invoke: calling CreateAugmentationUseCase ")


        //Generate Content
       val generatedContent = generateContentUseCase(context, question)

       /* return GenerateContentRequest(
            model = "", systemInstruction = ""

        )*/


        return generatedContent

    }
}