package com.rahul.kotlin.architecture.os.permission

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

/**
 * Single permission keep all required permission in the app at single place. It also helps
 * tracking the permissions acquired and minimize the possibility of mixing multiple permissions
 * wrongly be combining all of them in a single enum at single place.
 *
 * @property rationale message to explain user why we need this permission.
 * @property permissions list of permissions required for specific action e.g. location permission
 * may include multiple permissions to be acquired together.
 */
enum class Permission(
    @StringRes val rationale: Int,
    val permissions: Array<String>,
) {
    /**
     * Permission to check device connectivity. Since it isn't a dangerous permission and can be
     * acquired silently, we can skip its rationale by setting its value to 0.
     */
    NETWORK_STATE(0, arrayOf(permission.ACCESS_NETWORK_STATE));
}

/**
 * Extension method to simplify checking if the required permission is acquired or not.
 *
 * @param context reference to activity context.
 * @return true if all permissions in the list are acquired; false if any of the permissions in
 * list is not given.
 */
fun Permission.hasPermission(context: Context): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}
