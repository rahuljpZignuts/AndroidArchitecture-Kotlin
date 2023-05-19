package com.rahul.kotlin.architecture.data.common

data class PagingResource<M>(
    val result: List<M>,
    val prevKey: Int?,
    val nextKey: Int?,
)
