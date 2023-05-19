package com.test.rahul.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.test.rahul.R
import com.test.rahul.ui.widget.helper.NestedContentHelper

class NestedFrameLayout(
    context: Context, attrs: AttributeSet,
) : FrameLayout(context, attrs), NestedContentHelper {
    override var heightType: NestedContentHelper.HeightType =
        NestedContentHelper.HEIGHT_TYPE_DEFAULT
    override var maxHeightDimension: Int = 0
    override var minHeightDimension: Int = 0
    override var maxHeightRatio: Float = 1F
    override var minHeightRatio: Float = 1F
    override var constraintMinHeight: Int = 0
    override var constraintMaxHeight: Int = 0

    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.NestedContent, 0, 0
        ).apply {
            try {
                loadFullScreenAttributes(this)
            } finally {
                recycle()
            }
        }
        applyAttributes()
    }
}
