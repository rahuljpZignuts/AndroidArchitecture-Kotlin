package com.rahul.kotlin.architecture.network

import retrofit2.Retrofit

/**
 * Web service class to let [Retrofit] turn it into a java interface. This empty interface let
 * us binding generic types easier.
 *
 * @see [NetworkExecutor] for more details and its usage
 */
interface WebService
