package com.test.rahul.network.meta

import retrofit2.Response

/**
 * Wrapper class to wrap network response with optional exception in case of failed requests.
 *
 * @param T generic type of response class.
 * @property response object containing network response and its meta
 * @property exception cause of request failure (if any)
 */
data class ResponseWrapper<T>(
    val response: Response<T>,
    val exception: Exception? = null,
)
