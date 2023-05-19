package com.rahul.kotlin.architecture.utils

import android.content.Context
import com.rahul.kotlin.architecture.R
import com.rahul.kotlin.architecture.application.DateFormats
import com.rahul.kotlin.architecture.extension.format
import com.rahul.kotlin.architecture.extension.getArgumentString
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object DateTimeUtils {

    fun getCurrentTimeInMillis() = Calendar.getInstance().timeInMillis

    fun getCurrentTimeInSeconds() =
        TimeUnit.MILLISECONDS.toSeconds(Calendar.getInstance().timeInMillis)

    fun getCurrentDate(): Date = Calendar.getInstance().time

    fun getCurrentWeekDates(calendar: Calendar = Calendar.getInstance()): MutableList<Date> {
        val list = mutableListOf<Date>()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        if (currentDay == Calendar.FRIDAY || currentDay == Calendar.SATURDAY) {
            calendar.firstDayOfWeek = currentDay
            calendar.set(Calendar.DAY_OF_WEEK, currentDay)
        } else {
            calendar.firstDayOfWeek = Calendar.SUNDAY
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        }
        for (i in 0..6) {
            list.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return list
    }

    fun getTimeElapsed(fromDate: Date, toDate: Date, context: Context): String {
        val daysDiff = TimeUnit.MILLISECONDS.toDays(fromDate.time.minus(toDate.time))
        val hoursDiff = TimeUnit.MILLISECONDS.toHours(fromDate.time.minus(toDate.time))
        val minsDiff = TimeUnit.MILLISECONDS.toMinutes(fromDate.time.minus(toDate.time))
        return when {
            daysDiff > 7 -> toDate.format(DateFormats.FORMAT_DATE_FULL.reversed())
            daysDiff in 1..7 -> context.getArgumentString(R.string.days_ago, daysDiff.toString())
            hoursDiff in 1..23 -> context.getArgumentString(
                R.string.hours_ago,
                hoursDiff.toString()
            )
            minsDiff in 1..59 -> context.getArgumentString(R.string.mins_ago, minsDiff.toString())
            else -> context.getString(R.string.just_now)
        }
    }

    fun calculateTimeInFormat(timeSecs: Long): String {
        val estimated = timeSecs % 86400
        val calculated = estimated % 3600
        val hours = estimated / 3600
        val mins = calculated / 60
        val secs = calculated % 60
        return StringBuilder("").apply {
            if (hours > 0) append("${hours}h")
            if (mins > 0) append(" ${mins}m")
            if (secs > 0) append(" ${secs}s")
            if (this.toString().trim().isEmpty()) append("0m")
        }.toString().trim()
    }

    fun convertToDurationString(timeSecs: Long) =
        String.format(
            "%02d:%02d",
            TimeUnit.SECONDS.toMinutes(timeSecs),
            timeSecs % TimeUnit.MINUTES.toSeconds(1)
        )
}

//Extensions Here
fun String.stringToDate(dateFormat: String, locale: Locale = Locale.ENGLISH): Date {
    return try {
        val format = SimpleDateFormat(dateFormat, locale)
        format.timeZone = TimeZone.getTimeZone("UTC") // considering time from server is in UTC
        format.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    } ?: DateTimeUtils.getCurrentDate()
}

fun String.stringToAmbiguousDate(locale: Locale = Locale.ENGLISH): Date {
    var formattedDate: Date? = null
    DateFormats.formatsAmbiguous.forEach { dateFormat ->
        if (formattedDate == null) {
            try {
                val format = SimpleDateFormat(dateFormat, locale)
                format.timeZone = TimeZone.getTimeZone("UTC")
                formattedDate = format.parse(this)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }
    return formattedDate ?: DateTimeUtils.getCurrentDate()
}
