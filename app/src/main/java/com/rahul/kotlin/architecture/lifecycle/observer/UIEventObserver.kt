package com.rahul.kotlin.architecture.lifecycle.observer

import android.util.SparseArray
import androidx.lifecycle.Observer
import com.rahul.kotlin.architecture.lifecycle.observable.UIEvent
import com.rahul.kotlin.architecture.lifecycle.observable.UIEventType

/**
 * UIEvent observer boilerplate for observing UI events
 */
class UIEventObserver(private val listener: UIEventListener) : Observer<UIEvent> {
    override fun onChanged(event: UIEvent?) {
        when (val type = event?.getEvent()) {
            is UIEventType.Alert -> listener.onShowAlertEventReceived(type, event.extras)
            is UIEventType.Navigate -> listener.onNavigationEventReceived(type, event.extras)
            is UIEventType.Toast -> listener.onShowToastEventReceived(type, event.extras)
            else -> {}
        }
    }
}

/**
 * UI Event callbacks to simplify handling UI events in activities and fragments
 */
interface UIEventListener {
    fun onShowAlertEventReceived(event: UIEventType.Alert, extras: SparseArray<Any?>? = null) {}
    fun onNavigationEventReceived(event: UIEventType.Navigate, extras: SparseArray<Any?>? = null) {}
    fun onShowToastEventReceived(event: UIEventType.Toast, extras: SparseArray<Any?>? = null) {}
}
