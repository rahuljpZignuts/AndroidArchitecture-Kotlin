package com.rahul.kotlin.architecture.extension

import android.os.Build

fun <K, V> MutableMap<K, V>.replaceValue(key: K, value: V) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.replace(key, value)
    } else {
        if (this.containsKey(key)) {
            this[key] = value
        }
    }
}
