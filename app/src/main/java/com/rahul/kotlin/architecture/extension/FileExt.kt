package com.rahul.kotlin.architecture.extension

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.rahul.kotlin.architecture.application.Config
import com.rahul.kotlin.architecture.network.retrofit.toProgressRequestBody
import com.rahul.kotlin.architecture.utils.FileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL

val URL?.toFile get() = if (this != null) File(this.file) else null

@Throws(IllegalArgumentException::class)
fun ContentResolver.getFileName(uri: Uri): String {
    val cursor = query(uri, null, null, null, null)
    val name: String
    if (cursor != null) {
        val indexOfDisplayName = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        name = cursor.getString(indexOfDisplayName)
        cursor.close()
    } else name = ""
    return name
}

fun Uri.toFile(context: Context) = try {
    val imageFileDescriptor = context.contentResolver.openFileDescriptor(
        this, "r", null
    )
    val inputStream = FileInputStream(imageFileDescriptor?.fileDescriptor)
    val fileName: String = context.contentResolver.getFileName(this)
    val file = context.createFile(FileUtils.EXTERNAL_FILES_DIRECTORY, fileName)
    val outputStream = FileOutputStream(file)
    inputStream.copyTo(outputStream)
    imageFileDescriptor?.close()
    file
} catch (e: Exception) {
    null
}

fun Context.createFile(directory: String, fileName: String): File =
    File(this.getExternalFilesDir(directory), fileName)

fun File.toUri(context: Context): Uri {
    return FileProvider.getUriForFile(
        context, Config.applicationId + ".fileProvider", this
    )
}

/**
 * Returns file size in MBs
 */
fun File.size() = length() / (1024 * 1024)

fun File.createProgressFormData(context: Context, progressLiveData: MutableLiveData<Int?>) =
    MultipartBody.Part.createFormData(
        name = "file",
        filename = this.name,
        body = this.toProgressRequestBody(
            mediaType = FileUtils.getMimeType(context, this.toUri())?.toMediaTypeOrNull(),
            progressLiveData = progressLiveData
        )
    )

fun File.isValid() = this.exists() && this.size() > 0
