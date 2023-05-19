package com.test.rahul.core.component.smarttv

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.test.rahul.extension.createLocaleContext
import com.test.rahul.extension.setLayoutDirectionExplicitly
import com.test.rahul.state.DeviceType
import com.test.rahul.state.StateBus

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
