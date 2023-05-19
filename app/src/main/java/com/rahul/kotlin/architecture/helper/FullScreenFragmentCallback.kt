package com.rahul.kotlin.architecture.helper

interface FullScreenFragmentCallback {
    /**
     * Checks if the fragment should show system bars or not
     */
    fun shouldShowSystemBars(): Boolean

    fun onSystemBarsVisibilityChanged(visible: Boolean)
}
