package com.github.kokorin.graphdrawing.application

fun main(args: Array<String>) {
    ApplicationParams.drawingAreaParams = DrawingAreaParams(600, 400)

    try {
        require(args.size == 2) { "Please, specify drawing API type and graph type" }

        ApplicationParams.graphType = when (args[1]) {
            "matrix" -> GraphType.MATRIX
            "list" -> GraphType.LIST
            else -> throw IllegalArgumentException("Graph type not specified, should be matrix or list")
        }

        val application = when (args[0]) {
            "javaFX" -> JavaFXApplication()
            "awt" -> AwtApplication()
            else -> throw IllegalArgumentException("Drawing API not specified, should be javaFX or awt")
        }
        application.startApplication()
    } catch (e: IllegalArgumentException) {
        println("Incorrect usage of program: ${e.message}")
    }
}

