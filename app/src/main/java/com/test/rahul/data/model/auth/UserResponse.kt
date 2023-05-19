package com.test.rahul.data.model.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponse(
    @Json(name = "first_name")
    val firstName: String,
    @Json(name = "last_name")
    val lastName: String,
)

@JsonClass(generateAdapter = true)
data class UserAuthResponse(
    @Json(name = "access_token")
    val accessToken: String?,
)
