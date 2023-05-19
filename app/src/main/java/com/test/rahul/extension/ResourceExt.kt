package com.test.rahul.extension

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import com.test.rahul.R

fun Drawable.changeSolidColor(context: Context, @AttrRes color: Int, setStroke: Boolean = false) {
    when (this) {
        is ShapeDrawable -> {
            // cast to 'ShapeDrawable'
            val shapeDrawable = this
            shapeDrawable.paint.color = context.getAttribute(color)
        }
        is GradientDrawable -> {
            // cast to 'GradientDrawable'
            val gradientDrawable = this
            gradientDrawable.setColor(context.getAttribute(color))
            if (setStroke) {
                if (color == R.attr.colorTransparent)
                    gradientDrawable.setStroke(context.getDimen(R.dimen.default_stroke),
                        context.getAttribute(R.attr.colorStepTextDescription))
                else
                    gradientDrawable.setStroke(context.getDimen(R.dimen.default_stroke),
                        context.getAttribute(color))
            }
        }
        is ColorDrawable -> {
            // alpha value may need to be set again after this call
            val colorDrawable = this
            colorDrawable.color = context.getAttribute(color)
        }
    }
}

fun Drawable.changeSolidColor(color: Int) {
    when (this) {
        is ShapeDrawable -> {
            // cast to 'ShapeDrawable'
            val shapeDrawable = this
            shapeDrawable.paint.color = color
        }
        is GradientDrawable -> {
            // cast to 'GradientDrawable'
            val gradientDrawable = this
            gradientDrawable.setColor(color)
        }
        is ColorDrawable -> {
            // alpha value may need to be set again after this call
            val colorDrawable = this
            colorDrawable.color = color
        }
    }
}

fun Drawable.changeBackgroundTint(@ColorInt color: Int?): Drawable = color?.let {
    DrawableCompat.wrap(this).also { wrappedDrawable ->
        DrawableCompat.setTint(wrappedDrawable, color)
    }
} ?: this
