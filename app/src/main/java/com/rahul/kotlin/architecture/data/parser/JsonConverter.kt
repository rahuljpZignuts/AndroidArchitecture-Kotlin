package com.rahul.kotlin.architecture.data.parser

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Json converter class to parse from/to raw json data to/from data models. The adapter currently
 * uses [Moshi] for parsing data and is responsible for adding abstraction layer from the caller
 * classes so that any update in the parsing should not affect the caller classes.
 */
@Singleton
class JsonConverter @Inject constructor(private val moshi: Moshi) {
    private val adapterMap: HashMap<Type, JsonAdapter<*>> = HashMap()

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getAdapter(type: Type): JsonAdapter<T> = adapterMap.getOrPut(type) {
        return@getOrPut moshi.adapter<T>(type).nullSafe()
    } as JsonAdapter<T>

    /**
     * Parses raw json to desired model.
     *
     * @param T generic type of model class
     * @param json raw json
     * @param typeOfT class of data model
     * @return parsed data model if the parsing is successful; null otherwise
     */
    fun <T : Any> fromJson(json: String, typeOfT: Type): T = getAdapter<T>(
        type = typeOfT
    ).fromJson(json) ?: throw RuntimeException("Type $typeOfT cannot be created from $json")

    /**
     * Parses data model to raw json.
     *
     * @param T generic type of model class
     * @param source data model object to parse
     * @return parsed raw json if the parsing is successful; empty json otherwise
     */
    fun <T : Any> toJson(source: T): String = getAdapter<T>(
        type = source::class.java
    ).toJson(source)
}
