package com.rahul.kotlin.architecture.extension

import android.content.res.ColorStateList
import android.graphics.Paint
import android.os.Build
import android.text.Html
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import com.rahul.kotlin.architecture.R

fun TextView.setTextWithVisibility(content: String?) {
    text = content
    isVisible = !content.isNullOrBlank()
}

fun TextView.setTextWithVisibility(@StringRes content: Int?) {
    content?.let { setText(it) }
    isVisible = content != null
}

fun TextView.setArgumentText(@StringRes resId: Int, vararg formatArgs: Any?) {
    text = context.getArgumentString(resId, *formatArgs)
}

fun TextView.underline() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

fun TextView.makeLinks(
    vararg links: Pair<String, View.OnClickListener>,
    color: Int = context.getAttribute(androidx.appcompat.R.attr.colorPrimary),
    underlined: Boolean = true,
) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = color
                textPaint.isUnderlineText = underlined
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        spannableString.setSpan(clickableSpan, startIndexOfLink,
            startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod = LinkMovementMethod.getInstance()
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun TextView.copyTextToClipBoard(@StringRes toastMessage: Int) {
    text.toString().copyToClipBoard(context, toastMessage)
}

fun TextView.setStartDrawable(@DrawableRes id: Int?) =
    this.setCompoundDrawablesRelativeWithIntrinsicBounds(id ?: 0, 0, 0, 0)

fun TextView.setHtmlText(
    htmlText: String?,
    setVisibility: Boolean = false
) {
    htmlText?.takeIf { it.isNotEmpty() }?.let {
        @Suppress("DEPRECATION")
        this.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(it)
        }
    }?.also {
        if (setVisibility)
            this.isVisible = htmlText.isNotEmpty()
    }
}

fun TextView.setEmphasizedText(
    text: String,
    textToEmphasize: String,
    @AttrRes spanColor: Int,
    @AttrRes emphasisStyle: Int = R.attr.textAppearanceHeadingH4Bold,
) {
    val spannableString = text.emphasizeText(context, textToEmphasize, spanColor, emphasisStyle)
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun TextView.setCompatDrawableTintList(color: Int) {
    TextViewCompat.setCompoundDrawableTintList(
        this, ColorStateList.valueOf(color)
    )
}
