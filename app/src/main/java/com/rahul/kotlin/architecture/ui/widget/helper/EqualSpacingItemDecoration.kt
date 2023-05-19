package com.rahul.kotlin.architecture.ui.widget.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rahul.kotlin.architecture.state.StateBus
import kotlin.math.ceil

class EqualSpacingItemDecoration(
    private val left: Int = 0,
    private val right: Int = 0,
    private val last: Int = 0,
    private val first: Int = 0,
    private val top: Int = 0,
    private val firstTop: Int = 0,
    private val lastBottom: Int = 0,
    private val bottom: Int = 0,
    private val displayMode: Int = -1,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val position = parent.getChildViewHolder(view).absoluteAdapterPosition
        val itemCount = state.itemCount
        setSpacing(outRect, position, itemCount, parent.layoutManager)
    }

    private fun setSpacing(
        outRect: Rect,
        position: Int,
        itemCount: Int,
        layoutManager: RecyclerView.LayoutManager?,
    ) {
        when (displayMode) {
            HORIZONTAL_CUSTOM_LEFT_RIGHT -> {
                outRect.left = if (!StateBus.isLanguageRTL) {
                    if (position == 0) first else left
                } else {
                    if (position == itemCount - 1) last else right
                }
                outRect.right = if (!StateBus.isLanguageRTL) {
                    if (position == itemCount - 1) last else right
                } else {
                    if (position == 0) first else left
                }
                outRect.top = top
                outRect.bottom = bottom
            }
            GRID_CUSTOM -> {
                val cols = (layoutManager as? GridLayoutManager)?.spanCount
                cols?.let {
                    val rows = ceil(itemCount.toFloat() / cols).toInt()
                    val currentRow = position / cols
                    outRect.left = if (StateBus.isLanguageRTL) {
                        if (position % cols == cols - 1) last else right
                    } else {
                        if (position % cols == 0) first else left
                    }
                    outRect.right = if (StateBus.isLanguageRTL) {
                        if (position % cols == 0) first else left
                    } else {
                        if (position % cols == cols - 1) last else right
                    }
                    outRect.top = if (currentRow == 0) firstTop else top
                    outRect.bottom = if (currentRow == rows - 1) lastBottom else bottom
                }
            }
        }
    }

    companion object {
        const val HORIZONTAL_CUSTOM_LEFT_RIGHT = 1
        const val GRID_CUSTOM = 2
    }
}
