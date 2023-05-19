package com.rahul.kotlin.architecture.lifecycle.observer

import androidx.lifecycle.Observer

/**
 * Observer class to observe only non null values, it receives lambda function as an
 * argument which is only called when the emitted value is not null
 */
class NonNullObserver<T>(private val consumer: (content: T) -> Unit) : Observer<T?> {
    override fun onChanged(t: T?) {
        consumer(t ?: return)
    }
}
