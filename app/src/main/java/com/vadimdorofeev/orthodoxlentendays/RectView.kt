package com.vadimdorofeev.orthodoxlentendays

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet

class RectView @JvmOverloads constructor(context: Context,
                                         attrs: AttributeSet? = null,
                                         defStyleAttr: Int = 0,
                                         defStyleRes: Int = 0) :
                                         FigureView(context, attrs, defStyleAttr, defStyleRes) {

    private var outerRect = RectF()

    private val segmentsRects = mutableListOf<RectF>()

    private val paintGlow = Paint().apply {
        setShadowLayer(100f, 0f, 0f, Common.colorGlow)
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    override fun recalculate() {
        val info = Common.selectedYearInfo
        segmentsRects.clear()

        val rectW = clientWidth - figureMargin * 2f
        val rectH = if (clientHeight > clientWidth)
            clientHeight / 5f
        else
            clientHeight / 2f
        var x = (clientWidth - rectW) / 2
        val y = (clientHeight - rectH) / 2
        val y2 = y + rectH
        val segmentWidth = rectW / info.days.size
        var segmentAddon = rectW - (segmentWidth * info.days.size)
        outerRect = RectF(x, y, x + rectW, y + rectH)
        for (i in 0 until info.days.size) {
            val x2 = x + segmentWidth + if (segmentAddon > 0) 1 else 0
            segments.add(Segment().apply {
                color = getDayColor(info, i)

                addPoint(x, y)
                addPoint(x2 + 1, y)
                addPoint(x2 + 1, y2)
                addPoint(x, y2, true)

                addSelectionPoint(x, y)
                addSelectionPoint(x2, y)
                addSelectionPoint(x2, y2)
                addSelectionPoint(x, y2, true)
            })
            segmentsRects.add(RectF(x, y, x2, y2))

            x = x2
            segmentAddon--
        }
    }

    override fun getDayIndex(x: Float, y: Float): Int {
        for (i in 0 until segmentsRects.size)
            if (x in segmentsRects[i].left..segmentsRects[i].right)
                return i
        return 0
    }

    override fun beforeDraw(canvas: Canvas) {
        if (Common.nightMode)
            canvas.drawRect(outerRect, paintGlow)
    }
}