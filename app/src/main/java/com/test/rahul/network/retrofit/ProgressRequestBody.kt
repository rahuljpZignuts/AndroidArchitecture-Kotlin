package com.test.rahul.network.retrofit

import androidx.lifecycle.MutableLiveData
import com.test.rahul.application.Config
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class ProgressRequestBody(
    private val file: File,
    private val mediaType: MediaType?,
    private val progressLiveData: MutableLiveData<Int?>,
) : RequestBody() {
    private var writeToCalls = 0

    override fun contentLength() = file.length()

    override fun contentType() = mediaType

    override fun writeTo(sink: BufferedSink) {
        if (Config.environment.debugLogsEnabled && ++writeToCalls <= 1) return
        val fileLength = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var uploaded: Long = 0
        FileInputStream(file).use { ins ->
            var lastProgressPercentUpdate = 0.0f
            var read = ins.read(buffer)
            while (read != -1) {
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
                read = ins.read(buffer)
                val progress = (uploaded.toFloat() / fileLength.toFloat()) * 100f
                if (progress - lastProgressPercentUpdate > 1 || progress == 100f) {
                    progressLiveData.postValue(progress.toInt())
                    lastProgressPercentUpdate = progress
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }
}

fun File.toProgressRequestBody(
    mediaType: MediaType?, progressLiveData: MutableLiveData<Int?>,
) = ProgressRequestBody(
    file = this,
    mediaType = mediaType,
    progressLiveData = progressLiveData
)
