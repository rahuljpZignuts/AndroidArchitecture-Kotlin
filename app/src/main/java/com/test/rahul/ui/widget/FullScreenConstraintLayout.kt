package com.test.rahul.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.test.rahul.R
import com.test.rahul.ui.widget.helper.FullScreenContentHelper

class FullScreenConstraintLayout(
    context: Context, attrs: AttributeSet,
) : ConstraintLayout(context, attrs), FullScreenContentHelper {
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
        //post due to rendering issue
        post { applyFullScreenAttributes() }
    }

    override fun setFitsSystemWindows(fitSystemWindows: Boolean) {
        post {
            (parent as? View)?.let { it.fitsSystemWindows = fitSystemWindows }
                ?: if (isAttachedToWindow) throw RuntimeException(
                    "FullScreenConstraintLayout must be wrapped in parent layout to let " +
                            "fitsSystemWindows work because of possible bug in ConstraintLayout"
                )
                else { }
        }
    }
}
