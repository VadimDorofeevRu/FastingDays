package com.vadimdorofeev.orthodoxlentendays

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import java.time.LocalDate

class CalendarView @JvmOverloads constructor(context: Context,
                                             attrs: AttributeSet? = null,
                                             defStyleAttr: Int = 0,
                                             defStyleRes: Int = 0) :
                                             FigureView(context, attrs, defStyleAttr, defStyleRes) {

    private val segmentsRects = mutableListOf<RectF>()

    private val paintGlow = Paint().apply {
        setShadowLayer(100f, 0f, 0f, Common.colorGlow)
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    override fun recalculate() {
        val info = Common.selectedYearInfo
        segmentsRects.clear()

        val hNums = 54 // Количество недель, максимально возможное
        val wNums = 7  // Количество дней в неделе

        val wMargin = clientWidth * 0.2f // Отступ 20% с каждой стороны

        // Размеры одного блока
        val hSize = (clientHeight - figureMargin * 2) / hNums
        val wSize = (clientWidth - wMargin * 2 - figureMargin * 2) / wNums

        var row = 0

        val year = (Common.selectedDate.value ?: Common.today).year
        for (i in 0 until info.days.size) {
            val ld = LocalDate.ofYearDay(year, i + 1)
            val dow = ld.dayOfWeek.value - 1 // Т.е. 0 - понедельник, 1 - вторник, ...

            val left = figureMargin + wMargin + wSize * dow
            val top = figureMargin + hSize * row

            val segment = Segment().apply {
                color = getDayColor(info, i)

                // Сегмент
                addPoint(left + 1f, top + 1f)
                addPoint(left + wSize - 1f, top + 1f)
                addPoint(left + wSize - 1f, top + hSize - 1f)
                addPoint(left + 1f, top + hSize - 1f, true)

                // Выделитель дня
                addSelectionPoint(left + 4f, top + 4f)
                addSelectionPoint(left + wSize - 4f, top + 4f)
                addSelectionPoint(left + wSize - 4f, top + hSize - 4f)
                addSelectionPoint(left + 4f, top + hSize - 4f, true)
            }
            segments.add(segment)

            segmentsRects.add(RectF(left, top.toFloat(), left + wSize, top.toFloat() + hSize))

            if (dow == 6) // Воскресенье - переход на новую строку
                row++
        }
    }

    override fun getDayIndex(x: Float, y: Float): Int {
        for (i in 0 until segmentsRects.size)
            if ((x in segmentsRects[i].left..segmentsRects[i].right) &&
                (y in segmentsRects[i].top..segmentsRects[i].bottom))
                return i
        return 0
    }

    override fun beforeDraw(canvas: Canvas) {
        if (Common.nightMode)
            for (segmentRect in segmentsRects)
                canvas.drawRect(segmentRect, paintGlow)
    }
}