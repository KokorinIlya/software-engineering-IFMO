package com.github.kokorin.graphdrawing.draw

import javafx.scene.canvas.GraphicsContext

class JavaFXDrawingApi(
    private val gc: GraphicsContext,
    override val drawingAreaWidth: Int,
    override val drawingAreaHeight: Int
) : DrawingApi {

    override fun drawCircle(circle: Circle) {
        gc.fillOval(
            (circle.center.x - circle.radius).toDouble(),
            (circle.center.y - circle.radius).toDouble(),
            (circle.radius * 2).toDouble(),
            (circle.radius * 2).toDouble()
        )
    }

    override fun drawLine(start: Point, end: Point) {
        gc.strokeLine(
            start.x.toDouble(),
            start.y.toDouble(),
            end.x.toDouble(),
            end.y.toDouble()
        )
    }
}
