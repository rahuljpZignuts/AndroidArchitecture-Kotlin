package com.rahul.kotlin.architecture.extension

import android.content.Intent
import android.os.Build

fun Intent.reset() {
    this.replaceExtras(null)
}

fun Intent.isFromHistory(): Boolean {
    return this.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY != 0
}

fun <T> Intent.getParcelableExtraArg(key: String, type: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getParcelableExtra(key, type)
    } else {
        this.getParcelableExtra(key)
    }
}

