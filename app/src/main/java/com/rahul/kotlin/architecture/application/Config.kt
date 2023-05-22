package com.rahul.kotlin.architecture.application

import com.rahul.kotlin.architecture.BuildConfig
import com.rahul.kotlin.architecture.extension.enumValueByName
import com.rahul.kotlin.architecture.state.Language

/**
 * Wrapper class for configurations to create abstraction from [BuildConfig] fields. No other class
 * should use any of the fields directly from [BuildConfig].
 */
object Config {
    const val versionCode: Int = BuildConfig.VERSION_CODE
    const val versionName: String = BuildConfig.VERSION_NAME


    // Application startup language.
    val defaultLanguage: Language = Language.getLanguage(BuildConfig.DEFAULT_LANGUAGE)

    // Application ID.
    const val applicationId: String = BuildConfig.APPLICATION_ID

    // Base URL for calling LMS services
    const val baseUrl: String = BuildConfig.BASE_URL

    val environment: Environment by lazy {
        enumValueByName(BuildConfig.BUILD_TYPE, Environment.RELEASE)
    }

    val appVersionText: String by lazy {
        when (environment) {
            Environment.RELEASE -> ""
            else -> "Version ${BuildConfig.VERSION_NAME.split('-')[0]}"
        }
    }

    /**
     * Environment class to help enable/disable features based on environment and helps classify
     * app's behaviour more easily by providing enums and final values.
     *
     * @property debugLogsEnabled flag to enable/disable debug logs, should be true only for
     * development and testing environment.
     * @constructor Creates new Environment with specified configurations.
     */
    enum class Environment(val debugLogsEnabled: Boolean) {
        DEBUG(debugLogsEnabled = true),
        RELEASE(debugLogsEnabled = false);
    }
}
