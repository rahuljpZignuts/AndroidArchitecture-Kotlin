package com.rahul.kotlin.architecture.core.component

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Data binding abstract fragment that should be used as parent class by all fragments that need
 * to use [ViewDataBinding] in them.
 *
 * @param VB the [ViewDataBinding] type to be used by inherited fragment.
 */
abstract class DataBindingFragment<VB : ViewDataBinding> : BaseFragment() {
    // This property holds view data binding reference
    private var _binding: VB? = null

    // This property is only valid between onCreateView and onDestroyView
    protected val binding get() = _binding!!

    final override fun inflateView(inflater: LayoutInflater, container: ViewGroup?): View {
        _binding = DataBindingUtil.inflate(inflater, viewResourceId, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
