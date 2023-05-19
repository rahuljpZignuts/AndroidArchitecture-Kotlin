package com.test.rahul.state

/**
 * Network state class to hold detailed information about network connectivity.
 *
 * @property isAvailable flag to indicate if Internet is available on this network or not.
 * @property connectivity type of network the device is connected to.
 */
sealed class NetworkState(
    open val isAvailable: Boolean,
    open val connectivity: Connectivity,
) {
    /**
     * Flag to indicated if the device is connected to any network or not.
     */
    val isConnected: Boolean get() = connectivity != Connectivity.NONE

    /**
     * Connectivity class holds all possible types of that device may be able to connect and is
     * required by the application.
     */
    enum class Connectivity {
        /**
         * None connectivity indicates that the device is not connected to any network.
         */
        NONE,

        /**
         * Metered connectivity indicates that the device is connected to the network that is
         * marked with limited data plan. Usually the mobile data or limited wifi networks. This
         * can be used to support data saving options.
         */
        METERED,

        /**
         * Unlimited connectivity indicates that the device is connected to the network that has no
         * data limitations. Usually wifi networks using broadband Internet connections.
         */
        UNLIMITED,

        /**
         * Unknown connectivity indicates that the device is connected to a network but cannot
         * be identified as [METERED] or [UNLIMITED] plan.
         */
        UNKNOWN;
    }

    /**
     * Override equals operator to simplify comparison and assure that only unique values are
     * emitted from flows.
     */
    override fun equals(other: Any?): Boolean {
        return this === other || (other is NetworkState
                && isAvailable == other.isAvailable
                && connectivity == other.connectivity)
    }

    /**
     * Override hash code for equality using only constructor parameters.
     */
    override fun hashCode(): Int {
        var result = isAvailable.hashCode()
        result = 31 * result + connectivity.hashCode()
        return result
    }
}

/**
 * Child class of [NetworkState] to be used only when connectivity is identified and the device
 * is connected to any network type that can provide the Internet.
 */
data class NetworkStateConnected(
    override val isAvailable: Boolean,
    override val connectivity: Connectivity,
) : NetworkState(isAvailable, connectivity)

/**
 * Child class of [NetworkState] to be used only when connectivity is not identified or the device
 * is connected to any network type that cannot provide the Internet.
 */
object NetworkStateNotConnected : NetworkState(false, Connectivity.NONE)
