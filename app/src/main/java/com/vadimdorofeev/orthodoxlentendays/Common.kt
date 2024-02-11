package com.vadimdorofeev.orthodoxlentendays

import androidx.lifecycle.MutableLiveData
import java.time.LocalDate


object Common {
    private val yearInfos = mutableMapOf<Int, YearInfo>()

    // Сегодняшняя дата
    val today = LocalDate.now()!!

    // Выделенная дата
    var selectedDate = MutableLiveData(today)

    // Информация по выделенному году
    val selectedYearInfo: YearInfo
        get() {
            val year = (selectedDate.value ?: today).year
            if (!yearInfos.containsKey(year))
                yearInfos[year] = YearInfo(year)
            return yearInfos.getOrDefault(year, YearInfo(year))
        }

    // Названия постов и праздников
    val titlesFasting = mutableMapOf<Fasting, String>()
    val titlesHoliday = mutableMapOf<Holiday, String>()

    // Названия месяцев в родительном падеже ("января")
    val titlesMonth = mutableListOf<String>()

    // Названия месяцев в именительном падеже ("Январь)
    val titlesMonthSingle = mutableListOf<String>()

    // Названия дней недели ("воскресенье")
    val titlesDay = mutableListOf<String>()

    // Формат даты ("10 февраля, воскресенье")
    var dateFormat = "%d %m, %w"

    // Подготовка локализованной даты по формату
    fun dateToStr(date: LocalDate): String {
        return dateFormat
            .replace("%d", date.dayOfMonth.toString())
            .replace("%m", Common.titlesMonth[date.monthValue - 1])
            .replace("%w", Common.titlesDay[date.dayOfWeek.value - 1])
    }

    // Темы оформления
    val themes = mutableListOf<Theme>()
    var currentTheme = 0

    // Настройки
    var nightMode = false
    var lightenPast = true
    var flipRing = false
    var holidaysMode = HolidaysMode.SuperMain

    // Цвета

    // Цвет фона для фигур
    var colorBg             = 0

    // Цвет трёх главных праздников
    val colorHoliday3       = 0xFFFF4040.toInt()
    val colorHoliday3Past   = 0xFFFF8080.toInt()

    // Цвет двунадесятых празднгиков
    val colorHoliday12      = 0xFFFFA000.toInt()
    val colorHoliday12Past  = 0xFFFFC060.toInt()

    // Цвета постных и непостных дней в Расписании
    var colorTextFasting    = 0xFF00A0F0.toInt()
    var colorTextNonFasting = 0xFFF8F8F8.toInt()

    // Цвета сегодняшнего дня в Расписании
    var colorTodayBg        = 0
    var colorTodayBorder    = 0

    // Цвет свечения фигур в тёмном режиме
    var colorGlow           = 0x80FFFFFF.toInt()
}