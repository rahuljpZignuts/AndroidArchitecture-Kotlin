package com.rahul.kotlin.architecture.helper

import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.paging.insertFooterItem
import androidx.paging.insertHeaderItem
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class MergePagingAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>,
) : MergeAdapter<T, VH>,
    PagingDataAdapter<Any, RecyclerView.ViewHolder>(MergeAdapter.MergeItemDiffCallback(diffCallback)) {
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

    suspend fun submitPagingData(pagingData: PagingData<Any>) {
        super.submitData(getSubmitDataInternal(pagingData))
    }

    fun submitPagingData(lifecycle: Lifecycle, pagingData: PagingData<Any>) {
        super.submitData(lifecycle, getSubmitDataInternal(pagingData))
    }

    private fun getSubmitDataInternal(pagingData: PagingData<Any>): PagingData<Any> {
        var newPagingData = pagingData
        header?.let { newPagingData = newPagingData.insertHeaderItem(item = it) }
        footer?.let { newPagingData = newPagingData.insertFooterItem(item = it) }
        return newPagingData
    }
}
