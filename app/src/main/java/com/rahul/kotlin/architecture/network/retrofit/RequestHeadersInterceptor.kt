package com.rahul.kotlin.architecture.network.retrofit

import com.rahul.kotlin.architecture.data.proto.Authentication
import com.rahul.kotlin.architecture.network.Constants
import com.rahul.kotlin.architecture.network.Constants.BEARER_TOKEN_FORMAT
import com.rahul.kotlin.architecture.network.meta.NoAuth
import com.rahul.kotlin.architecture.persistance.proto.ProtoDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttp
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [OkHttp] interceptor to simplify adding headers in every request.
 * The interceptor intercepts every request and adds:
 *      Bearer token to every request that is not annotated with [NoAuth]
 *      Response language to every request that is annotated with [Localize]
 *      User Agent to every request that is annotated with [UserAgent]
 */
@Singleton
class RequestHeadersInterceptor @Inject constructor(
    private val dataStore: ProtoDataStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val invocation: Invocation? = request.tag(Invocation::class.java)
        val noAuth: NoAuth? = invocation?.method()?.getAnnotation(NoAuth::class.java)
        val requestBuilder = request.newBuilder()
        val authentication: Authentication? by lazy { runBlocking { dataStore.authenticationData.firstOrNull() } }
        if (noAuth == null && authentication != null) {
            authentication?.accessToken?.let {
                addAuthorization(requestBuilder, it)
            }
        }
        return chain.proceed(requestBuilder.build())
    }

    private fun addAuthorization(requestBuilder: Request.Builder, accessToken: String) {
        if (accessToken.isNotBlank()) {
            requestBuilder.removeHeader(Constants.HEADER_KEY_AUTHORIZATION)
            requestBuilder.addHeader(
                Constants.HEADER_KEY_AUTHORIZATION,
                BEARER_TOKEN_FORMAT.format(Locale.ENGLISH, accessToken)
            )
        }
    }
}
