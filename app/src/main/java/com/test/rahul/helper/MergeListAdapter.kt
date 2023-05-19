package com.test.rahul.helper

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class MergeListAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>,
) : MergeAdapter<T, VH>,
    ListAdapter<Any, RecyclerView.ViewHolder>(MergeAdapter.MergeItemDiffCallback(diffCallback)) {
    private val mergedItemsList: MutableList<Any> = mutableListOf()

    private var actualItemsList: List<T>? = null
    override var header: MergeAdapter.MergeItemFixed? = null
    override var footer: MergeAdapter.MergeItemFixed? = null

    @Suppress("UNCHECKED_CAST")
    override fun getActualItem(position: Int): T {
        return super.getItem(position) as T
    }

    override fun getFakeItem(position: Int): Any? {
        return super.getItem(position)
    }

    override fun getItemViewType(position: Int): Int {
        return getMergeItemViewType(position)
    }

    @Suppress("UNCHECKED_CAST")
    override fun submitList(list: List<Any>?) {
        super.submitList(getSubmitListInternal(list as List<T>?))
        notifyDataSetChanged()
    }

    @Suppress("UNCHECKED_CAST")
    override fun submitList(list: List<Any>?, commitCallback: Runnable?) {
        super.submitList(getSubmitListInternal(list as List<T>?), commitCallback)
        notifyDataSetChanged()
    }

    private fun getSubmitListInternal(list: List<T>?): MutableList<Any> {
        if (list === actualItemsList) return mergedItemsList
        actualItemsList = list
        mergedItemsList.clear()
        val isContentEmpty = list.isNullOrEmpty()
        header?.let { if (!isContentEmpty || it.showIfEmpty) mergedItemsList.add(it) }
        list?.let { mergedItemsList.addAll(it) }
        footer?.let { if (!isContentEmpty || it.showIfEmpty) mergedItemsList.add(it) }
        return mergedItemsList
    }
}

fun MergeListAdapter<*, *>.isLastItem(position: Int): Boolean {
    val lastIndex = itemCount - if (footer != null) 2 else 1
    return position >= lastIndex
}
