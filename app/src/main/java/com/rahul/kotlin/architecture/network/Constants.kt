package com.rahul.kotlin.architecture.network

/**
 * Constants class to hold network constants.
 */
object Constants {
    const val BEARER_TOKEN_FORMAT = "Bearer %s"
    const val HEADER_ALW_APPLICATION = "X-ALW-Application: true"
    const val HEADER_CONTENT_TYPE_PATCH_JSON = "Content-Type: application/merge-patch+json"
    const val HEADER_KEY_ACCEPT_LANGUAGE = "HTTP-Accept-Language"
    const val HEADER_KEY_USER_AGENT = "User-Agent"
    const val USER_AGENT_FORMAT = "edX/org.edx.mobile Dalvik/.1 (Linux; U; Android .%d; ALW Build/%d) %s/%s"
    const val HEADER_KEY_AUTHORIZATION = "Authorization"
    const val HEADER_KEY_COOKIE = "Cookie"
    const val HEADER_KEY_REDIRECT_URL = "Location"
    const val HEADER_KEY_REFERER = "Referer"
    const val HEADER_KEY_SET_COOKIE = "Set-Cookie"
    const val REFRESH_TOKEN_EXPIRY_THRESHOLD = 60
    const val REFRESH_TOKEN_INTERVAL_MINIMUM = 300
    const val TOKEN_TYPE_REFRESH = "refresh_token"
}
