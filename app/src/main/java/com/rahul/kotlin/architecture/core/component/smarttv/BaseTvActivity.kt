package com.rahul.kotlin.architecture.core.component.smarttv

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.rahul.kotlin.architecture.extension.createLocaleContext
import com.rahul.kotlin.architecture.extension.setLayoutDirectionExplicitly
import com.rahul.kotlin.architecture.state.DeviceType
import com.rahul.kotlin.architecture.state.StateBus

/**
 * Base abstract activity that should be used as parent class by all activities in this app.
 * This can be helpful in providing boiler plate code and basic functionality like tracking, etc.
 */
abstract class BaseTvActivity : FragmentActivity() {
    fun requireActivity(): FragmentActivity = this
    fun requireContext(): Context = this

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.createLocaleContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setLayoutDirectionExplicitly()
        }
        StateBus.setDeviceType(DeviceType.TV)
    }
}
