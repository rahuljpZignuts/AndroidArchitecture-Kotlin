package com.test.rahul.injection

import javax.inject.Qualifier

/**
 * Application coroutine scope to perform operations linked with application scope e.g. observing
 * user and network states.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class ApplicationScope
