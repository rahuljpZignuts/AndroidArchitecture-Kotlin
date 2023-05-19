package com.test.rahul.injection

import javax.inject.Qualifier

/**
 * Worker operations coroutine dispatcher to dispatch long running operations that are intended
 * not to run on the main thread.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class WorkerOperationsDispatcher
