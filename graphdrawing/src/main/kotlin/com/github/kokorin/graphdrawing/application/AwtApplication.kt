package com.github.kokorin.graphdrawing.application

import com.github.kokorin.graphdrawing.draw.AwtDrawingApi
import java.awt.Frame
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import kotlin.system.exitProcess

class AwtApplication : Frame(), DrawingApplication {
    override fun startApplication() {
        addWindowListener(
            object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
                    exitProcess(0)
                }
            }
        )
        setSize(ApplicationParams.drawingAreaParams.width, ApplicationParams.drawingAreaParams.height)
        isVisible = true
    }

    override fun paint(graphics: Graphics) {
        super.paint(graphics)
        val graphics2D = graphics as Graphics2D
        graphics2D.clearRect(0, 0, width, height)
        val api = AwtDrawingApi(
            graphics2D,
            ApplicationParams.drawingAreaParams.width,
            ApplicationParams.drawingAreaParams.height
        )
        val graphDrawer = GraphDrawer(api)
        graphDrawer.readAndDrawGraph(ApplicationParams.graphType)
        isResizable = false
    }
}
