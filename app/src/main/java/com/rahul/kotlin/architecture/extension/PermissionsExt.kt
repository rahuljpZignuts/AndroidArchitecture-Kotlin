package com.rahul.kotlin.architecture.extension

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified R : ActivityResultLauncher<Array<String>>> Fragment.requestPermission(
    noinline onGranted: () -> Unit = {},
    noinline onDenied: (permission: String) -> Unit = {},
    noinline showRationale: (permission: String) -> Unit = {},
): ReadOnlyProperty<Fragment, R> = PermissionResultDelegate(
    this,
    onGranted,
    onDenied,
    showRationale
)


class PermissionResultDelegate<R : ActivityResultLauncher<String>>(
    private val fragment: Fragment,
    private val granted: () -> Unit,
    private val denied: (permission: String) -> Unit,
    private val explained: (permission: String) -> Unit,
) : ReadOnlyProperty<Fragment, R> {

    private var permissionResult: ActivityResultLauncher<Array<String>>? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragment.apply {
                    permissionResult = registerForActivityResult(
                        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                        val allGranted = permissions.all { it.value }
                        if (allGranted) {
                            granted.invoke()
                        } else {
                            val firstDenied = permissions.entries.firstOrNull { !it.value }
                            firstDenied?.let { deniedPermission ->
                                val showRationale =
                                    shouldShowRequestPermissionRationale(deniedPermission.key)
                                if (showRationale) {
                                    explained.invoke(deniedPermission.key)
                                } else {
                                    denied.invoke(deniedPermission.key)
                                }
                            }
                        }
                    }
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                permissionResult = null
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): R {
        permissionResult?.let { return (it as R) }
        error("Failed to Initialize Permission")
    }
}
