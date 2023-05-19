package com.rahul.kotlin.architecture.network

import android.net.Uri
import com.rahul.kotlin.architecture.application.Config
import com.rahul.kotlin.architecture.data.enums.RequestState
import com.rahul.kotlin.architecture.network.meta.RequestType
import com.rahul.kotlin.architecture.network.meta.ResponseType
import com.rahul.kotlin.architecture.network.meta.ResponseWrapper
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber
import java.net.ConnectException
import java.net.UnknownHostException

/**
 * Utility class to hold all common operations for network layer
 */
object NetworkUtils {
    /**
     * Checks if the request was successful or not using HTTP status codes
     *
     * @param code http status code received in network response
     */
    @Suppress("MagicNumber")
    fun isRequestSuccessful(code: Int): Boolean = code in 200..299

    /**
     * Checks if the exception was due to Internet connectivity.
     *
     * @param exception raised when making the api call.
     * @return true if it was raised because network was unavailable; false otherwise.
     */
    fun isConnectivityError(exception: Exception?): Boolean {
        // Hacky workaround to check internet connectivity
        return exception is ConnectException || exception is UnknownHostException
    }

    /**
     * Identifies the final request state from provided response.
     *
     * @param T generic type of the response expected for this network call
     * @param wrapper response mete wrapped in an object
     * @param dataValidator lambda function to validate data and help differentiate empty requests
     * for different [RequestType]. Returns true if data is valid; false if it is considered to be
     * [RequestState.EMPTY]
     * @return final request state identified after validating request codes and response data.
     */
    inline fun <T> getRequestState(
        wrapper: ResponseWrapper<T>,
        crossinline dataValidator: () -> Boolean,
    ): RequestState = when {
        isRequestSuccessful(wrapper.response.code()) ->
            if (dataValidator()) RequestState.SUCCESS
            else RequestState.EMPTY
        wrapper.exception == null -> RequestState.INVALID
        isConnectivityError(wrapper.exception) -> RequestState.NETWORK_ERROR
        else -> RequestState.FAILURE
    }

    /**
     * Attempts to extract error message from api responses and fails gracefully if unable to do so.
     *
     * @return extracted text message; null if no message was received or was unable to parse it.
     */
    @Suppress("TooGenericExceptionCaught")
    fun optResponseMessage(response: Response<*>, errorBody: String?): String? {
        if (response.isSuccessful) return response.message()
        var message: String?
        try {
            val errors = JSONObject(errorBody ?: "{}")
            message = errors.optString("error_description")
            if (message.isNullOrBlank()) message = errors.optString("message")
            if (message.isNullOrBlank()) {
                val errorMessage: String? = errors.optMessageFromArray
                if (!errorMessage.isNullOrBlank()) message = errorMessage
            }
            if (message.isNullOrBlank()) {
                val errorMessage = errors.optJSONObject("errors")?.optMessageFromArray
                message = if (errorMessage.isNullOrBlank()) null else errorMessage
            }
        } catch (ex: Exception) {
            Timber.e(ex, "Unable to parse error response")
            message = null
        }
        return message
    }

    /**
     * Attempts to extract provided headerKey value from api responses.
     *
     * @return extracted header value; null if header doesn't exist or was unable to parse it.
     */
    fun optResponseHeader(response: ResponseType, headerKey: String): String? {
        return response.headers[headerKey]
    }

    /**
     * Attempts to extract error type from api responses and fails gracefully if unable to do so.
     *
     * @return extracted error code; null if no error was received or was unable to parse it.
     */
    fun optResponseError(errorBody: String?): String? = optResponseValue(errorBody, "error")

    @Suppress("TooGenericExceptionCaught")
    fun optResponseValue(errorBody: String?, key: String): String? = try {
        val response = JSONObject(errorBody ?: "{}")
        response.optString(key)
    } catch (ex: Exception) {
        Timber.e(ex, "Unable to parse error body")
        null
    }

    fun getEmbeddedUrl(url: String): String {
        return Uri.parse(Config.baseUrl)
            .buildUpon()
            .encodedPath(url)
            .build().toString()
    }
}

/**
 * Generic parser to extract string received from standard django framework.
 */
@Suppress("TooGenericExceptionCaught")
private val JSONObject.optMessageFromArray: String?
    get() = try {
        keys()
            .asSequence()
            .map { optJSONArray(it)?.join("\n") }
            .filterNotNull()
            .joinToString(separator = "\n") { it.trim('"') }
    } catch (ex: Exception) {
        Timber.e(ex, "Unable to parse error body")
        null
    }
