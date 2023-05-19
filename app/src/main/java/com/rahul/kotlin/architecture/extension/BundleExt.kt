package com.rahul.kotlin.architecture.extension

import android.os.Build
import android.os.Bundle

fun <T> Bundle.getParcelableArg(key: String, type: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getParcelable(key, type)
    } else {
        this.getParcelable(key)
    }
}
