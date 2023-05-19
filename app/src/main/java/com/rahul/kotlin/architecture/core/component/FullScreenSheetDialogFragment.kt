package com.rahul.kotlin.architecture.core.component

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

abstract class FullScreenSheetDialogFragment<VB : ViewDataBinding> :
    BindingSheetDialogFragment<VB>() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val bottomSheet =
                    findViewById<View?>(com.google.android.material.R.id.design_bottom_sheet)
                        ?: return@setOnShowListener
                bottomSheet.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    height = CoordinatorLayout.LayoutParams.MATCH_PARENT
                }
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
}
