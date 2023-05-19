package com.test.rahul.state

import com.test.rahul.application.Config

/**
 * Language enum class to store app locale states.
 */
enum class Language(val code: String) {
    /**
     * Arabic state indicates that current locale of the app is in arabic and all views are in
     * right-to-left (RTL) mode.
     */
    ARABIC("ar"),

    /**
     * English state indicates that current locale of the app is in english and all views are in
     * left-to-right (LTR) mode..
     */
    ENGLISH("en");

    companion object {
        fun getLanguage(language: String): Language = values().find {
            it.code.equals(language, ignoreCase = true)
        } ?: Config.defaultLanguage
    }
}
