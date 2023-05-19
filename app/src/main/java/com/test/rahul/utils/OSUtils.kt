package com.test.rahul.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.test.rahul.state.NetworkState

/**
 * Utility class to manage handy operations dependent on Android OS.
 */
object OSUtils {
    /**
     * Identifies the type of network to which the device is currently connected to.
     *
     * @param context reference to application context.
     * @return type of the network device is currently connected to.
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun identifyNetworkState(context: Context): NetworkState.Connectivity {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            identifyNetworkState(connectivityManager)
        } else {
            identifyNetworkStateLegacy(connectivityManager)
        }
    }

    /**
     * Identifies the network state for devices running Android 6 and greater.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun identifyNetworkState(
        connectivityManager: ConnectivityManager,
    ): NetworkState.Connectivity {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return when {
            capabilities == null -> NetworkState.Connectivity.NONE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkState.Connectivity.UNLIMITED
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkState.Connectivity.METERED
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> NetworkState.Connectivity.UNKNOWN
            else -> NetworkState.Connectivity.NONE
        }
    }

    /**
     * Identifies the network state using deprecated api for devices with OS releases before
     * Android 6.
     */
    @Suppress("DEPRECATION")
    private fun identifyNetworkStateLegacy(
        connectivityManager: ConnectivityManager,
    ): NetworkState.Connectivity {
        return if (connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true)
            NetworkState.Connectivity.UNKNOWN
        else NetworkState.Connectivity.NONE
    }
}
