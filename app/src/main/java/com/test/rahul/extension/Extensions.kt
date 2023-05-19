package com.test.rahul.extension

import timber.log.Timber

val Any.simpleClassName: String get() = this::class.java.simpleName

@Suppress("TooGenericExceptionCaught")
inline fun <T> runSafe(crossinline block: () -> T): T? = try {
    block()
} catch (ex: Throwable) {
    Timber.e(ex)
    null
}
