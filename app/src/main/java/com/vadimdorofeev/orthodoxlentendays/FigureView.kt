package com.vadimdorofeev.orthodoxlentendays

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.applyCanvas
import java.time.LocalDate


abstract class FigureView @JvmOverloads constructor(context: Context,
                                           attrs: AttributeSet? = null,
                                           defStyleAttr: Int = 0,
                                           defStyleRes: Int = 0) :
                                           View(context, attrs, defStyleAttr, defStyleRes) {

    protected var clientWidth = 0
    protected var clientHeight = 0

    protected var figureMargin = 0

    private var bitmap: Bitmap? = null

    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val paintSelection = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    protected val segments = mutableListOf<Segment>()

    // Подбор цвета для сегмента
    protected fun getDayColor(info: YearInfo, index: Int): Int {
        if (info.days[index].isSuperMainHoliday &&
            (Common.holidaysMode == HolidaysMode.SuperMain ||
             Common.holidaysMode == HolidaysMode.Great12)) {
            // Один из трёх главных праздников
            return if (info.days[index].isPast && Common.lightenPast)
                Common.colorHoliday3Past
            else
                Common.colorHoliday3
        }
        else if (info.days[index].isGreatFeastHoliday && Common.holidaysMode == HolidaysMode.Great12) {
            // Один из двунадесятых праздников
            return if (info.days[index].isPast && Common.lightenPast)
                Common.colorHoliday12Past
            else
                Common.colorHoliday12
        }
        else {
            // Праздник показывать не нужно
            return if (info.days[index].fasting != Fasting.None) {
                // Постный день
                if (info.days[index].isPast && Common.lightenPast)
                    Common.themes[Common.currentTheme].lightenColors[index]
                else
                    Common.themes[Common.currentTheme].colors[index]
            } else {
                // Непостный день
                if (info.days[index].isPast && Common.lightenPast)
                    Color.GRAY
                else
                    Color.BLACK
            }
        }
    }

    protected abstract fun recalculate()
    protected abstract fun beforeDraw(canvas: Canvas)
    protected abstract fun getDayIndex(x: Float, y: Float): Int

    private fun prepareImage() {
        bitmap = Bitmap.createBitmap(clientWidth, clientHeight, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply { isAntiAlias = true }
        bitmap?.applyCanvas {

            // Фон
            paint.color = Common.colorBg
            drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

            // Дополнительные эффекты вроде свечения и т.д.
            beforeDraw(this)

            // Сегменты фигуры
            for (segment in segments) {
                paint.color = segment.color
                drawPath(segment.path, paint)
            }
        }
    }

    fun refresh() {
        if (clientWidth != 0) {
            segments.clear()
            recalculate()
            prepareImage()
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        clientWidth = w
        clientHeight = h

        figureMargin = (clientWidth * 0.05f).toInt()
        refresh()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmap != null) {
            val index = (Common.selectedDate.value ?: Common.today).dayOfYear - 1
            canvas.drawBitmap(bitmap!!, 0f, 0f, paint)
            canvas.drawPath(segments[index].selection, paintSelection)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Обработка только событий нажатия и перемещения
        if (event != null && (event.action == MotionEvent.ACTION_DOWN ||
                              event.action == MotionEvent.ACTION_MOVE)) {
            val date = Common.selectedDate.value ?: Common.today
            Common.selectedDate.value = LocalDate.ofYearDay(
                date.year,
                getDayIndex(event.x, event.y) + 1
            )
            invalidate()
        }
        return true
    }
}