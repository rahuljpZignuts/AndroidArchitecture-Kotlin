package com.test.rahul.extension

import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

fun EditText.addFilters(vararg inputFilters: InputFilter) {
    filters = arrayOf(
        *(filters ?: emptyArray()),
        *inputFilters
    )
}

fun EditText.setMaxLength(maxLength: Int) {
    addFilters(LengthFilter(maxLength))
}

@ExperimentalCoroutinesApi
fun EditText.textChanges(debounceTime: Long = 1000L) =
    callbackFlow {
        doOnTextChanged { text, _, _, _ -> trySend(text.toString()) }
        awaitClose { }
    }
        .debounce(debounceTime)
        .map {
            it.trim().ifBlank { null }
        }
        .distinctUntilChanged()
        .asLiveData()
