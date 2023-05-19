package com.rahul.kotlin.architecture.core.component.smarttv

import android.view.View
import androidx.databinding.ViewDataBinding
import com.rahul.kotlin.architecture.core.component.DataBindingFragment
import com.rahul.kotlin.architecture.ui.widget.helper.TvLayoutFocusHelper

abstract class LeanbackBaseFragment<VB : ViewDataBinding> : DataBindingFragment<VB>() {
    override fun onViewInflated(root: View) {
        TvLayoutFocusHelper.setFocus(root)
    }
}
