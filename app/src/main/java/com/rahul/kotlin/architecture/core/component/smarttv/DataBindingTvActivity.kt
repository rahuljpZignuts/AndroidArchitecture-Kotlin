package com.rahul.kotlin.architecture.core.component.smarttv

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Data binding abstract activity that should be used as parent class by all activities that need
 * to use [ViewDataBinding] in them.
 *
 * @param VB the [ViewDataBinding] type to be used by inherited activity.
 */
abstract class DataBindingTvActivity<VB : ViewDataBinding> : BaseTvActivity() {
    /**
     * Layout resource id of the layout to be used by inherited activity.
     */
    protected abstract val viewResourceId: Int

    // This property holds view data binding reference
    private var _binding: VB? = null

    // This property is valid after onCreate
    protected val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, viewResourceId)
    }
}
