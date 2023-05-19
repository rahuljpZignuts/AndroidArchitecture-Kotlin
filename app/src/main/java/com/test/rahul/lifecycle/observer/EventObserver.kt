package com.test.rahul.lifecycle.observer

import androidx.lifecycle.Observer
import com.test.rahul.lifecycle.observable.Event

/**
 * Observer class to observe event instances, it receives lambda function as an argument
 * which is only called when the event has not been handled before once at least
 */
class EventObserver<T>(private val consumer: (content: T) -> Unit) : Observer<Event<T?>> {
    override fun onChanged(event: Event<T?>?) {
        event?.getEvent()?.let { content -> consumer(content) }
    }
}
