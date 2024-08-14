package com.vadimdorofeev.orthodoxlentendays

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year

class YearInfo(val year: Int) {

    lateinit var days: Array<DayInfo>
    var schedule = mutableListOf<DayInfo>()

    private data class Event(
        val offset: Int,
        val month: Int,
        val day: Int,
        val holiday: Holiday,
        val fasting: Fasting? = null
    )

    private val events = listOf(
        // Рождественский сочельник, 24 декабря / 6 января
        Event(-1, 1, 6, Holiday.ChristmasEve),

        // Рождество Христово, 25 декабря / 7 января
        Event(-1, 1, 7, Holiday.NativityOfChrist, Fasting.None),

        // Обрезание Господне, 1/14 января
        Event(-1, 1, 14, Holiday.CircumcisionOfJesus),

        // Крещенский сочельник, 5/18 января
        Event(-1, 1, 18, Holiday.TheophanyEve, Fasting.Daylong),

        // Крещение Господне / Богоявление, 6/19 января
        Event(-1, 1, 19, Holiday.BaptismOfChrist, Fasting.None),

        // Сретение Господне, 2/15 февраля
        Event(-1, 2, 15, Holiday.PresentationOfJesus),

        // Благовещение Пресвятой Богородицы, 25 марта / 7 апреля
        Event(-1, 4, 7, Holiday.Annunciation),

        // День апостолов Петра и Павла, 29 июня / 12 июля
        Event(-1, 7, 12, Holiday.FeastOfSaintsPeterAndPaul),

        // Преображение Господне, 6/19 августа
        Event(-1, 8, 19, Holiday.Transfiguration),

        // Успение Богородицы, 15/28 августа
        Event(-1, 8, 28, Holiday.DormitionOfTheTheotokos),

        // Усекновение главы Иоанна Предтечи, 29 августа / 11 сентября
        Event(-1, 9, 11, Holiday.BeheadingJohnBaptist, Fasting.Daylong),

        // Рождество Пресвятой Богородицы, 8/21 сентября
        Event(-1, 9, 21, Holiday.NativityOfTheTheotokos),

        // Воздвижение Креста Господня, 14/27 сентября
        Event(-1, 9, 27, Holiday.ExaltationOfTheCross, Fasting.Daylong),

        // Покров Пресвятой Богородицы, 1/14 октября
        Event(-1, 10, 14, Holiday.IntercessionOfTheTheotokos),

        // Введение во храм Пресвятой Богородицы, 21 ноября / 4 декабря
        Event(-1, 12, 4, Holiday.PresentationOfTheTheotokos),

        // Вселенская родительская суббота
        Event(-57, 0, 0, Holiday.SaturdayOfMeatfareWeek),

        // Вход Господень в Иерусалим / Вербное воскресенье
        Event(-7, 0, 0, Holiday.EntryIntoJerusalem),

        // Пасха
        Event(0, 0, 0, Holiday.Easter),

        // Радоница / Родительский вторник
        Event(9, 0, 0, Holiday.Radonitsa),

        // Вознесение Господне
        Event(39, 0, 0, Holiday.AscensionOfChrist),

        // Троицкая родительская суббота
        Event(48, 0, 0, Holiday.SaturdayBeforePentecost),

        // День Святой Троицы / Пятидесятница
        Event(49, 0, 0, Holiday.Pentecost),

        // День Святого Духа
        Event(50, 0, 0, Holiday.MondayOfTheHolySpirit)
    )

    private val today = LocalDate.now()

    // Перевод юлианской даты в григорианскую
    private fun julianToGregorian(year: Int, month: Int, day: Int): LocalDate {
        val y = year + 4800
        var m = month - 3
        val jdn = day + (153 * m + 2) / 5 + 365 * y + y / 4 - 32083
        val a = jdn + 32044
        val b = (4 * a + 3) / 146097
        val c = a - (146097 * b) / 4
        val d = (4 * c + 3) / 1461
        val e = c - (1461 * d) / 4
        m = (5 * e + 2) / 153

        return LocalDate.of(
            100 * b + d - 4800 + m / 10,
            m + 3 - 12 * (m / 10),
            e - (153 * m + 2) / 5 + 1
        )
    }

    // Дата Пасхи по юлианскому календарю
    fun getEasterDate(): LocalDate {
        val a = (19 * (year % 19) + 15) % 30
        val b = (2 * (year % 4) + 4 * (year % 7) + 6 * a + 6) % 7
        val month = if ((a + b) > 9) 4 else 3
        val day = if ((a + b) > 9) a + b - 9 else 22 + a + b

        return julianToGregorian(year, month, day)
    }

    // Количество постных дней
    fun getFastingDaysCount(): Int {
        return days.count { it.fasting != Fasting.None }
    }

    // Количество прошедших постных дней
    fun getFastingDaysPassed(dayOfYear: Int): Int {
        var count = 0
        for (i in 0 until dayOfYear)
            if (days[i].fasting != Fasting.None)
                count++
        return count
    }

