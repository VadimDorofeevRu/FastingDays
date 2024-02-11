package com.vadimdorofeev.orthodoxlentendays

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import androidx.core.graphics.applyCanvas


class Theme(val title: String, private val pointColors: IntArray) {

    val colors = Array(366) { 0 }
    val lightenColors = Array(366) { 0 }

    private val lightenFactor = 0.5f

    // Осветление цвета
    // Источник: http://stackoverflow.com/questions/4928772/android-color-darker
    private fun lightenColor(color: Int): Int {
        return Color.argb(
            Color.alpha(color),
            ((Color.red(color) * (1 - lightenFactor) / 255 + lightenFactor) * 255).toInt(),
            ((Color.green(color) * (1 - lightenFactor) / 255 + lightenFactor) * 255).toInt(),
            ((Color.blue(color) * (1 - lightenFactor) / 255 + lightenFactor) * 255).toInt()
        )
    }

    // Генерация цветов с переходом через отрисовку градиента
    private fun generateColors() {
        val positions = FloatArray(pointColors.size) { it.toFloat() / (pointColors.size - 1) }
        val colorsArray = IntArray(pointColors.size) { (pointColors[it] + 0xFF000000).toInt() }
        val bitmap = Bitmap.createBitmap(366, 1, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply {
            style = Paint.Style.FILL
            shader = LinearGradient(0f, 0f, 366f, 1f,
                                    colorsArray, positions,
                                    Shader.TileMode.REPEAT)
        }
        bitmap.applyCanvas {
            drawRect(0f, 0f, 366f, 1f, paint)
        }
        for (i in 0 until 366) {
            colors[i] = bitmap.getPixel(i, 0)
            lightenColors[i] = lightenColor(colors[i])
        }
    }

    init {
        generateColors()
    }
}