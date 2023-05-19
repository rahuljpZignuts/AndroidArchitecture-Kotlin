package com.rahul.kotlin.architecture.network.meta

/**
 * No authentication annotation to indicate requests that do not require to be intercepted with
 * authentication mechanism e.g. login, registration and public api calls.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class NoAuth

/**
 * Mocks api response in debug builds for testing purpose
 *
 * @property filename name of file that contains response in desired format
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class Mock(
    val filename: String,
)
