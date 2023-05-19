package com.rahul.kotlin.architecture.network.meta

import com.rahul.kotlin.architecture.R
import com.rahul.kotlin.architecture.lifecycle.bo.AndroidStringResource
import com.rahul.kotlin.architecture.lifecycle.bo.StringResource

/**
 * Resource exception class helps identifying the cause that prevents any operation to be
 * completed successfully.
 *
 * @property description human readable message that can be shown to user to explain the issue raised.
 * @property exception stack track containing detailed information of the event that didn't let
 * the action to be completed.
 */
sealed class ResourceException(
    open val description: StringResource, open val exception: Exception? = null,
) : Throwable() {
    /**
     * Exception to indicate that the action could not be completed because of network
     * unavailability.
     */
    object NetworkNotAvailable : ResourceException(
        description = AndroidStringResource(resId = R.string.error_message_connectivity)
    )

    /**
     * [RequiresVerification] indicates the action is permitted to the user but verification
     * is required for further action
     */
    data class RequiresVerification(
        override val description: StringResource = StringResource.create(),
        override val exception: Exception? = null,
    ) : ResourceException(description, exception)

    /**
     * [OperationNotSupported] indicates that the user was either not allowed to do the perform
     * the desired operation or the data provided was invalid.
     */
    data class OperationNotSupported(
        override val description: StringResource = StringResource.create(),
        override val exception: Exception? = null,
    ) : ResourceException(description, exception)

    /**
     * [ResourceNotFound] exception can be used to indicate that the requested data/page/resource
     * being accessed is no longer valid or doesn't exist in the system. This can be because of
     * invalid request but is generally expected to be used for the cases where some resource
     * is expected to be loaded e.g. course loading, profile fetching, etc. while
     * [OperationNotSupported] is normally thrown against performed actions e.g. form submissions,
     * course progress updates, etc.
     */
    data class ResourceNotFound(
        override val description: StringResource = StringResource.create(),
        override val exception: Exception? = null,
    ) : ResourceException(description, exception)

    /**
     * Exception to indicate that the action could not be completed because server is down and/or
     * unavailable.
     */
    object ServerNotAvailable : ResourceException(
        description = AndroidStringResource(resId = R.string.error_message_unexpected_behaviour)
    )

    override fun toString(): String {
        return "description=$description, exception=$exception"
    }
}
