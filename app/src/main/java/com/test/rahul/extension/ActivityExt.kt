package com.test.rahul.extension

import android.app.Activity
import android.content.Intent
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.View
import android.view.WindowInsets
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.test.rahul.data.enums.DEFAULT_TRANSITION_ANIM
import com.test.rahul.data.enums.TransitionAnim
import com.test.rahul.state.Language
import com.test.rahul.state.StateBus

fun Activity.recreateWithAnimation(
    transitionAnim: TransitionAnim = TransitionAnim.FADE_IN,
) {
    recreate()
    overridePendingTransition(transitionAnim)
}

fun Activity.finishWithAnimation(
    transitionAnim: TransitionAnim = TransitionAnim.SLIDE_IN_END_OUT_START,
) {
    finish()
    overridePendingTransition(transitionAnim)
}

fun Activity.overridePendingTransition(transitionAnim: TransitionAnim) {
    // Added RTL check because activity `overridePendingTransition`
    // was not picking the RTL anim from resourceId and was using LTR anim
    // P.S (As activity may be picking RTL from device context)
    if (getCurrentLanguage() == Language.ARABIC) {
        overridePendingTransition(
            transitionAnim.enterAnimRTL,
            transitionAnim.exitAnimRTL,
        )
    } else {
        overridePendingTransition(
            transitionAnim.enterAnimLTR,
            transitionAnim.exitAnimLTR,
        )
    }
}

fun <T> Activity.launchActivity(
    destination: Class<T>,
    flags: Int? = null,
    transitionAnim: TransitionAnim? = DEFAULT_TRANSITION_ANIM,
    extras: Bundle.() -> Unit = {},
) {
    val intent = Intent(this, destination)
    intent.putExtras(Bundle().apply(extras))
    if (flags != null) {
        intent.flags = flags
    }
    startActivity(intent)
    transitionAnim?.let { overridePendingTransition(it) }
}

fun Activity.launchIntent(
    intent: Intent,
    transitionAnim: TransitionAnim? = DEFAULT_TRANSITION_ANIM,
) {
    startActivity(intent)
    transitionAnim?.let { overridePendingTransition(it) }
}

fun Activity.requestFullScreenMode(rootView: View) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, rootView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
    }
}

fun Activity.toggleSystemBarsVisibility(showSystemBars: Boolean, rootView: View) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, rootView).let { controller ->
        if (showSystemBars) {
            controller.show(WindowInsetsCompat.Type.systemBars())
        } else {
            controller.hide(WindowInsetsCompat.Type.systemBars())
        }
    }
}

fun Activity.addSystemBarsVisibilityListener(
    onSystemBarsVisibilityChanged: (Boolean) -> Unit,
) {
    val view = window.decorView
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        view.setOnApplyWindowInsetsListener { v, insets ->
            val suppliedInsets = v.onApplyWindowInsets(insets)
            val systemBarsVisible =
                suppliedInsets.isVisible(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            onSystemBarsVisibilityChanged.invoke(systemBarsVisible)
            return@setOnApplyWindowInsetsListener suppliedInsets
        }
    } else {
        @Suppress("DEPRECATION")
        view.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                onSystemBarsVisibilityChanged.invoke(true)
            } else {
                onSystemBarsVisibilityChanged.invoke(false)
            }
        }
    }
}

@Suppress("DEPRECATION")
fun Activity.setOrientationEventListener(onOrientationChanged: (Int?) -> Unit) {
    val orientationEventListener = object : OrientationEventListener(
        this,
        SensorManager.SENSOR_DELAY_NORMAL
    ) {
        override fun onOrientationChanged(orientation: Int) {
            val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) display
            else windowManager.defaultDisplay
            onOrientationChanged(display?.rotation)
        }
    }
    orientationEventListener.enable()
}

/**
 * Creating this extension to only cater layout direction issue in Android 12 for now
 */
fun Activity.setLayoutDirectionExplicitly(language: Language = StateBus.language) {
    val direction = if (language == Language.ARABIC) View.LAYOUT_DIRECTION_RTL
    else View.LAYOUT_DIRECTION_LTR
    window.decorView.layoutDirection = direction
}

fun Activity.recreateNewTask() {
    with(Intent(this, this::class.java)) {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(this)
    }
    finish()
}
