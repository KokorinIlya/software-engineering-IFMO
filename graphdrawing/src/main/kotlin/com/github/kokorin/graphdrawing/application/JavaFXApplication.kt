package com.github.kokorin.graphdrawing.application

import com.github.kokorin.graphdrawing.draw.JavaFXDrawingApi
import javafx.application.Application
import javafx.stage.Stage

class JavaFXApplication : Application(), DrawingApplication {
    override fun startApplication() {
        launch(this::class.java)
    }

    override fun start(primaryStage: Stage) {
        val api = JavaFXDrawingApi(
            primaryStage,
            ApplicationParams.drawingAreaParams.width,
            ApplicationParams.drawingAreaParams.height
        )
        val graphDrawer = GraphDrawer(ApplicationParams.graphType)
        graphDrawer.readAndDrawGraph(api)
    }
}
