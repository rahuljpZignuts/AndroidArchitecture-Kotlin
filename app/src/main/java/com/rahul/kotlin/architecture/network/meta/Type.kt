package com.rahul.kotlin.architecture.network.meta

import com.rahul.kotlin.architecture.data.enums.RequestState
import okhttp3.Headers

/**
 * Request type class to identify response expectations from the request. Every sub-class
 * inheriting [RequestType] should have a corresponding [ResponseType] to map the response.
 */
sealed class RequestType

/**
 * Response type class to classify different responses by providing boiler plate code. Every
 * sub-class inheriting [ResponseType] is mapped with corresponding [RequestType] to ease the
 * mapping.
 *
 * @property requestState final state of the request.
 * @property code response code of server response.
 * @property message readable message received from server.
 * @property errorBody string error message received from server.
 */
sealed class ResponseType(
    open val requestState: RequestState,
    open val code: Int,
    open val message: String?,
    open val errorBody: String?,
    open val headers: Headers,
)

/**
 * Empty request to indicate requests that rely completely on status codes and do not expect any
 * data in response. e.g toggle switches, etc.
 */
object EmptyRequest : RequestType()

/**
 * Response mapped for [EmptyRequest] that do not expect any additional data to be returned in
 * response.
 */
data class EmptyResponse(
    override val requestState: RequestState,
    override val code: Int,
    override val message: String?,
    override val errorBody: String?,
    override val headers: Headers,
) : ResponseType(requestState, code, message, errorBody, headers)

/**
 * Data request to indicate requests made to fetch some data e.g. fetch user profile, settings, etc.
 */
object DataRequest : RequestType()

/**
 * Response mapped for [DataRequest] that expect specified data type to be returned in
 * response.
 *
 * @param T generic class type for expected response.
 * @property data object holding the desired response.
 */
data class DataResponse<T>(
    override val requestState: RequestState,
    override val code: Int,
    override val message: String?,
    override val errorBody: String?,
    override val headers: Headers,
    val data: T?,
) : ResponseType(requestState, code, message, errorBody, headers)

/**
 * List request are very similar to [DataRequest] except that they should be used for requests
 * with bulk data to help differentiating the [RequestState] more easily. e.g. fetch courses list,
 * lessons list, etc. The [RequestState.EMPTY] for such states would be slightly different from
 * [DataRequest].
 */
object ListRequest : RequestType()

/**
 * Response mapped for [ListRequest] that expect collection of specified data type to be returned
 * in response.
 *
 * @param T generic class type of bulk response data.
 * @property data collection containing data of the specified model.
 */
data class ListResponse<T : Collection<*>>(
    override val requestState: RequestState,
    override val code: Int,
    override val message: String?,
    override val errorBody: String?,
    override val headers: Headers,
    val data: T?,
) : ResponseType(requestState, code, message, errorBody, headers)
