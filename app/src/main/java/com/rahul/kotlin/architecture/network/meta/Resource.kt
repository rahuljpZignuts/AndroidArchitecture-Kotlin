package com.rahul.kotlin.architecture.network.meta

import com.rahul.kotlin.architecture.data.enums.RequestState

/**
 * Resource class to hold responses with their result status.
 *
 * @param T generic type of result class.
 * @property state object to hold request status originated for this resource.
 * @constructor Create empty Resource
 */
sealed class Resource<out T>(val state: RequestState) {
    /**
     * Success class to indicate successful response.
     *
     * @property data response data object.
     */
    class Success<out T>(
        val data: T, state: RequestState = RequestState.SUCCESS,
    ) : Resource<T>(state)

    /**
     * Error class to indicate failure response.
     *
     * @property exception to help identify the cause of failure.
     */
    class Error<T>(
        val exception: ResourceException, state: RequestState = RequestState.FAILURE,
    ) : Resource<T>(state)

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[state=$state, data=$data]"
            is Error -> "Error[state=$state, exception=$exception]"
        }
    }
}

/**
 * Extension method on [Resource] class to reduce false NullSafeMutableLiveData warnings from
 * IDE by making it easier to access desired value
 */
val <T> Resource<T>.value: T?
    get() = if (this is Resource.Success) data else null

fun Resource<Any>.getState(list: List<Any>?): RequestState {
    return if (list.isNullOrEmpty()) {
        RequestState.EMPTY
    } else {
        this.state
    }
}