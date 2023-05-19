package com.test.rahul.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import com.test.rahul.R
import com.test.rahul.ui.widget.helper.FullScreenContentHelper

class FullScreenScrollView(
    context: Context, attrs: AttributeSet,
) : ScrollView(context, attrs), FullScreenContentHelper {
    override var contentPaddingStart: Int = 0
    override var contentPaddingTop: Int = 0
    override var contentPaddingEnd: Int = 0
    override var contentPaddingBottom: Int = 0
    override var layoutIncludeStatusBar: Boolean = true
    override var layoutIncludeNavigationBar: Boolean = true

    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.FullScreenContent, 0, 0
        ).apply {
            try {
                loadFullScreenAttributes(this)
            } finally {
                recycle()
            }
        }
        applyFullScreenAttributes()
    }

    override fun applyFullScreenAttributes() {
        super.applyFullScreenAttributes()
        post { setChildHeightToFullScreen() }
    }

    private fun setChildHeightToFullScreen() {
        val child = getChildAt(0)
        val minimumHeight = height - systemBarsHeight
        when (child) {
            is FullScreenContentHelper -> throw RuntimeException(
                "FullScreenScrollView must not contain any FullScreenContentHelper layout to " +
                        "avoid repetitive system bar margins")
            !is ConstraintLayout -> throw RuntimeException(
                "FullScreenScrollView must contain ConstraintLayout descendant to support " +
                        "minimum height for full screen accessibility")
            else -> child.minHeight = minimumHeight
        }
    }
}
