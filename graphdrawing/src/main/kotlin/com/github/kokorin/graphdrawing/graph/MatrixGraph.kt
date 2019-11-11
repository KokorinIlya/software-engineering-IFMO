package com.github.kokorin.graphdrawing.graph

import com.github.kokorin.graphdrawing.draw.Circle
import com.github.kokorin.graphdrawing.draw.DrawingApi
import com.github.kokorin.graphdrawing.draw.Point
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class MatrixGraph(private val matrix: Array<BooleanArray>, private val drawingApi: DrawingApi) : Graph(drawingApi) {
    override fun drawGraph() {
        val center = Point(drawingApi.drawingAreaWidth / 2, drawingApi.drawingAreaHeight / 2)
        val bigR = min(drawingApi.drawingAreaHeight, drawingApi.drawingAreaWidth) / 2 * 0.9
        val vertexR = sqrt(min(drawingApi.drawingAreaHeight, drawingApi.drawingAreaHeight).toDouble()).toInt()

        val n = matrix.size

        fun getVertexCenter(i: Int): Point {
            return Point(
                (center.x + bigR * cos(Math.PI * 2 * i / n)).toInt(),
                (center.y + bigR * sin(Math.PI * 2 * i / n)).toInt()
            )
        }

        for (i in (0 until n)) {
            drawingApi.drawCircle(Circle(getVertexCenter(i), vertexR))
        }

        for (i in 0 until n) {
            for (j in 0 until n) {
                if (matrix[i][j]) {
                    drawingApi.drawLine(getVertexCenter(i), getVertexCenter(j))
                }
            }
        }
    }

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
