package com.github.kokorin.graphdrawing.graph

import com.github.kokorin.graphdrawing.draw.Circle
import com.github.kokorin.graphdrawing.draw.DrawingApi
import com.github.kokorin.graphdrawing.draw.Point
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

data class Vertex(val number: Int)
data class Edge(val from: Vertex, val to: Vertex)

class ListGraph(
    private val vertexCount: Int,
    private val edges: List<Edge>,
    private val drawingApi: DrawingApi
) : Graph(drawingApi) {
    override fun drawGraph() {
        val center = Point(drawingApi.drawingAreaWidth / 2, drawingApi.drawingAreaHeight / 2)
        val bigR = min(drawingApi.drawingAreaHeight, drawingApi.drawingAreaWidth) * 0.3
        val vertexR = sqrt(min(drawingApi.drawingAreaHeight, drawingApi.drawingAreaHeight).toDouble()).toInt() / 2

        fun getVertexCenter(i: Int): Point {
            return Point(
                (center.x + bigR * cos(Math.PI * 2 * i / vertexCount)).toInt(),
                (center.y + bigR * sin(Math.PI * 2 * i / vertexCount)).toInt()
            )
        }

        for (i in (0 until vertexCount)) {
            drawingApi.drawCircle(Circle(getVertexCenter(i), vertexR))
        }

        for (edge in edges) {
            drawingApi.drawLine(getVertexCenter(edge.from.number), getVertexCenter(edge.to.number))
        }
    }

    companion object {
        fun readListGraph(drawingApi: DrawingApi): ListGraph {
            val n = readLine()?.toInt() ?: throw IllegalArgumentException("Specify number of vertex")
            val m = readLine()?.toInt() ?: throw IllegalArgumentException("Specify number of edges")
            val edges = List(m) {
                val line = readLine()
                    ?.split(" ")
                    ?.map { it.toInt() }
                    ?: throw IllegalArgumentException("Specify from and to vertex")
                require(line.size == 2) { "Specify two edges" }
                val from = line[0]
                val to = line[1]
                Edge(Vertex(from), Vertex(to))
            }

            return ListGraph(n, edges, drawingApi)
        }
    }
}