    // Номер дня в году начиная с 0
    private fun getDateIndex(month: Int, day: Int): Int {
        val date = LocalDate.of(year, month, day)
        return date.dayOfYear - 1
    }

    // Расстановка постных и праздничных дней в году
    private fun initDays() {
        // Инициализация массива
        val count = if (Year.of(year).isLeap) 366 else 365
        days = Array(count) { DayInfo(LocalDate.ofYearDay(year, it + 1)) }

        // Определение в прошлом день или нет
        days.forEach { day ->
            day.isPast = when {
                day.date.year < Common.today.year -> true
                day.date.year > Common.today.year -> false
                else -> day.date.dayOfYear < today.dayOfYear
            }
        }

        // Дата Пасхи
        val easter = getEasterDate()
        val easterDay = getDateIndex(easter.monthValue, easter.dayOfMonth)


        // Посты для графики

        // Посты по средам и пятницам
        for (day in days) {
            if (day.date.dayOfWeek == DayOfWeek.WEDNESDAY)
                day.fasting = Fasting.Wednesday
            if (day.date.dayOfWeek == DayOfWeek.FRIDAY)
                day.fasting = Fasting.Friday
        }

        // Рождественский пост (в начале года), 24 декабря / 6 января
        val nativityLast = getDateIndex(1, 6)
        for (i in 0..nativityLast)
            days[i].fasting = Fasting.Nativity

        // Великий пост
        for (i in easterDay - 48 until easterDay)
            days[i].fasting = Fasting.Great

        // Петров пост, 28 июня / 11 июля
        val apostleLast = getDateIndex(7, 11)
        for (i in easterDay + 57 .. apostleLast)
            days[i].fasting = Fasting.Apostles

        // Успенский пост, 1/14 августа - 14/27 августа
        val dormitionFirst = getDateIndex(8, 14)
        val dormitionLast = getDateIndex(8, 27)
        for (i in dormitionFirst .. dormitionLast)
            days[i].fasting = Fasting.Dormition

        // Рождественский пост (в конце года), 15/28 ноября
        val nativityFirst = getDateIndex(11, 28)
        for (i in nativityFirst until  days.size)
            days[i].fasting = Fasting.Nativity

        // Святки: от Рождества (25 декабря / 7 января) до сочельника Крещения (4/17 января)
        for (i in getDateIndex(1, 7) .. getDateIndex(1, 17))
            days[i].fasting = Fasting.None

        // Сплошная седмица: Мытаря и фарисея
        for (i in easterDay - 69 .. easterDay - 63)
            days[i].fasting = Fasting.None

        // Сплошная седмица: Сырная
        for (i in easterDay - 55 .. easterDay - 49)
            days[i].fasting = Fasting.None

        // Сплошная седмица: Пасхальная
        for (i in easterDay + 1 .. easterDay + 7)
            days[i].fasting = Fasting.None

        // Сплошная седмица: Троицкая
        for (i in easterDay + 50 .. easterDay + 56)
            days[i].fasting = Fasting.None


        // Посты для расписания

        // Рождественский пост (окончание)
        schedule.add(DayInfo(days[nativityLast].date).apply {
            fasting = Fasting.Nativity
            fastingEnd = Fasting.Nativity
        })

        // Великий пост
        schedule.add(DayInfo(days[easterDay - 48].date).apply {
            fasting = Fasting.Great
            fastingBegin = Fasting.Great
        })
        schedule.add(DayInfo(days[easterDay - 1].date).apply {
            fasting = Fasting.Great
            fastingEnd = Fasting.Great
        })

        // Петров пост
        schedule.add(DayInfo(days[easterDay + 57].date).apply {
            fasting = Fasting.Apostles
            fastingBegin = Fasting.Apostles
        })
        schedule.add(DayInfo(days[apostleLast].date).apply {
            fasting = Fasting.Apostles
            fastingEnd = Fasting.Apostles
        })

        // Успенский пост
        schedule.add(DayInfo(days[dormitionFirst].date).apply {
            fasting = Fasting.Dormition
            fastingBegin = Fasting.Dormition
        })
        schedule.add(DayInfo(days[dormitionLast].date).apply {
            fasting = Fasting.Dormition
            fastingEnd = Fasting.Dormition
        })

        // Рождественский пост (начало)
        schedule.add(DayInfo(days[nativityFirst].date).apply {
            fasting = Fasting.Nativity
            fastingBegin = Fasting.Nativity
        })


        // События

        for (event in events) {
            val offset = if (event.offset != -1) easterDay + event.offset
                         else getDateIndex(event.month, event.day)
            days[offset].apply {
                holiday = event.holiday
                if (event.fasting != null)
                    fasting = event.fasting
            }
            schedule.add(DayInfo(days[offset].date).apply {
                fasting = days[offset].fasting
                holiday = event.holiday
                isPast = days[offset].isPast
            })
        }

        schedule.sortWith { a, b -> a.date.dayOfYear.compareTo(b.date.dayOfYear) }
    }

    init {
        initDays()
    }
}