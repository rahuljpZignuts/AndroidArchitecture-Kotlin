package com.rahul.kotlin.architecture.lifecycle.observer

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Observer that receives only one update after subscription when the given condition is met, used
 * for one time events for given condition.
 *
 * This avoids a common problem with events: on network response, an update can be listened if the
 * observer is active. This Observer only calls the registerObserver for the first time and
 * unregister itself before it emits the value that has been received.
 *
 * Note that only one value is going to be delivered from this observer.
 */
abstract class PredicateEventObserver<T>(
    private val source: LiveData<T>,
    private val predicate: (T?) -> Boolean,
) : Observer<T> {
    /**
     * Called when the data is changed.
     *
     * @param data The new data delivered
     */
    abstract fun onChangeReceived(data: T?)

    private val pending = AtomicBoolean(false)

    override fun onChanged(value: T?) {
        val emit: Boolean = predicate(value)
        if (emit && pending.compareAndSet(false, true)) {
            removeObserver()
            onChangeReceived(value)
        }
    }

    private fun removeObserver() {
        source.removeObserver(this)
    }
}
