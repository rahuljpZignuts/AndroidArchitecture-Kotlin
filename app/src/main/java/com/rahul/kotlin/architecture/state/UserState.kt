package com.rahul.kotlin.architecture.state

/**
 * User state enum class to store user authorization states.
 */
enum class UserState {
    /**
     * Authorized state indicates that user is authenticated and should be able to continue using
     * the app normally.
     */
    AUTHORIZED,

    /**
     * Session expired indicates that user authentication has expired and/or force invalidated
     * and they need to login again to use the app.
     */
    SESSION_EXPIRED,

    /**
     * Session invalidated state indicates that user authorization has invalidated on user
     * request and they need to login again to use the app.
     */
    SESSION_INVALIDATED,

    /**
     * Unauthorized state indicates that user was either never logged in or they have been logged
     * out previously.
     */
    UNAUTHORIZED,

    /**
     * Unidentified state indicates that user authentication has not been identified and all
     * related operations should wait for the state to be updated.
     */
    UNIDENTIFIED;
}
