package com.github.kokorin.graphdrawing.draw

import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.stage.Stage

class JavaFXDrawingApi(
    private val stage: Stage,
    override val drawingAreaWidth: Int,
    override val drawingAreaHeight: Int
) : DrawingApi {
    private val canvas = Canvas(drawingAreaWidth.toDouble(), drawingAreaHeight.toDouble())
    private val gc = canvas.graphicsContext2D!!

    override fun show() {
        val root = Group()
        root.children.add(canvas)
        stage.scene = Scene(root, Color.WHITE)
        stage.isResizable = false
        stage.show()
    }

    override fun drawCircle(circle: Circle) {
        gc.fillOval(
            (circle.center.x - circle.radius).toDouble(), (circle.center.y - circle.radius).toDouble(),
            (circle.radius * 2).toDouble(), (circle.radius * 2).toDouble()
        )
    }

    override fun drawLine(start: Point, end: Point) {
        gc.strokeLine(start.x.toDouble(), start.y.toDouble(), end.x.toDouble(), end.y.toDouble())
    }
}
