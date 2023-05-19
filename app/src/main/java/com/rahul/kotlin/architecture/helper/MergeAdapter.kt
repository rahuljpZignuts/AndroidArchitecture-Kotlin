package com.rahul.kotlin.architecture.helper

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

interface MergeAdapter<T : Any, VH : RecyclerView.ViewHolder> {
    var header: MergeItemFixed?
    var footer: MergeItemFixed?

    fun getActualItem(position: Int): T
    fun getFakeItem(position: Int): Any?

    fun clearHeader() {
        header = null
    }

    fun setHeader(@LayoutRes layout: Int) {
        header = MergeItemFixed(ITEM_VIEW_TYPE_HEADER, layout)
    }

    fun clearFooter() {
        footer = null
    }

    fun setFooter(@LayoutRes layout: Int) {
        footer = MergeItemFixed(ITEM_VIEW_TYPE_FOOTER, layout)
    }

    fun getMergeItemViewType(position: Int): Int {
        return when (val item = getFakeItem(position)) {
            is MergeItem<*> -> item.viewType
            else -> ITEM_VIEW_TYPE_DEFAULT
        }
    }

    fun onCreateViewHolder(parent: ViewGroup): VH {
        throw RuntimeException("Child must override onCreateViewHolder to provide extended ViewHolders")
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        ITEM_VIEW_TYPE_HEADER -> header?.let {
            ViewHolder(LayoutInflater.from(parent.context)
                .inflate(it.content, parent, false))
        }
        ITEM_VIEW_TYPE_FOOTER -> footer?.let {
            ViewHolder(LayoutInflater.from(parent.context)
                .inflate(it.content, parent, false))
        }
        else -> null
    } ?: onCreateViewHolder(parent)

    fun onBindViewHolder(holder: VH, position: Int, viewType: Int) {}
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getMergeItemViewType(position)
        if (holder is ViewHolder) {
            holder.bind(getFakeItem(position) as MergeItemFixed)
        } else {
            @Suppress("UNCHECKED_CAST")
            onBindViewHolder(holder as VH, position, viewType)
        }
    }

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        open fun bind(item: MergeItemFixed) {}
    }

    @Suppress("NULLABLE_TYPE_PARAMETER_AGAINST_NOT_NULL_TYPE_PARAMETER", "UNCHECKED_CAST")
    class MergeItemDiffCallback<T>(
        private val diffCallback: DiffUtil.ItemCallback<T>,
    ) : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if (oldItem is MergeItemFixed || newItem is MergeItemFixed) {
                oldItem === newItem
            } else diffCallback.areItemsTheSame(oldItem as T, newItem as T)
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if (oldItem is MergeItemFixed || newItem is MergeItemFixed) {
                if (oldItem is MergeItemFixed && newItem is MergeItemFixed) oldItem == newItem
                else false
            } else diffCallback.areContentsTheSame(oldItem as T, newItem as T)
        }
    }

    open class MergeItem<T>(open val viewType: Int, val content: T)

    class MergeItemFixed(
        viewType: Int,
        @LayoutRes layoutResId: Int,
        val showIfEmpty: Boolean = false,
    ) : MergeItem<Int>(viewType, layoutResId)

    companion object {
        const val ITEM_VIEW_TYPE_DEFAULT = 0
        const val ITEM_VIEW_TYPE_HEADER = 1001
        const val ITEM_VIEW_TYPE_FOOTER = 1002
    }
}
