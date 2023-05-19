package com.rahul.kotlin.architecture.network

import com.rahul.kotlin.architecture.data.source.AccountService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

/**
 * Web service module to provide web service executors required by remote sources.
 */
@InstallIn(SingletonComponent::class)
@Module
object WebServiceModule {

    @Provides
    fun provideAccountService(
        retrofit: Retrofit
    ): NetworkExecutor<AccountService> = NetworkExecutor(
        retrofit.create(AccountService::class.java)
    )
}
