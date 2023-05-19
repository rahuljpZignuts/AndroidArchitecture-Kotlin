package com.test.rahul.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.test.rahul.data.enums.RequestState
import com.test.rahul.data.enums.isLoading
import com.test.rahul.lifecycle.observable.UIEvent
import com.test.rahul.lifecycle.observer.PredicateEventObserver
import com.test.rahul.lifecycle.observer.SingleEventObserver
import com.test.rahul.lifecycle.observer.UIEventListener
import com.test.rahul.lifecycle.observer.UIEventObserver

fun <Owner> LiveData<UIEvent>.observe(owner: Owner) where Owner : LifecycleOwner, Owner : UIEventListener {
    observe(owner, UIEventObserver(owner))
}

fun <Owner> LiveData<UIEvent>.observe(owner: Owner) where Owner : Fragment, Owner : UIEventListener {
    observe(owner.viewLifecycleOwner, UIEventObserver(owner))
}

fun <Owner> LiveData<UIEvent>.observeActivity(owner: Owner) where Owner : Fragment {
    observe(owner.viewLifecycleOwner, UIEventObserver(owner.requireActivity() as UIEventListener))
}

fun <T> LiveData<T>.observeOnce(observer: Observer<T>, owner: LifecycleOwner? = null) {
    val listener = object : SingleEventObserver<T>(source = this) {
        override fun onChangeReceived(data: T?) {
            observer.onChanged(data)
        }
    }
    if (owner == null) {
        this.observeForever(listener)
    } else {
        this.observe(owner, listener)
    }
}

fun <T> LiveData<T>.observeOnly(
    observer: Observer<T>,
    owner: LifecycleOwner? = null,
    predicate: (T?) -> Boolean,
) {
    val listener = object : PredicateEventObserver<T>(source = this, predicate = predicate) {
        override fun onChangeReceived(data: T?) {
            observer.onChanged(data)
        }
    }
    if (owner == null) {
        this.observeForever(listener)
    } else {
        this.observe(owner, listener)
    }
}

fun LiveData<Boolean>.transformToRequestState() = Transformations.map(this) {
    return@map if (it == true) RequestState.IN_PROGRESS else RequestState.SUCCESS
}

fun LiveData<RequestState>.transformFromRequestState() = Transformations.map(this) {
    return@map it.isLoading()
}
