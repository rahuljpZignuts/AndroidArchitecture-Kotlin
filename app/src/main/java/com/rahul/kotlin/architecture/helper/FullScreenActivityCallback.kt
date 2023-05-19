package com.rahul.kotlin.architecture.helper

interface FullScreenActivityCallback {

    fun hideSystemBars(fadeOut: Boolean = true)

    fun showSystemBars()

    companion object {
        const val SYSTEM_BARS_VISIBILITY_TIME = 2_000L //2 seconds
    }
}
