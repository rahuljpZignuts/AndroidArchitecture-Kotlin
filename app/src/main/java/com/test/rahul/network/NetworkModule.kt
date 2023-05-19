package com.test.rahul.network

import android.content.Context
import com.squareup.moshi.Moshi
import com.test.rahul.application.Config
import com.test.rahul.network.retrofit.FileLogger
import com.test.rahul.network.retrofit.MockResponseInterceptor
import com.test.rahul.network.retrofit.RequestHeadersInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Network module to provide network layer components e.g. dispatchers, interceptors, request
 * executors, etc.
 */
@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    private annotation class PrintLogsInterceptor

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    private annotation class RecordLogsInterceptor

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    private annotation class WebServicesBaseURL

    @Provides
    @WebServicesBaseURL
    fun provideBaseURL(): String = Config.baseUrl

    @Provides
    @Singleton
    fun provideConverterFactory(moshi: Moshi): Converter.Factory {
        return MoshiConverterFactory.create(moshi)
    }

    @PrintLogsInterceptor
    @Provides
    @Singleton
    fun providePrintLogsInterceptor(): Interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @RecordLogsInterceptor
    @Provides
    @Singleton
    fun provideRecordLogsInterceptor(
        @ApplicationContext context: Context,
    ): Interceptor = HttpLoggingInterceptor(
        FileLogger(context = context)
    ).apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder = getInsecureSSLClient()

    @Provides
    @Singleton
    fun provideRetrofit(
        @WebServicesBaseURL baseURL: String,
        converterFactory: Converter.Factory,
        okHttpClientBuilder: OkHttpClient.Builder,
        headersInterceptor: RequestHeadersInterceptor,
        mockResponseInterceptor: MockResponseInterceptor,
        @PrintLogsInterceptor printLogsInterceptor: Interceptor,
        @RecordLogsInterceptor recordLogsInterceptor: Interceptor,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(converterFactory)
        .client(
            with(okHttpClientBuilder) {
                addInterceptor(headersInterceptor)
                if (Config.environment.debugLogsEnabled) {
                    addInterceptor(printLogsInterceptor)
                    addInterceptor(recordLogsInterceptor)
                    if (Config.environment == Config.Environment.DEBUG) {
                        addInterceptor(mockResponseInterceptor)
                    }
                }
                build()
            }
        ).build()

    private fun getInsecureSSLClient(): OkHttpClient.Builder {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }
        })
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory = sslContext.socketFactory
        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .readTimeout(NETWORK_READ_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(NETWORK_CONNECT_TIMEOUT, TimeUnit.SECONDS)
    }


    private const val NETWORK_CONNECT_TIMEOUT = 15L
    private const val NETWORK_READ_TIMEOUT = 30L
}
