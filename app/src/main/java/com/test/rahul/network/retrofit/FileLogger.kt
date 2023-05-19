package com.test.rahul.network.retrofit

import android.content.Context
import com.test.rahul.application.DateFormats
import com.test.rahul.extension.createFile
import com.test.rahul.extension.format
import com.test.rahul.utils.DateTimeUtils
import okhttp3.logging.HttpLoggingInterceptor
import java.io.FileOutputStream

class FileLogger(val context: Context) : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        val dateTime = DateTimeUtils.getCurrentDate()
        val fileName = dateTime.format(DateFormats.FORMAT_DATE_FULL_REVERSE) + ".txt"
        val file = context.createFile("Logs", fileName)
        FileOutputStream(file, true).bufferedWriter().use {
            it.write("$dateTime:\n$message\n")
        }
    }
}
