package com.rahul.kotlin.architecture.lifecycle.bo

import android.content.Context
import androidx.annotation.StringRes
import com.rahul.kotlin.architecture.R
import com.rahul.kotlin.architecture.extension.getArgumentString
import java.text.MessageFormat

/**
 * String resource is a sealed class to wrap localized strings without the need of context.
 */
sealed class StringResource(open val args: Array<Any>) {
    companion object {
        /**
         * Creates the relevant StringResource class based on provided data. Creates
         * [RawStringResource] if [rawText] is not null or blank or fallback to
         * [AndroidStringResource] using the provided localized string resource.
         *
         * @param rawText raw text resource to create string resource from, supports null values.
         * @param resId reference to localized string resource as fallback to make sure we have a
         * valid string resource for every instance; default value pointing to generic error
         * message for ease.
         * @return valid [StringResource] created from provided data.
         */
        fun create(
            rawText: String? = null,
            @StringRes resId: Int = R.string.error_unexpected_behaviour_description,
        ): StringResource = when {
            !rawText.isNullOrBlank() -> RawStringResource(rawText)
            else -> AndroidStringResource(resId)
        }
    }
}

/**
 * Android string resource to wrap localized string references.
 */
data class AndroidStringResource(
    @StringRes val resId: Int,
    override val args: Array<Any> = emptyArray()
) : StringResource(args)

/**
 * Raw string resource to use with plain text.
 */
data class RawStringResource internal constructor(val rawText: String) :
    StringResource(emptyArray())

/**
 * Helper method to get string text from [StringResource] easily.
 *
 * @param context reference to localized context.
 * @param formatArgs additional string arguments (if any).
 * @return formatted localized string from the provided resource.
 */
fun StringResource.getText(context: Context, vararg formatArgs: Any? = args): String {
    return when (this) {
        is AndroidStringResource -> context.getArgumentString(resId, *formatArgs)
        is RawStringResource -> if (rawText.isBlank() || formatArgs.isEmpty()) rawText
        else MessageFormat.format(rawText, *formatArgs)
    }
}
