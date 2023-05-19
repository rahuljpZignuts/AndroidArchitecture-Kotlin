package com.rahul.kotlin.architecture.lifecycle.observable

import android.util.SparseArray
import android.widget.Toast.LENGTH_SHORT
import com.rahul.kotlin.architecture.R
import com.rahul.kotlin.architecture.core.component.BaseViewModel
import com.rahul.kotlin.architecture.lifecycle.bo.AndroidStringResource
import com.rahul.kotlin.architecture.lifecycle.bo.StringResource

/**
 * Sealed class to expose common UI events throughout the app with additional resources included.
 */
sealed class UIEventType {
    data class Navigate(val screen: Int) : UIEventType()

    data class Alert(
        val title: StringResource? = null,
        val message: StringResource? = null,
        val positiveButtonText: StringResource? = AndroidStringResource(resId = R.string.ok),
        val negativeButtonText: StringResource? = null,
        val neutralButtonText: StringResource? = null,
        val cancelable: Boolean = true,
    ) : UIEventType()

    data class Toast(
        val message: StringResource? = null,
        val duration: Int = LENGTH_SHORT,
    ) : UIEventType()
}

/**
 * Creates UIEvent from its type and additional arguments.
 *
 * @param extras optional additional arguments required to be dispatched with event.
 * @return UIEvent wrapped over provided type.
 */
fun UIEventType.create(vararg extras: Pair<Int, Any?>): UIEvent {
    val array = SparseArray<Any?>(extras.size)
    for ((key, value) in extras) array.put(key, value)
    return UIEvent(type = this, extras = array)
}

/**
 * Creates UIEvent from its type and dispatches it using the provided ViewModel using its dispatch
 * method.
 *
 * @param viewModel to dispatch event to.
 * @param immediate true if called from main thread, false otherwise; default true.
 * @param extras optional additional arguments required to be dispatched with event.
 */
fun UIEventType.dispatch(
    viewModel: BaseViewModel,
    immediate: Boolean = true,
    vararg extras: Pair<Int, Any?>,
) = viewModel.dispatch(create(*extras), immediate)

/**
 * Wrapper class for UI events exposed via observables to assure they are observed only once.
 *
 * @property extras optional additional arguments required to be dispatched with event.
 */
class UIEvent internal constructor(
    type: UIEventType, val extras: SparseArray<Any?>? = null,
) : Event<UIEventType>(type)

@Suppress("UNCHECKED_CAST")
fun <T> UIEvent.getExtra(key: Int): T? = extras?.get(key) as? T
