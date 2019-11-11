package com.github.kokorin.graphdrawing.application

import com.github.kokorin.graphdrawing.draw.DrawingApi
import com.github.kokorin.graphdrawing.graph.ListGraph
import com.github.kokorin.graphdrawing.graph.MatrixGraph

class GraphDrawer(private val drawingApi: DrawingApi) {
    fun readAndDrawGraph(graphType: GraphType) {
        val graph = if (graphType == GraphType.LIST) {
            ListGraph.readListGraph(drawingApi)
        } else {
            MatrixGraph.readMatrixGraph(drawingApi)
        }
        graph.drawGraph()
    }
}
