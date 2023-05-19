package com.test.rahul.data.source

import com.test.rahul.data.model.Public
import com.test.rahul.network.WebService
import com.test.rahul.network.meta.NoAuth
import retrofit2.Call
import retrofit2.http.GET

/**
 * Account service api interface for all account related api calls
 */
interface AccountService : WebService {

    @GET("/random")
    @NoAuth
    fun login(): Call<Public>

    @GET("/entries")
    fun getEntries() : Call<Public>
}
