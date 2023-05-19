package com.rahul.kotlin.architecture.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.IOException

object FileUtils {
    const val EXTERNAL_FILES_DIRECTORY = "Files"

    fun generateFileName(ext: String): String = "File_${System.currentTimeMillis()}.$ext"

    fun getExtension(url: String): String {
        val lastIndex = url.lastIndexOf('.')
        return if (lastIndex > -1) {
            url.substring(lastIndex)
        } else ""
    }

    fun getFileType(context: Context, url: String): FileType {
        val mimeType = getMimeType(context = context, uri = Uri.parse(url))
        return (mimeType?.let {
            when {
                it.contains("pdf") -> FileType.PDF
                it.contains("image") -> FileType.IMAGE
                it.contains("video") -> FileType.VIDEO
                else -> FileType.UNKNOWN
            }
        } ?: FileType.UNKNOWN)
    }

    fun getMimeType(context: Context, uri: Uri): String? {
        return if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            context.contentResolver.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
        }
    }

    fun clearExternalFilesDirectory(context: Context) {
        try {
            val directory = context.getExternalFilesDir(EXTERNAL_FILES_DIRECTORY)
            if (directory?.exists() == true && directory.isDirectory) {
                directory.deleteRecursively()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

enum class FileType {
    IMAGE,
    VIDEO,
    PDF,
    UNKNOWN
}
