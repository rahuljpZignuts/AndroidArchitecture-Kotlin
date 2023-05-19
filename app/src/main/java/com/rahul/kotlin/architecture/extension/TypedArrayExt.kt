package com.rahul.kotlin.architecture.extension

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.TypedValue
import androidx.annotation.AnyRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StyleableRes
import androidx.appcompat.content.res.AppCompatResources

/**
 * Returns the [ColorStateList] from the attributes. The resource can include themed attributes,
 * regardless of API level.
 */
fun TypedArray.getColorStateList(
    context: Context,
    @StyleableRes index: Int,
    @ColorRes fallback: Int,
): ColorStateList {
    val resId: Int = getResourceId(index, 0).takeIf { it != 0 } ?: fallback
    return getColorStateList(index) ?: AppCompatResources.getColorStateList(context, resId)
}

@ColorInt
fun TypedArray.optColor(@StyleableRes index: Int, @ColorInt defValue: Int = 0): Int {
    return if (hasValue(index)) getColor(index, defValue)
    else defValue
}

@AnyRes
fun TypedArray.optResourceId(@StyleableRes index: Int, defValue: Int = 0): Int {
    return if (hasValue(index)) getResourceId(index, defValue)
    else defValue
}

@AnyRes
fun TypedArray.optAttributeId(@StyleableRes index: Int, defValue: Int = 0): Int {
    return if (hasValue(index)) TypedValue().apply { getValue(index, this) }.data
    else defValue
}
