package com.vadimdorofeev.orthodoxlentendays

import android.os.Bundle
import android.text.SpannedString
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.Period
import kotlin.math.abs


class ScheduleFragment : Fragment(R.layout.fragment_schedule) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        schedule = view.findViewById(R.id.schedule)

        Common.selectedDate.observe(viewLifecycleOwner) { prepareItems() }

        prepareItems()
        schedule.adapter = ScheduleAdapter(items)

        Common.selectedDate.observe(viewLifecycleOwner) {
            prepareItems()
            schedule.adapter = ScheduleAdapter(items)
            schedule.scrollToPosition(todayPos)
        }
    }

    private lateinit var schedule: RecyclerView
    private val items = mutableListOf<ScheduleItem>()
    private var todayPos = -1

    private fun getScheduleItem(title: String, date: LocalDate, isFasting: Boolean): ScheduleItem {
        val item = ScheduleItem(ScheduleItemKind.Event)
        item.title = HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
        item.day = date.dayOfMonth.toString()
        item.isFasting = isFasting

        // Если два дня подряд с одним числом, то у второго дата должна быть пустой
        if (items.size != 0) {
            val last = items.last()
            if (last.kind == ScheduleItemKind.Event && last.day == item.day)
                item.day = ""
        }

        // Текст смещения события относительно сегодняшнего дня
        val period = Period.between(Common.today, date)
        if (period.years == 0 && period.months == 0 && period.days == 0)
            item.offset = getString(R.string.offset_today)
        else if (period.years == 0 && period.months == 0 && period.days == 1)
            item.offset = getString(R.string.offset_tomorrow)
        else if (period.years == 0 && period.months == 0 && period.days == -1)
            item.offset = getString(R.string.offset_yesterday)
        else {
            val yearsOffset = if (period.years == 0) ""
                else resources.getQuantityString(R.plurals.offset_year, abs(period.years), abs(period.years)) + " "
            val monthsOffset = if (period.months == 0) ""
                else resources.getQuantityString(R.plurals.offset_month, abs(period.months), abs(period.months)) + " "
            val daysOffset = if (period.days == 0) ""
                else resources.getQuantityString(R.plurals.offset_day, abs(period.days), abs(period.days)) + " "
            val format = getString(if (period.isNegative) R.string.offset_past else R.string.offset_future)
            item.offset = format
                .replace("%y", yearsOffset)
                .replace("%m", monthsOffset)
                .replace("%d", daysOffset)
        }

        // Время события: <0 - в прошлом, 0 - сегодня, >0 - в будущем
        item.tense = date.compareTo(Common.today)

        return item
    }

    // Создание сегодняшней рамки
    private fun getTodayScheduleItem(): ScheduleItem {
        return ScheduleItem(ScheduleItemKind.Today).apply {
            title = SpannedString(Common.dateToStr(Common.today))
        }
    }

    private fun prepareItems() {
        val info = Common.selectedYearInfo
        items.clear()

        var prevMonth = -1
        var prevIsPast = true
        var monthItem: ScheduleItem? = null
        todayPos = -1
        for (day in info.schedule) {
            val month = day.date.monthValue - 1
            if (month != prevMonth) {
                // Если предыдущий элемент - тоже название месяца,
                // т.е. событий в нём не было, то удаляем элемент из расписания
                if (items.size != 0 && items[items.size - 1].kind == ScheduleItemKind.MonthName)
                    items.removeAt(items.size - 1)

                monthItem = ScheduleItem(ScheduleItemKind.MonthName).apply {
                    title = SpannedString(Common.titlesMonthSingle[month])
                    color = if (Common.nightMode)
                        Common.themes[Common.currentTheme].colors[month * 31]
                    else
                        Common.themes[Common.currentTheme].lightenColors[month * 31]
                }
                prevMonth = month
            }

            val item = if (day.fastingBegin != Fasting.None || day.fastingEnd != Fasting.None)
                getScheduleItem(
                    if (day.fastingBegin != Fasting.None)
                        "${Common.titlesFasting[day.fastingBegin]} ${getString(R.string.beginning)}"
                    else if (day.fastingEnd != Fasting.None)
                        "${Common.titlesFasting[day.fastingEnd]} ${getString(R.string.ending)}"
                    else
                        "",
                    day.date,
                    day.fasting != Fasting.None)
            else
                getScheduleItem(
                    Common.titlesHoliday[day.holiday] ?: "",
                    day.date,
                    day.fasting != Fasting.None)

            // Если год текущий и переход из прошлого, нужно добавить сегодняшний день
            if (prevIsPast && item.tense >= 0 && day.date.year == Common.today.year) {

                if (monthItem != null && month <= Common.today.monthValue - 1) {
                    items.add(monthItem)
                    monthItem = null
                }

                prevIsPast = false
                if (item.tense > 0) // Переход сразу в будущее - создаётся сегодняшняя рамка
                    items.add(getTodayScheduleItem())

                // Поиск заголовка месяца, чтобы потом можно было прокрутить список до него
                todayPos = items.size - 1
                while (todayPos > 0 && items[todayPos].kind != ScheduleItemKind.MonthName)
                    todayPos--
            }

            if (monthItem != null) {
                items.add(monthItem)
                monthItem = null
            }

            items.add(item)
        }

        // Если год текущий, но сегодняшней рамки нет, то надо её создать
        if (info.year == Common.today.year && todayPos == -1) {
            items.add(getTodayScheduleItem())
            todayPos = items.size - 1
        }

        // Поиск заголовка сегодняшнего месяца, чтобы потом можно было прокрутить список до него
        if (todayPos != -1)
            while (todayPos > 0 && items[todayPos].kind != ScheduleItemKind.MonthName)
                todayPos--
    }
}