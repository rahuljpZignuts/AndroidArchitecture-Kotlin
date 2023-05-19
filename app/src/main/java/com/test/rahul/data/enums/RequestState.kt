package com.test.rahul.data.enums

/**
 * Request state to help identify step by step progress for data operations e.g. form submission,
 * fetching data list, etc. These states are useful in showing better loading and other progress
 * and/or creative messages to users.
 */
enum class RequestState {
    /**
     * Empty state to indicate that the data fetched is empty and/or has no content.
     */
    EMPTY,

    /**
     * Failure state to indicate that the the requested action could not be completed because
     * either server is unavailable or any other unknown error occurred while executing it.
     */
    FAILURE,

    /**
     * Fetching state indicates the data is being fetched silently and UI should not be blocked
     * for the user e.g. fetching next page of list or synchronizing data in background. etc.
     */
    FETCHING,

    /**
     * In progress state indicates that the request progress should be clearly shown to user and
     * may block the UI if needed e.g. submitting forms, fetching initial list data, etc.
     */
    IN_PROGRESS,

    /**
     * Invalid state can be used when data/request submission fails because either data provided
     * is invalid/incomplete or the user is not allowed to complete the desired operation. This
     * may rise when there is inconsistency between app and server and may help indicate states
     * that are synchronized with server.
     */
    INVALID,

    /**
     * Network error state indicates the request is failed because of connectivity issues and we
     * may need to notify the user to check its Internet settings.
     */
    NETWORK_ERROR,

    /**
     * Success, as the name indicates, is used when the request is completed successfully without
     * any error(s).
     */
    SUCCESS;
}

/**
 * Returns true for states that are considered to be loading state, this means the
 * [RequestState] is not the final one and is waiting for some action to complete. All other
 * states may be considered as final state and should not be expected to update automatically
 * unless in result to any other operation.
 */
fun RequestState.isLoading() = (this == RequestState.FETCHING || this == RequestState.IN_PROGRESS)
