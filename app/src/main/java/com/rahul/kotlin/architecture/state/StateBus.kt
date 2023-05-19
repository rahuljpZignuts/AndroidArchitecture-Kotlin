package com.rahul.kotlin.architecture.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.rahul.kotlin.architecture.application.Config
import com.rahul.kotlin.architecture.network.NetworkExecutor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * StateBus holds current states for various actions with an option to observe them in the desired
 * pattern by using [Flow] or [LiveData]. StateBus is only responsible for holding data and is
 * driven by several other classes e.g. [ActivityStateDriver], [NetworkExecutor].
 *
 * Following states are currently stored in the StateBus:
 *
 * [NetworkState]: Network connectivity state to help identify connectivity issues.
 * [UserState]: User state make sure they can only see authorized components at any time.
 */
object StateBus {
    /**
     * Network state emitter and observable that hold last known value for network connectivity.
     */
    private val networkStateEmitter: MutableStateFlow<NetworkState> = MutableStateFlow(
        NetworkStateConnected(true, NetworkState.Connectivity.UNKNOWN)
    )
    val networkStateFlow: Flow<NetworkState> = networkStateEmitter
    val networkState: NetworkState get() = networkStateEmitter.value


    /**
     * User state emitter and observable that hold last known value for user authentication.
     */
    private val userStateFlow = MutableStateFlow(UserState.UNIDENTIFIED)
    val userStateObservable: LiveData<UserState> = userStateFlow.asLiveData()
    val userState: UserState get() = userStateFlow.value
    val isUserAuthorized: Boolean get() = userState == UserState.AUTHORIZED

    /**
     * Language emitter and observable that hold the last known for application language.
     */
    private val languageFlow = MutableStateFlow(
        Language.getLanguage(Config.defaultLanguage.code)
    )
    val languageObservable: LiveData<Language> = languageFlow.asLiveData()
    val language get() = languageFlow.value
    val isLanguageRTL get() = language == Language.ARABIC

    /**
     * Device Type emitter and observable that hold last known value for Device Type.
     */
    private val deviceTypeFlow = MutableStateFlow(DeviceType.MOBILE)
    val deviceTypeObservable: LiveData<DeviceType> = deviceTypeFlow.asLiveData()
    val deviceType: DeviceType get() = deviceTypeFlow.value
    val isTvDevice: Boolean get() = deviceType == DeviceType.TV

    /**
     * Mark connectivity as active whenever network call is succeeded as it guarantees the
     * availability of Internet. However the type of network the device is connected to is not
     * updated and is used from the cached value.
     */
    fun markConnectivityAsActive() {
        val state = networkStateEmitter.value
        if (state.isAvailable) return
        val connectivity = if (state.connectivity == NetworkState.Connectivity.NONE) null
        else state.connectivity
        networkStateEmitter.value = NetworkStateConnected(
            isAvailable = true,
            connectivity = connectivity ?: NetworkState.Connectivity.UNKNOWN,
        )
    }

    /**
     * Mark connectivity as inactive whenever api call is failed because of unavailability of the
     * Internet. If the cached value holds type of the network the device was connected, it is only
     * marked as unavailable with same connectivity; else the state is marked as unidentified.
     */
    fun markConnectivityAsInactive() {
        val state = networkStateEmitter.value
        networkStateEmitter.value =
            if (state.isAvailable) NetworkStateConnected(isAvailable = false, state.connectivity)
            else NetworkStateNotConnected
    }

    /**
     * Called whenever network state change is detected. The updated value is not emitted if the
     * connectivity is same as cached value. If the device is not connected to any network, it is
     * marked as not connected; else it is marked as connected but with unavailability of the
     * Internet as it can only be guaranteed once some network call is executed successfully.
     *
     * @param connectivity type of the network the device is currently connected to.
     */
    fun onNetworkStateChangeDetected(connectivity: NetworkState.Connectivity) {
        val state = networkStateEmitter.value
        if (connectivity == state.connectivity) return
        networkStateEmitter.value = when (connectivity) {
            NetworkState.Connectivity.NONE -> NetworkStateNotConnected
            else -> NetworkStateConnected(state.isAvailable, connectivity)
        }
    }

    /**
     * Called whenever user authentication state has been changed.
     *
     * @param state updated authentication state of the user.
     */
    fun onUserStateChanged(state: UserState) {
        userStateFlow.value = state
    }

    /**
     * Called whenever app language has been changed.
     *
     * @param state updated language state of the app.
     */
    fun onLanguageChanged(state: Language) {
        languageFlow.value = state
    }

    /**
     * Called whenever Device Type has been changed.
     *
     * @param deviceType updated device type of the app.
     */
    fun setDeviceType(deviceType: DeviceType) {
        deviceTypeFlow.value = deviceType
    }
}
