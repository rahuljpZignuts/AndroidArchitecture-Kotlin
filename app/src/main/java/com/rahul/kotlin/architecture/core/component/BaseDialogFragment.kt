package com.rahul.kotlin.architecture.core.component

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.rahul.kotlin.architecture.R

abstract class BaseDialogFragment : DialogFragment() {

    protected abstract val viewResourceId: Int

    protected open fun inflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): View? = inflater.inflate(viewResourceId, container, false)

    protected open fun onViewInflated(root: View) {
        root.setBackgroundResource(R.drawable.bg_round_corners_base_dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflateView(inflater, container)?.also { onViewInflated(root = it) }
}
