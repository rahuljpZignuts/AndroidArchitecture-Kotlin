package com.test.rahul.extension

import com.test.rahul.R
import com.test.rahul.core.component.BaseViewModel
import com.test.rahul.lifecycle.bo.AndroidStringResource
import com.test.rahul.lifecycle.observable.UIEventType
import com.test.rahul.lifecycle.observable.create
import com.test.rahul.network.meta.Resource

/**
 * Dispatches error as alert which is considered to be the general behaviour of displaying
 * error message to user throughout the app. UIEvent is dispatched on main thread immediately
 * if [immediate] is true assuming that the caller is sure and wants to emit this to main thread,
 * posts the value otherwise.
 *
 * @param error resource to refer for user readable message.
 * @param immediate true if called from main thread, false otherwise; default true.
 */
fun BaseViewModel.dispatchError(
    error: Resource.Error<*>, immediate: Boolean = true,
) = dispatch(
    UIEventType.Alert(
        title = AndroidStringResource(R.string.error),
        message = error.exception.description,
        positiveButtonText = AndroidStringResource(resId = R.string.ok),
    ).create(), immediate
)
