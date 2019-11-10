package com.github.kokorin.graphdrawing.draw

interface DrawingApi {
    val drawingAreaWidth: Int

    val drawingAreaHeight: Int

    fun drawCircle(circle: Circle)

    fun drawLine(start: Point, end: Point)

    fun show()
}
