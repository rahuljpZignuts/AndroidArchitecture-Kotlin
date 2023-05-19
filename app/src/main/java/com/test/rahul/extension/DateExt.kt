package com.test.rahul.extension

import android.content.Context
import androidx.annotation.StringRes
import com.test.rahul.utils.DateTimeUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.format(pattern: String, locale: Locale = Locale.ENGLISH): String {
    return try {
        SimpleDateFormat(pattern, locale).format(this)
    } catch (ex: Exception) {
        Timber.e(ex)
        null
    } ?: ""
}

val Date.asCalendar: Calendar
    get() = Calendar.getInstance().also { it.time = this }

val Date.dayOfMonth: Int
    get() = asCalendar.get(Calendar.DAY_OF_MONTH)

val Date.daySuffix: String
    get() = when (val d = dayOfMonth) {
        in 11..13 -> "th"
        in 1..31 -> when (d % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
        else -> ""
    }

val Date.dateYear: Int
    get() = asCalendar.get(Calendar.YEAR)

val Date.dateMonth: Int
    get() = asCalendar.get(Calendar.MONTH)

val Date.dayOfYear: Int
    get() = asCalendar.get(Calendar.DAY_OF_YEAR)

val Date.nextDate: Date
    get() = asCalendar.also { it.add(Calendar.DATE, 1) }.time

fun Date.mergeFormats(
    context: Context,
    date: String? = null,
    month: String? = null,
    year: String? = null,
    @StringRes formattedDateFormat: Int,
): String = context.getArgumentString(formattedDateFormat, date, month, year)

fun Date.isSame(date: Date): Boolean = (dayOfYear == date.dayOfYear && dateYear == date.dateYear)

fun Date.toCustomFormat(
    context: Context,
    dateFormat: String? = null,
    monthFormat: String? = null,
    yearFormat: String? = null,
    @StringRes formattedDateFormat: Int,
): String {
    val date = dateFormat?.let { SimpleDateFormat(it, Locale.ENGLISH).format(this) }
    val month = monthFormat?.let { SimpleDateFormat(it, Locale.getDefault()).format(this) }
    val year = yearFormat?.let { SimpleDateFormat(it, Locale.ENGLISH).format(this) }
    return context.getArgumentString(formattedDateFormat, date, month, year)
}

fun Date.dropTime(): Date {
    val calendar = this.asCalendar
    calendar.set(Calendar.HOUR, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun Date.isPast() = before(DateTimeUtils.getCurrentDate())
