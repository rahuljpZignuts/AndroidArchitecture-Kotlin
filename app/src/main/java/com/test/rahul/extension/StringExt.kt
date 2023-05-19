package com.test.rahul.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.util.Patterns
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.core.text.bold
import com.test.rahul.R
import java.net.URL
import java.util.Locale

fun String.toDigits(): String = this.filter { it.isDigit() }

fun String?.toBoolean(): Boolean = when {
    this.equals("false", true) -> false
    else -> true
}

fun String.toBoldStyle(): Spannable = SpannableStringBuilder()
    .bold { append(this@toBoldStyle) }

fun String.copyToClipBoard(context: Context, @StringRes toastMessage: Int) {
    val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    manager.setPrimaryClip(ClipData.newPlainText("", this))
    context.showToast(context.getString(toastMessage))
}

fun String.capitalise(locale: Locale = Locale.getDefault()): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}

val String.toUrl get() = if (this.isValidUrl) URL(this) else null

val String.isValidUrl get() = Patterns.WEB_URL.matcher(this).matches()

fun String.emphasizeText(
    context: Context,
    textToEmphasize: String,
    @AttrRes spanColor: Int,
    @AttrRes emphasisStyle: Int = R.attr.textAppearanceHeadingH4Bold,
): SpannableString {
    val startPosition = this.indexOf(textToEmphasize)
    val endPosition = startPosition + textToEmphasize.length
    val spannableString = SpannableString(this)
    spannableString.setSpan(
        TextAppearanceSpan(context, context.getAttribute(emphasisStyle)),
        startPosition,
        endPosition,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    spannableString.setSpan(
        ForegroundColorSpan(context.getAttribute(spanColor)),
        startPosition,
        endPosition,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}
