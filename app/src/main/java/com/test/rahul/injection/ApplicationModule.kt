package com.test.rahul.injection

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import javax.inject.Singleton

/**
 * Application module to provide application wide components e.g. dispatchers.
 */
@InstallIn(SingletonComponent::class)
@Module
object ApplicationModule {
    @DelicateCoroutinesApi
    @ApplicationScope
    @Provides
    fun provideApplicationScope(): CoroutineScope = GlobalScope

    @DataSourceDispatcher
    @Provides
    fun provideDataSourceDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @LocalSourceDispatcher
    @Provides
    fun provideLocalSourceDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = with(Moshi.Builder()) {
        build()
    }

    @Provides
    @RepositoryDispatcher
    fun provideRepositoryDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @WorkerOperationsDispatcher
    fun provideWorkerOperationsDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
