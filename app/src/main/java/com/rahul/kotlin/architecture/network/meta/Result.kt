package com.rahul.kotlin.architecture.network.meta

data class Result<T>(
    val data: T? = null,
    val error: String? = null,
)
