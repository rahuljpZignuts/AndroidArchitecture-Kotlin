package com.rahul.kotlin.architecture.ui.widget.helper

import android.content.Context
import android.content.res.TypedArray
import android.view.View
import androidx.annotation.Px
import com.rahul.kotlin.architecture.R
import com.rahul.kotlin.architecture.extension.navigationBarHeight
import com.rahul.kotlin.architecture.extension.statusBarHeight

interface FullScreenContentHelper {
    val component: View get() = this as View

    var contentPaddingTop: Int
    var contentPaddingBottom: Int
    var contentPaddingStart: Int
    var contentPaddingEnd: Int
    var layoutIncludeStatusBar: Boolean
    var layoutIncludeNavigationBar: Boolean

    val systemBarsHeight: Int
        get() = getContext().navigationBarHeight + getContext().statusBarHeight
    val contentHeight: Int
        get() = component.height - systemBarsHeight

    fun getContext(): Context
    fun setFitsSystemWindows(fitSystemWindows: Boolean)
    fun setPadding(left: Int, top: Int, right: Int, bottom: Int)

    @Throws(Exception::class)
    fun loadFullScreenAttributes(attributes: TypedArray) {
        val padding: Int = attributes.getDimensionPixelSize(
            R.styleable.FullScreenContent_android_padding, 0)
        contentPaddingStart = attributes.getDimensionPixelSize(
            R.styleable.FullScreenContent_android_paddingStart, padding)
        contentPaddingTop = attributes.getDimensionPixelSize(
            R.styleable.FullScreenContent_android_paddingTop, padding)
        contentPaddingEnd = attributes.getDimensionPixelSize(
            R.styleable.FullScreenContent_android_paddingEnd, padding)
        contentPaddingBottom = attributes.getDimensionPixelSize(
            R.styleable.FullScreenContent_android_paddingBottom, padding)
        layoutIncludeStatusBar = attributes.getBoolean(
            R.styleable.FullScreenContent_layout_includeStatusBar, layoutIncludeStatusBar)
        layoutIncludeNavigationBar = attributes.getBoolean(
            R.styleable.FullScreenContent_layout_includeNavigationBar, layoutIncludeNavigationBar)
    }

    fun applyFullScreenAttributes() {
        setFitsSystemWindows(true)
        setContentPaddingInternal()
    }

    fun setContentPadding(@Px padding: Int) {
        contentPaddingStart = padding
        contentPaddingTop = padding
        contentPaddingEnd = padding
        contentPaddingBottom = padding
        setContentPaddingInternal()
    }

    fun setContentPadding(@Px start: Int, @Px top: Int, @Px end: Int, @Px bottom: Int) {
        contentPaddingStart = start
        contentPaddingTop = top
        contentPaddingEnd = end
        contentPaddingBottom = bottom
        setContentPaddingInternal()
    }

    private fun setContentPaddingInternal() {
        val top = contentPaddingTop +
                if (layoutIncludeStatusBar) getContext().statusBarHeight else 0
        val bottom = contentPaddingBottom +
                if (layoutIncludeNavigationBar) getContext().navigationBarHeight else 0
        setPadding(contentPaddingStart, top, contentPaddingEnd, bottom)
    }
}
