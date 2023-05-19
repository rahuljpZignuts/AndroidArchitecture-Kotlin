package com.rahul.kotlin.architecture.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Public(
    @Json(name = "count")
    val count: Int,
    val entries: List<Entry>
)

@JsonClass(generateAdapter = true)
data class Entry(
    @Json(name = "API")
    val api: String
)
