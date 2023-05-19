package com.rahul.kotlin.architecture.state

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.rahul.kotlin.architecture.injection.ApplicationScope
import com.rahul.kotlin.architecture.injection.WorkerOperationsDispatcher
import com.rahul.kotlin.architecture.os.permission.Permission
import com.rahul.kotlin.architecture.os.permission.hasPermission
import com.rahul.kotlin.architecture.persistance.proto.ProtoDataStore
import com.rahul.kotlin.architecture.utils.OSUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Activity state driver is responsible for driving [StateBus] through actions linked with activity
 * e.g. Whenever Internet connectivity is affected, the driver takes responsibility to identify
 * appropriate state and inform the bus so all observers may get the most ideal network state at
 * earliest.
 * It is also responsible for providing appropriate callbacks whenever needed e.g. when user is
 * logged out forcefully or because of authentication failure.
 * The driver needs to be started first time to invoke one time operations required throughout
 * application lifecycle. Once the driver is started and register to activity lifecycle callback,
 * it takes the driving seat with the responsibility to take lead in the mentioned actions.
 *
 * @property coroutineScope application scope to perform suspending operations linked with
 * application lifecycle.
 * @property dispatcher to be used for common tasks like mapping and filtering which can be done
 * on worker threads for smoother experience.
 * @property protoDataStore data store containing user authentication details and other useful
 * information.
 * @constructor Create empty Activity state driver.
 *
 * @param context application context to communication with Android OS for desired tasks.
 */
@Singleton
class ActivityStateDriver @Inject constructor(
    @ApplicationContext context: Context,
    @ApplicationScope private val coroutineScope: CoroutineScope,
    @WorkerOperationsDispatcher private val dispatcher: CoroutineDispatcher,
    private val protoDataStore: ProtoDataStore,
) : Application.ActivityLifecycleCallbacks {
    /**
     * Network state transformer to filter unused network states and connectivity values.
     *
     * Returns a pair of emitted value to currently identified network state so most optimal
     * state can be identified using the provided information.
     */
    private val networkStateTransformer = { state: NetworkState ->
        val connectivity =
            if (Permission.NETWORK_STATE.hasPermission(
                    context
                )
            ) {
                OSUtils.identifyNetworkState(context)
            } else null
        if (connectivity != null) {
            Pair(state, connectivity)
        } else null
    }


    /**
     * Push start the engine so that all required operations are started that are irrespective
     * of activity/fragment lifecycle and need to observed throughout application lifecycle.
     */
    fun start() {
//        coroutineScope.launch { startAuthenticationEngine() }
        coroutineScope.launch { startNetworkEngine() }
        coroutineScope.launch { startUserEngine() }
        coroutineScope.launch { startSettingsEngine() }
    }

//    /**
//     * Starts authentication engine to observe and maintain user state throughout the application.
//     */
//    private suspend fun startAuthenticationEngine() {
//        protoDataStore.authenticationData.catch { ex ->
//            Timber.e(ex)
//        }.collectLatest { authentication ->
//            // Checks for updated state. Authorized state for logged in user while unauthorized
//            // state depends on previous state. If the user was authorized previously, it means
//            // that they had requested for signing out of the app as force unauthorized states
//            // should be handled by network classes. Else, if the user state was unidentified,
//            // set a proper state for better experience.
//            StateBus.onUserStateChanged(
//                when {
//                    authentication.isAuthenticated() -> UserState.AUTHORIZED
//                    else -> when (StateBus.userState) {
//                        UserState.AUTHORIZED -> UserState.SESSION_INVALIDATED
//                        UserState.UNIDENTIFIED -> UserState.UNAUTHORIZED
//                        else -> return@collectLatest
//                    }
//                }
//            )
//        }
//    }

    /**
     * Starts network engine to observe and maintain network state throughout the application.
     */
    private suspend fun startNetworkEngine() {
        StateBus.networkStateFlow.mapNotNull {
            networkStateTransformer(it)
        }.flowOn(dispatcher).catch { ex ->
            Timber.e(ex)
        }.collectLatest(::onNetworkStateChanged)
    }

    /**
     * Starts user engine to observe and maintain student grade throughout the application
     */
    private suspend fun startUserEngine() {
        protoDataStore.authUserData.catch { ex ->
            Timber.e(ex)
        }.collectLatest { userInfo ->

        }
    }

    /**
     * Starts settings engine to observe and maintain locale and theme throughout the application.
     */
    private suspend fun startSettingsEngine() {

    }

    /**
     * Called whenever network state chang is detected to notify [StateBus] about it.
     *
     * @param meta pair of current network state to identified network state.
     */
    private fun onNetworkStateChanged(meta: Pair<NetworkState, NetworkState.Connectivity>) {
        StateBus.onNetworkStateChangeDetected(meta.second)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // Check network connectivity on every activity creation. It can be moved to network
        // state listener or other relevant method seems to be exhaustive and/or unnecessary here
        networkStateTransformer(StateBus.networkState)?.let {
            StateBus.onNetworkStateChangeDetected(it.second)
        }
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}
