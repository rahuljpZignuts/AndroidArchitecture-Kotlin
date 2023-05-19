package com.test.rahul.injection

import javax.inject.Qualifier

/**
 * Data source coroutine dispatcher to dispatch data fetch and update operations like network
 * calls and database transactions.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class LocalSourceDispatcher
