package com.vadimdorofeev.orthodoxlentendays

import android.graphics.Color
import android.graphics.Path
import android.graphics.PointF

class Segment {
    var color = Color.WHITE
    val path = Path()
    val selection = Path()

    private var isPathEmpty = true
    private var isSelectionEmpty = true

    fun addPoint(x: Float, y: Float, isFinal: Boolean = false) {
        if (isPathEmpty) {
            path.moveTo(x, y)
            isPathEmpty = false
        }
        else
            path.lineTo(x, y)
        if (isFinal)
            path.close()
    }

    fun addPoint(point: PointF, isFinal: Boolean = false) {
        addPoint(point.x, point.y, isFinal)
    }

    fun addSelectionPoint(x: Float, y: Float, isFinal: Boolean = false) {
        if (isSelectionEmpty) {
            selection.moveTo(x, y)
            isSelectionEmpty = false
        }
        else
            selection.lineTo(x, y)
        if (isFinal)
            selection.close()
    }
    fun addSelectionPoint(point: PointF, isFinal: Boolean = false) {
        addSelectionPoint(point.x, point.y, isFinal)
    }
}