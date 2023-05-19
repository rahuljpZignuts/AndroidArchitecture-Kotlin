package com.rahul.kotlin.architecture.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber

fun <T> Flow<T>.handleExceptions(): Flow<T> = catch { exception ->
    Timber.e(exception)
}
