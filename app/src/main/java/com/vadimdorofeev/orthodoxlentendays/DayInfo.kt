package com.vadimdorofeev.orthodoxlentendays

import java.time.LocalDate

class DayInfo(val date: LocalDate) {
    var fasting = Fasting.None
    var holiday = Holiday.None
    var isPast = false

    // День начала и окончания больших постов, для вида Расписание
    var fastingBegin = Fasting.None
    var fastingEnd = Fasting.None

    // Три самых главных праздника
    val isSuperMainHoliday
        get() = holiday == Holiday.NativityOfChrist ||
                holiday == Holiday.Easter ||
                holiday == Holiday.Pentecost

    // Двунадесятые праздники
    val isGreatFeastHoliday
        get() = holiday == Holiday.NativityOfTheTheotokos ||
                holiday == Holiday.PresentationOfTheTheotokos ||
                holiday == Holiday.NativityOfChrist ||
                holiday == Holiday.BaptismOfChrist ||
                holiday == Holiday.PresentationOfJesus ||
                holiday == Holiday.Annunciation ||
                holiday == Holiday.EntryIntoJerusalem ||
                holiday == Holiday.Easter ||
                holiday == Holiday.AscensionOfChrist ||
                holiday == Holiday.Pentecost ||
                holiday == Holiday.Transfiguration ||
                holiday == Holiday.DormitionOfTheTheotokos ||
                holiday == Holiday.ExaltationOfTheCross
}