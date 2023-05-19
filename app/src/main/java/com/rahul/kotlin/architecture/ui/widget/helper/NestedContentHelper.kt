package com.rahul.kotlin.architecture.ui.widget.helper

import android.content.res.TypedArray
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.rahul.kotlin.architecture.R
import com.rahul.kotlin.architecture.extension.enumValueByOrdinal
import timber.log.Timber
import kotlin.math.roundToInt

interface NestedContentHelper {
    val component: View get() = this as View

    var heightType: HeightType
    var maxHeightDimension: Int
    var minHeightDimension: Int
    var maxHeightRatio: Float
    var minHeightRatio: Float
    var constraintMinHeight: Int
    var constraintMaxHeight: Int

    @Throws(Exception::class)
    fun loadFullScreenAttributes(attributes: TypedArray) {
        maxHeightDimension = attributes.getDimensionPixelSize(
            R.styleable.NestedContent_android_maxHeight, 0)
        minHeightDimension = attributes.getDimensionPixelSize(
            R.styleable.NestedContent_android_minHeight, 0)
        maxHeightRatio = attributes.getFloat(
            R.styleable.NestedContent_maxHeightRatio, 1F)
            .coerceIn(MIN_HEIGHT_RATIO, 1F)
        minHeightRatio = attributes.getFloat(
            R.styleable.NestedContent_minHeightRatio, maxHeightRatio)
            .coerceIn(MIN_HEIGHT_RATIO, maxHeightRatio)
        heightType = enumValueByOrdinal(attributes.getInt(
            R.styleable.NestedContent_heightType, HEIGHT_TYPE_DEFAULT.ordinal),
            HEIGHT_TYPE_DEFAULT)
    }

    fun applyAttributes() {
        component.post { measureHeightInternal() }
    }

    private fun measureHeightInternal() {
        val view = component
        when (heightType) {
            HeightType.SCREEN -> {
                val heightPixels = view.resources.displayMetrics.heightPixels
                constraintMinHeight = (heightPixels * minHeightRatio).roundToInt()
                constraintMaxHeight = (constraintMinHeight * maxHeightRatio).roundToInt()
            }
            HeightType.PARENT -> {
                // Find first parent with appropriate height
                var container: View? = view.parent as? View
                while (container != null && container.height <= 0) {
                    container = container.parent as? View
                }
                val parentHeight = (container as? FullScreenContentHelper)?.contentHeight
                    ?: container?.height ?: 0
                constraintMinHeight = (parentHeight * minHeightRatio).roundToInt()
                constraintMaxHeight = (parentHeight * maxHeightRatio).roundToInt()
            }
            HeightType.DIMENSION -> {
                constraintMinHeight = minHeightDimension
                constraintMaxHeight = maxHeightDimension
            }
        }
        var constraintParent: View? = view
        while (constraintParent != null && constraintParent.parent !is ConstraintLayout) {
            constraintParent = constraintParent.parent as? View
        }
        if (constraintParent == null) {
            if (constraintMinHeight == constraintMaxHeight) {
                Timber.w("Fixing height using default height params")
                view.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = constraintMaxHeight
                }
            } else throw RuntimeException("Different dimension height is only supported within ConstraintLayout")
        } else {
            var viewInHierarchy: View? = view
            // Update all nested layout heights to match parent for expected behavior
            while (viewInHierarchy != constraintParent) {
                viewInHierarchy?.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = ViewGroup.LayoutParams.MATCH_PARENT
                }
                viewInHierarchy = viewInHierarchy?.parent as? View
            }
            // Update dynamic heights using ConstraintLayout support
            constraintParent.updateLayoutParams<ConstraintLayout.LayoutParams> {
                matchConstraintMinHeight = constraintMinHeight
                matchConstraintMaxHeight = constraintMaxHeight
            }
        }
    }

    enum class HeightType {
        SCREEN,
        PARENT,
        DIMENSION;
    }

    companion object {
        val HEIGHT_TYPE_DEFAULT = HeightType.PARENT
        private const val MIN_HEIGHT_RATIO = 0.2F
    }
}
