package com.test.rahul.core.component.smarttv

import android.view.View
import androidx.databinding.ViewDataBinding
import com.test.rahul.core.component.DataBindingFragment
import com.test.rahul.ui.widget.helper.TvLayoutFocusHelper

abstract class LeanbackBaseFragment<VB : ViewDataBinding> : DataBindingFragment<VB>() {
    override fun onViewInflated(root: View) {
        TvLayoutFocusHelper.setFocus(root)
    }
}
