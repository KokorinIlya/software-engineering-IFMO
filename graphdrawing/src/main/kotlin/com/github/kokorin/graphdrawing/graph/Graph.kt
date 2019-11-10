package com.github.kokorin.graphdrawing.graph

import com.github.kokorin.graphdrawing.draw.DrawingApi

abstract class Graph(drawingApi: DrawingApi) {
    abstract fun drawGraph()
}

