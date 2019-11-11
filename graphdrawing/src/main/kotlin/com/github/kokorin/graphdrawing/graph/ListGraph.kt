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
    override val vertexCount: Int,
    private val edges: List<Edge>,
    private val drawingApi: DrawingApi
) : Graph(drawingApi) {
    override fun drawEdges() {
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
                val from = line[0] - 1
                val to = line[1] - 1
                Edge(Vertex(from), Vertex(to))
            }

            return ListGraph(n, edges, drawingApi)
        }
    }
}
