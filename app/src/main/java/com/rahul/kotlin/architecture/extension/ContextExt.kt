package com.rahul.kotlin.architecture.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.LocaleList
import android.provider.Settings
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.rahul.kotlin.architecture.application.App
import com.rahul.kotlin.architecture.state.Language
import com.rahul.kotlin.architecture.state.StateBus
import java.text.MessageFormat
import java.util.*
import kotlin.math.ceil

fun Context.getCompatColor(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun Context.getCompatDrawable(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}

fun Context.getAttributedDrawable(@AttrRes id: Int) = getCompatDrawable(getAttributeResourceId(id))

fun Context.getDimen(@DimenRes id: Int): Int {
    return resources.getDimensionPixelSize(id)
}

fun Context.getAttribute(@AttrRes id: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(id, typedValue, true)
    return typedValue.data
}

fun Context.getAttributeResourceId(@AttrRes id: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(id, typedValue, true)
    return typedValue.resourceId
}

fun Context.showToast(message: String?, length: Int = Toast.LENGTH_SHORT) {
    if (message.isNullOrBlank()) return
    Toast.makeText(this, message, length).show()
}

fun Context.createLocaleContext(): Context {
    val language = StateBus.language.code
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = Configuration(this.resources.configuration)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        config.setLocales(LocaleList(locale))
    } else {
        config.setLocale(locale)
    }
    return createConfigurationContext(config)
}

fun Context.getCurrentLanguage(): Language {
    return Language.getLanguage(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0].language
        } else {
            @Suppress("DEPRECATION")
            resources.configuration.locale.language
        }
    )
}

fun Context.getArgumentString(@StringRes resId: Int, vararg formatArgs: Any?): String {
    return MessageFormat.format(getString(resId), *formatArgs)
}

val Context.statusBarHeight: Int
    get() {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ceil(24 * resources.displayMetrics.density).toInt()
            } else {
                ceil(25 * resources.displayMetrics.density).toInt()
            }
        }
    }

@Suppress("DEPRECATION")
val Context.navigationBarHeight: Int
    get() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insets = windowManager.currentWindowMetrics.windowInsets
                .getInsets(WindowInsets.Type.navigationBars())
            if (resources.configuration.orientation == ORIENTATION_PORTRAIT) {
                insets.bottom
            } else {
                insets.right
            }
        } else {
            val currentDisplay = windowManager.defaultDisplay
            val appUsableSize = Point()
            val realScreenSize = Point()
            currentDisplay?.apply {
                getSize(appUsableSize)
                getRealSize(realScreenSize)
            }
            return when {
                // navigation bar on the side
                appUsableSize.x < realScreenSize.x -> realScreenSize.x - appUsableSize.x
                // navigation bar at the bottom
                appUsableSize.y < realScreenSize.y -> realScreenSize.y - appUsableSize.y
                else -> 0
            }
        }
    }

fun Context.getActivity(): Activity? {
    return (this as? Activity) ?: (this as? ContextWrapper)?.getActivity()
}

fun Context.isDarkThemeOn(): Boolean {
    return resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}

fun Context.hideKeyboard(view: View?) {
    view?.let { it ->
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

val Context.application: App
    get() = applicationContext as App

fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.fromParts("package", this.packageName, null)
    this.startActivity(intent)
}

fun Context.queryIntentActivities(intent: Intent, flag: Int): List<ResolveInfo> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.packageManager.queryIntentActivities(
            intent,
            PackageManager.ResolveInfoFlags.of(flag.toLong())
        )
    } else {
        this.packageManager.queryIntentActivities(intent, flag)
    }
}
