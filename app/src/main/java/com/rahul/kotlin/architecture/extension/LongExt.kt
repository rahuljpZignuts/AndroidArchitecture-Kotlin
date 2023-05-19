package com.rahul.kotlin.architecture.extension

import java.util.Calendar
import java.util.Date

fun Long.toDate(): Date {
    return Date(this)
}

fun Long.toCalendar(): Calendar {
    return Calendar.getInstance().apply {
        time = this@toCalendar.toDate()
    }
}
