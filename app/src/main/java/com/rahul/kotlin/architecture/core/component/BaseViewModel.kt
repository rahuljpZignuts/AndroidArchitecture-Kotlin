package com.rahul.kotlin.architecture.core.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rahul.kotlin.architecture.lifecycle.observable.UIEvent

/**
 * Base abstract view-model that should be used as parent class by all view-models in this app.
 * This can be helpful in providing boiler plate code and basic functionality like dispatching
 * [UIEvent]s and making the handling for network requests easier with their loading states.
 *
 */
abstract class BaseViewModel() : ViewModel() {
    // Live data to dispatch and observe UI events
    private val _uiEvent: MutableLiveData<UIEvent> = MutableLiveData()

    // Live data that uses backing property so that events can only be dispatched from ViewModel
    // and can only be observed by other classes
    val uiEvent: LiveData<UIEvent> = _uiEvent

    /**
     * Dispatches UIEvent to its live data on main thread immediately if [immediate] is true
     * assuming that the caller is sure and wants to emit this to main thread, posts the value
     * otherwise.
     *
     * @param event data to be emitted.
     * @param immediate true if called from main thread, false otherwise.
     */
    fun dispatch(event: UIEvent, immediate: Boolean) {
        if (immediate) _uiEvent.value = event
        else _uiEvent.postValue(event)
    }
}
