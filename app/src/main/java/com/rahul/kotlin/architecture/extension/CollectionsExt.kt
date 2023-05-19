package com.rahul.kotlin.architecture.extension

fun <T> MutableCollection<T>.clearAndUpdate(data: Collection<T>?) {
    this.clear()
    data?.let { this.addAll(it) }
}
