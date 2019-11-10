package com.github.kokorin.graphdrawing.draw

import java.awt.Graphics2D
import java.awt.geom.Ellipse2D

class AwtDrawingApi(
    private val graphics2D: Graphics2D,
    override val drawingAreaWidth: Int,
    override val drawingAreaHeight: Int
) : DrawingApi {

    override fun drawCircle(circle: Circle) {
        graphics2D.fill(
            Ellipse2D.Float(
                (circle.center.x - circle.radius).toFloat(), (circle.center.y - circle.radius).toFloat(),
                (circle.radius * 2).toFloat(), (circle.radius * 2).toFloat()
            )
        )
    }

    override fun drawLine(start: Point, end: Point) {
        graphics2D.drawLine(start.x, start.y, end.x, end.y)
    }

    override fun show() {
    }
}
