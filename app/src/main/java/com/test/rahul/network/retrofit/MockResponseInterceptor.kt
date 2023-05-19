package com.test.rahul.network.retrofit

import android.content.Context
import com.test.rahul.network.meta.Mock
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Invocation
import java.net.HttpURLConnection
import javax.inject.Inject

/**
 * Mock response interceptor class to mock dummy/hardcoded json response for testing purpose.
 * The class is useful when api isn't available for any reason and can be used for
 * testing mobile side implementation without the remote source.
 * This should only be used for testing and has required file placed in assets. Sample usage for
 * mocking response can be seen below
 *
 * @Mock(filename = "grades.json")
 * fun getGrades(): Call<List<Grade>>
 *
 * where grades.json is has copy of api response and placed in assets folder.
 */
class MockResponseInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val invocation: Invocation? = request.tag(Invocation::class.java)
        val mock: Mock? = invocation?.method()?.getAnnotation(Mock::class.java)

        return if (mock != null && shouldInterceptRequest(request)) {
            val json = readJSONFromFile(filename = mock.filename)
            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_2)
                .code(HttpURLConnection.HTTP_OK)
                .message("")
                .body(json.toResponseBody())
                .build()
        } else chain.proceed(request)
    }

    private fun shouldInterceptRequest(request: Request): Boolean {
        return true
    }

    private fun readJSONFromFile(filename: String): String {
        return context.assets.open(filename).bufferedReader().use { it.readText() }.trim()
    }
}
