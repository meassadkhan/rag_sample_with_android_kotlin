package com.example.document_reader_rag.data.similarity

import kotlin.math.sqrt

object VectorMath {
    fun cosineSimilarity(
        a: List<Float>,
        b: List<Float>
    ): Float {

        require(a.size == b.size) {
            "Vectors must have the same size"
        }

        var dotProduct = 0.0
        var magnitudeA = 0.0
        var magnitudeB = 0.0

        for (i in a.indices) {
            dotProduct += a[i] * b[i]
            magnitudeA += a[i] * a[i]
            magnitudeB += b[i] * b[i]
        }

        return (
                dotProduct /
                        (sqrt(magnitudeA) * sqrt(magnitudeB))
                ).toFloat()
    }
}