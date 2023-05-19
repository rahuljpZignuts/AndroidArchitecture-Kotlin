package com.rahul.kotlin.architecture.network

import androidx.annotation.WorkerThread
import com.rahul.kotlin.architecture.data.enums.RequestState
import com.rahul.kotlin.architecture.network.meta.DataRequest
import com.rahul.kotlin.architecture.network.meta.DataResponse
import com.rahul.kotlin.architecture.network.meta.EmptyRequest
import com.rahul.kotlin.architecture.network.meta.EmptyResponse
import com.rahul.kotlin.architecture.network.meta.ListRequest
import com.rahul.kotlin.architecture.network.meta.ListResponse
import com.rahul.kotlin.architecture.network.meta.RequestType
import com.rahul.kotlin.architecture.network.meta.ResponseType
import com.rahul.kotlin.architecture.network.meta.ResponseWrapper
import com.rahul.kotlin.architecture.state.StateBus
import com.rahul.kotlin.architecture.state.UserState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber
import java.net.HttpURLConnection
import javax.inject.Inject

/**
 * Network executor class that provided boiler plate code for executing network calls. It also
 * takes responsibility for parsing common error responses, observing user authentication state
 * from api response code and keep the network state update after each api call.
 *
 * @param S generic type of service class holding the api requests.
 * @property webService reference to service instance creates by retrofit.
 */
class NetworkExecutor<S : WebService> @Inject constructor(private val webService: S) {
    /**
     * Executes the call request and updates user and network based on api response.
     *
     * @param T generic type of response required from the api.
     * @param call reference to call object.
     * @return response received against the call; if the call fails with an exception, the
     * exception is passed to the caller to know the exact cause of failure.
     */
    @Suppress("TooGenericExceptionCaught")
    @WorkerThread
    private fun <T> execute(call: Call<T>): ResponseWrapper<T> {
        try {
            val response = call.execute()
            when (response.code()) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> if (StateBus.userState == UserState.AUTHORIZED) {
                    StateBus.onUserStateChanged(UserState.SESSION_EXPIRED)
                }
            }
            StateBus.markConnectivityAsActive()
            return ResponseWrapper(response)
        } catch (exception: Exception) {
            Timber.e(exception)
            if (NetworkUtils.isConnectivityError(exception)) StateBus.markConnectivityAsInactive()
            return ResponseWrapper(
                Response.error(
                    HTTP_NETWORK_CONNECT_TIMEOUT_ERROR,
                    "Unexpected response from server: ${exception.message}".toResponseBodyPlainText()
                ),
                exception,
            )
        }
    }

    /**
     * Supports execution for [EmptyRequest] and [DataRequest] and returns the mapped response
     * for respective [RequestType].
     *
     * Please note that [ListRequest] is not supported by this method as they need to specify list
     * type explicitly.
     * @see execute to execute list requests.
     *
     * @param T generic type of response required from the api.
     * @param R generic [ResponseType] of the api.
     * @param request object to identify request type mapped with desired response type.
     * @param responseValidator optional block to validate response for empty states.
     * @param requestBlock call block to generate api call with available parameters.
     * @return response type specified by the caller wrapped with all the required information.
     */
    @Suppress("UNCHECKED_CAST")
    @WorkerThread
    fun <T, R : ResponseType> execute(
        request: RequestType,
        responseValidator: (T?) -> Boolean = { it != null },
        requestBlock: S.() -> Call<T>,
    ): R {
        val wrapper: ResponseWrapper<T> = execute(requestBlock(webService))
        val code: Int = wrapper.response.code()
        val errorBody = wrapper.response.errorBody()?.string()
        val message: String? = NetworkUtils.optResponseMessage(wrapper.response, errorBody)
        val data: T? by lazy { wrapper.response.body() }
        val requestState: RequestState = NetworkUtils.getRequestState(wrapper) {
            responseValidator(data)
        }
        val headers = wrapper.response.headers()
        return when (request) {
            is EmptyRequest -> EmptyResponse(requestState, code, message, errorBody, headers)
            is DataRequest -> DataResponse(requestState, code, message, errorBody, headers, data)
            is ListRequest -> throw RuntimeException("List requests should be specified explicitly")
        } as R
    }

    /**
     * Supports execution for [ListRequest] only. It differs from normal execute block by
     * simplifying specify the list types for the caller.
     *
     * @param T generic list type required from the api.
     * @param requestBlock call block to generate api call with available parameters.
     * @return [ListResponse] with type specified by the caller wrapped with all the required
     * information.
     */
    @WorkerThread
    fun <T : Collection<*>> execute(requestBlock: S.() -> Call<T>): ListResponse<T> {
        val wrapper: ResponseWrapper<T> = execute(requestBlock(webService))
        val errorBody = wrapper.response.errorBody()?.string()
        val data: T? = wrapper.response.body()
        val requestState: RequestState = NetworkUtils.getRequestState(wrapper) {
            !data.isNullOrEmpty()
        }
        val headers = wrapper.response.headers()
        return ListResponse(
            requestState,
            wrapper.response.code(),
            NetworkUtils.optResponseMessage(wrapper.response, errorBody),
            errorBody,
            headers,
            data,
        )
    }

    companion object {
        private const val HTTP_NETWORK_CONNECT_TIMEOUT_ERROR = 599
    }
}


/**
 * Creates [RequestBody] from plain text string.
 */
private fun String.toRequestBodyPlainText(): RequestBody {
    return toRequestBody("text/plain".toMediaTypeOrNull())
}

/**
 * Creates [ResponseBody] from plain text string.
 */
private fun String.toResponseBodyPlainText(): ResponseBody {
    return toResponseBody("text/plain".toMediaTypeOrNull())
}
