package com.github.kokorin.graphdrawing.graph

import com.github.kokorin.graphdrawing.draw.DrawingApi

class MatrixGraph(private val matrix: Array<BooleanArray>, private val drawingApi: DrawingApi) : Graph(drawingApi) {
    override fun drawEdges() {
        for (i in 0 until vertexCount) {
            for (j in 0 until vertexCount) {
                if (matrix[i][j]) {
                    drawingApi.drawLine(getVertexCenter(i), getVertexCenter(j))
                }
            }
        }
    }

    override val vertexCount: Int
        get() = matrix.size

    companion object {
        fun readMatrixGraph(drawingApi: DrawingApi): MatrixGraph {
            val n = readLine()?.toInt() ?: throw IllegalArgumentException("Number of vertices should be specified")
            val result = Array(n) {
                BooleanArray(0) {
                    false
                }
            }
            for (i in 0 until n) {
                val edges = readLine()?.split(" ")?.map {
                    it == "1"
                }?.toBooleanArray() ?: throw IllegalArgumentException("${i + 1} line of matrix should be specified")
                result[i] = edges
            }
            return MatrixGraph(result, drawingApi)
        }
    }
}
