package com.vadimdorofeev.orthodoxlentendays

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment


class FigureFragment(private val mode: ViewMode? = null) : Fragment(R.layout.fragment_figure) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Добавление фигуры нужного типа
        // null - если система сама восстанавливает фрагмент без параметра, можно игнорировать
        if (mode != null) {

            root = view as ConstraintLayout

            val figure = when (mode) {
                ViewMode.Ring -> RingView(view.context)
                ViewMode.Rect -> RectView(view.context)
                else -> CalendarView(view.context)
            }

            figure.id = View.generateViewId()
            root.addView(figure)

            // Привязка фигуры к окружению
            ConstraintSet().apply {
                clone(root)
                connect(figure.id, ConstraintSet.TOP, R.id.holiday, ConstraintSet.BOTTOM)
                connect(figure.id, ConstraintSet.BOTTOM, R.id.stat_total, ConstraintSet.TOP)
                connect(figure.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(figure.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                constrainHeight(figure.id, ConstraintSet.MATCH_CONSTRAINT)
                constrainWidth(figure.id, ConstraintSet.MATCH_CONSTRAINT)
                applyTo(root)
            }

            // Обработка изменения года
            Common.selectedDate.observe(viewLifecycleOwner) {
                val date = Common.selectedDate.value ?: Common.today
                if (date.year != prevYear) {
                    // Пересчёт только если изменился год
                    figure.refresh()
                    prevYear = date.year
                }
                updateInfo()
            }

            updateInfo()
        }
    }

    private lateinit var root: ConstraintLayout
    private val tvDate by lazy { root.findViewById<TextView>(R.id.date) }
    private val tvFasting by lazy { root.findViewById<TextView>(R.id.fasting) }
    private val tvHoliday by lazy { root.findViewById<TextView>(R.id.holiday) }
    private val tvStatTotal by lazy { root.findViewById<TextView>(R.id.stat_total) }
    private val tvStatPassed by lazy { root.findViewById<TextView>(R.id.stat_passed) }
    private var prevYear = 0

    private fun updateInfo() {
        val date = Common.selectedDate.value ?: Common.today
        val index = date.dayOfYear - 1
        val info = Common.selectedYearInfo
        val total = info.getFastingDaysCount()
        val totalPercent = total * 100 / info.days.size
        val passed = info.getFastingDaysPassed(index)
        val passedPercent = passed * 100 / total

        tvDate.text = Common.dateToStr(date)
        tvFasting.text = Common.titlesFasting[info.days[index].fasting]
        tvHoliday.text = Common.titlesHoliday[info.days[index].holiday]

        tvStatTotal.text = resources.getQuantityString(R.plurals.stat_total, total,
            date.year,
            total,
            totalPercent
        )
        tvStatPassed.text = resources.getQuantityString(R.plurals.stat_passed, passed,
            passed,
            passedPercent
        )
    }
}