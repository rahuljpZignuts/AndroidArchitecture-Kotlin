package com.test.rahul.network.meta

import com.test.rahul.data.common.PagingResource
import com.test.rahul.data.enums.RequestState
import com.test.rahul.network.NetworkUtils

/**
 * Gets localized error messages mapped for generic exceptions; returns null otherwise.
 */
fun <T> ResourceException.asResource(requestState: RequestState = RequestState.FAILURE): Resource<T> {
    return Resource.Error(exception = this, state = requestState)
}

/**
 * Returns [ResourceException] for generic errors e.g. network failure, server being down or
 * parsing issues to avoid code repetition for such issues. Returns null if the error raised is
 * not amongst the generic exceptions.
 */
fun ResponseType.getGenericException(): ResourceException? = when (requestState) {
    RequestState.NETWORK_ERROR -> ResourceException.NetworkNotAvailable
    RequestState.FAILURE -> ResourceException.ServerNotAvailable
    else -> null
}

/**
 * Returns true if the request is considered to be successful.
 */
val ResponseType.isSuccessful get() = NetworkUtils.isRequestSuccessful(code)

/**
 * Transforms [EmptyResponse] to [Resource] object for success and common error scenarios; null
 * otherwise.
 */
fun EmptyResponse.asResource(): Resource<Unit>? =
    if (isSuccessful) Resource.Success(Unit, requestState)
    else getGenericException()?.asResource(requestState)

/**
 * Transforms [DataResponse] to [Resource] object for success and common error scenarios; null
 * otherwise.
 */
fun <T> DataResponse<T>.asResource(reqState: RequestState? = null): Resource<T>? =
    if (isSuccessful && data != null) Resource.Success(data, reqState ?: requestState)
    else getGenericException()?.asResource(reqState ?: requestState)

/**
 * Transforms [ListResponse] to [Resource] object for success and common error scenarios; null
 * otherwise.
 */
fun <T : Collection<*>> ListResponse<T>.asResource(reqState: RequestState? = null): Resource<T>? =
    if (isSuccessful && data != null) Resource.Success(data, reqState ?: requestState)
    else getGenericException()?.asResource(reqState ?: requestState)

/**
 * Transforms [ListResponse] to [PagingResource] object for success and common error scenarios;
 * null otherwise.
 */
fun <M> ListResponse<List<M>>.asPagingResource(): Resource<PagingResource<M>>? =
    if (isSuccessful && data != null) Resource.Success(PagingResource(
        result = data,
        nextKey = null,
        prevKey = null,
    ), requestState)
    else getGenericException()?.asResource(requestState)

/**
 * Transforms [Resource] from one type to another, helpful for cases where the response to be
 * delivered has different type then the request made.
 *
 * @param T generic type of original response class.
 * @param R generic type of expected response class.
 * @param block code block to map original response to desired response.
 */
fun <T, R> Resource<T>.transform(block: (T) -> R? = { null }): Resource<R> = when (this) {
    is Resource.Success -> Resource.Success(block(data)!!, state)
    is Resource.Error -> Resource.Error(exception, state)
}
