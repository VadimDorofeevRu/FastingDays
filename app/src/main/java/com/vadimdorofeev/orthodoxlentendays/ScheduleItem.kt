package com.vadimdorofeev.orthodoxlentendays

import android.text.Spanned
import android.text.SpannedString

class ScheduleItem(val kind: ScheduleItemKind) {
    var title: Spanned = SpannedString("")
    var offset = ""
    var day = ""
    var isFasting = false
    var tense = 0
    var color = 0
}