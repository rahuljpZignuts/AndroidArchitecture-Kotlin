package com.rahul.kotlin.architecture.ui.widget.helper

import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView

object TvLayoutFocusHelper {
    const val ZOOM_ANIM_TIME_SHORT = 200L

    fun setFocus(root: View) {
        iterateViewChildren(root)
    }

    private fun iterateViewChildren(view: View) {
        if (view is NestedScrollView) {
            view.isFocusable = false
            view.isFocusableInTouchMode = false
            view.clipChildren = false
            for (i in 0 until view.childCount) {
                iterateViewChildren(view.getChildAt(i))
            }
            return
        }
        if (view.isFocusable) {
//            view.getTag(R.id.hover_zoom_tv)?.let {
//                if (it.toString().toBoolean()) {
//                    setFocusListener(view)
//                }
//            }
        } else if (view is ViewGroup) {
            view.clipChildren = false
            for (i in 0 until view.childCount) {
                iterateViewChildren(view.getChildAt(i))
            }
        }
    }

    private fun setFocusListener(view: View) {
        view.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                view.elevation = 100f
                view.animate().scaleX(1.3f).scaleY(1.3f).setDuration(ZOOM_ANIM_TIME_SHORT)
                    .start()
            } else {
                view.elevation = 0f
                view.animate().scaleX(1f).scaleY(1f).setDuration(ZOOM_ANIM_TIME_SHORT)
                    .start()
            }
        }
    }

}
