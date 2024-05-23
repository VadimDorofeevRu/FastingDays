package com.vadimdorofeev.orthodoxlentendays

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class RingView @JvmOverloads constructor (context: Context,
                                          attrs: AttributeSet? = null,
                                          defStyleAttr: Int = 0,
                                          defStyleRes: Int = 0) :
                                            FigureView(context, attrs, defStyleAttr, defStyleRes) {

    private var step = 0f // Размер сегмента в градусах

    private var centerX = 0f // Центр кольца
    private var centerY = 0f

    private var innerRadius = 0f // Внутренний радиус кольца
    private var outerRadius = 0f // Внешний радиус кольца

    private val paintGlow = Paint().apply {
        setShadowLayer(100f, 0f, 0f, Common.colorGlow)
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    // Координаты точки с заданным углом и расстоянием от центра кольца
    private fun getPoint(radius: Float, angle: Float): PointF {
        val a = (Math.PI / 180) * angle
        return PointF(
            (centerX + radius * cos(a)).toFloat(),
            (centerY + radius * sin(a)).toFloat()
        )
    }

    // Угол для заданного дня, с учётом поворота кольца
    private fun getAngle(index: Int): Float {
        var angle = index * step - 90
        if (Common.flipRing)
            angle = 360 - angle + 180
        return angle % 360
    }

    override fun recalculate() {
        val info = Common.selectedYearInfo

        step = (1f / info.days.size) * 360
        centerX = clientWidth / 2f
        centerY = clientHeight / 2f
        outerRadius = if (clientHeight > clientWidth)
            clientWidth / 2f - figureMargin
        else
            clientHeight / 2f - figureMargin
        innerRadius = outerRadius / 2f

        for (i in 0 until info.days.size) {
            val segment = Segment()
            segment.color = getDayColor(info, i)

            // Первый край сегмента и выделителя
            var angle = getAngle(i)

            var p = getPoint(outerRadius, angle)
            segment.addPoint(p.x, p.y)
            segment.addSelectionPoint(p.x, p.y)

            p = getPoint(innerRadius, angle)
            segment.addPoint(p.x, p.y)
            segment.addSelectionPoint(p.x, p.y)

            // Второй край сегмента
            angle = getAngle(if (i != info.days.size - 1) i + 2 else i + 1)
            segment.addPoint(getPoint(innerRadius, angle))
            segment.addPoint(getPoint(outerRadius, angle), true)

            // Второй край выделителя дня
            angle = getAngle(i + 1)
            segment.addSelectionPoint(getPoint(innerRadius, angle))
            segment.addSelectionPoint(getPoint(outerRadius, angle), true)

            segments.add(segment)
        }

        // Свечение для тёмного режима

        val margin = (clientWidth - outerRadius * 2) / 2
        val length = clientWidth / 2f

        paintGlow.strokeWidth = length
        paintGlow.shader = RadialGradient(
            centerX,
            centerY,
            length,
            intArrayOf( // Цвета свечения
                0x00000000,
                0x00000000,
                0xff707070.toInt(),
                0xff606060.toInt(),
                0x00000000),
            floatArrayOf(
                // Ключевые точки для цветов 0..1:
                0f,                                     // Центр кольца
                (innerRadius - margin / 1.3f) / length, // От внутреннего радиуса к центру
                (innerRadius + margin) / length,        // От внутреннего радиуса к краю
                (outerRadius - margin) / length,        // От внешнего радиуса к центру
                1f                                      // Край экрана
            ),
            Shader.TileMode.CLAMP)
    }

    override fun getDayIndex(x: Float, y: Float): Int {
        val info = Common.selectedYearInfo
        val a = (atan2(centerY - y, x - centerX) * 180 / Math.PI + 270) % 360
        val day = if (Common.flipRing)
            (a / step).toInt()
        else
            ((360 - a) / step).toInt()
        return (day + info.days.size) % info.days.size
    }

    override fun beforeDraw(canvas: Canvas) {
        if (Common.nightMode)
            canvas.drawCircle(
                centerX, centerY,
                outerRadius,
                paintGlow
            )
    }
}