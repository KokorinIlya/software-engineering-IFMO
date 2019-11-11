package com.github.kokorin.graphdrawing.graph

import com.github.kokorin.graphdrawing.draw.Circle
import com.github.kokorin.graphdrawing.draw.DrawingApi
import com.github.kokorin.graphdrawing.draw.Point
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.properties.Delegates

abstract class Graph(private val drawingApi: DrawingApi) {
    private lateinit var centralPoint: Point
    private var radius by Delegates.notNull<Double>()

    protected fun getVertexCenter(i: Int): Point {
        return Point(
            (centralPoint.x + radius * cos(2 * Math.PI * i / vertexCount)).toInt(),
            (centralPoint.y + radius * sin(2 * Math.PI * i / vertexCount)).toInt()
        )
    }

    fun drawGraph() {
        centralPoint = Point(drawingApi.drawingAreaWidth / 2, drawingApi.drawingAreaHeight / 2)
        radius = min(drawingApi.drawingAreaHeight, drawingApi.drawingAreaWidth) * 0.3
        val vertexRadius = sqrt(
            min(
                drawingApi.drawingAreaHeight,
                drawingApi.drawingAreaHeight
            ).toDouble()
        ).toInt() / 2

        for (i in (0 until vertexCount)) {
            drawingApi.drawCircle(Circle(getVertexCenter(i), vertexRadius))
        }

        drawEdges()
    }

    protected abstract fun drawEdges()

    abstract val vertexCount: Int
}

