package com.test.rahul.lifecycle.observable

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Wrapper class for data that exposed via observables representing an event
 */
open class Event<out T> protected constructor(private val content: T) {
    private val consumed: AtomicBoolean = AtomicBoolean(false)
    val isConsumed: Boolean get() = consumed.get()

    /**
     * Returns the event and prevents its use again by marking the value as consumed
     */
    fun getEvent(): T? = if (consumed.compareAndSet(false, true)) content else null

    /**
     * Returns the event, even if it's already been consumed
     */
    fun peekEvent(): T = content

    companion object {
        fun empty(): Event<Any> = Event(content = Any())
        fun <T> create(content: T): Event<T> = Event(content = content)
    }
}
