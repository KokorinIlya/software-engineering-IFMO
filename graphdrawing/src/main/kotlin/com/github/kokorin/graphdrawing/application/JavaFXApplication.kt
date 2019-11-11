package com.github.kokorin.graphdrawing.application

import com.github.kokorin.graphdrawing.draw.JavaFXDrawingApi
import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.stage.Stage

class JavaFXApplication : Application(), DrawingApplication {
    override fun startApplication() {
        launch(this::class.java)
    }

    override fun start(primaryStage: Stage) {
        val canvas = Canvas(
            ApplicationParams.drawingAreaParams.width.toDouble(),
            ApplicationParams.drawingAreaParams.height.toDouble()
        )
        val gc = canvas.graphicsContext2D
        val api = JavaFXDrawingApi(
            gc,
            ApplicationParams.drawingAreaParams.width,
            ApplicationParams.drawingAreaParams.height
        )
        val graphDrawer = GraphDrawer(api)
        graphDrawer.readAndDrawGraph(ApplicationParams.graphType)
        val root = Group()
        root.children.add(canvas)
        primaryStage.scene = Scene(root, Color.WHITE)
        primaryStage.isResizable = false
        primaryStage.show()
    }
}
