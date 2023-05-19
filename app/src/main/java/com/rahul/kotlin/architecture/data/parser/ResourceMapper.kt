package com.rahul.kotlin.architecture.data.parser

/**
 * Resource parser generally used to map network responses to application data classes. This
 * provides an abstraction from the network layer by keeping data models independent of
 * network responses. Any change in network response should not affect data models and may only
 * require updating the parsing in its sub-classes. Network response classes should not be used
 * behind its sub-classes implementations.
 *
 * @param Input generic type of network response class
 * @param Output generic type of data model class
 */
interface ResourceMapper<Input, Output> {
    /**
     * Parses the response from Input to Output class, the implementation can use merging
     * technique with the old value so that multiple sources can be merged into one class.
     *
     * @param response object received from server.
     * @param cachedValue data stored locally, can be used to merge with new data or comparison.
     * @return parsed response in the desired format.
     */
    fun map(response: Input?, cachedValue: Output? = null): Output?
}
