package com.test.rahul.core.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Base abstract fragment that should be used as parent class by all bottom sheet fragments in this app.
 * This can be helpful in providing boiler plate code and basic functionality like inflation,
 * tracking, etc.
 */
abstract class BaseSheetDialogFragment : BottomSheetDialogFragment() {
    /**
     * Layout resource id of the layout to be used by inherited fragment.
     */
    protected abstract val viewResourceId: Int
    protected val bottomSheetBehavior: BottomSheetBehavior<FrameLayout>?
        get() = (dialog as? BottomSheetDialog)?.behavior

    protected open fun inflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): View? = inflater.inflate(viewResourceId, container, false)

    /**
     * Method that can be used to perform early operations on views as it is called before
     * [onViewCreated] just after inflation.
     *
     * @param root reference to inflated root view.
     */
    protected open fun onViewInflated(root: View) {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflateView(inflater, container)?.also { onViewInflated(root = it) }

    protected fun setSwipeEnabled(isDraggable: Boolean = true) {
        bottomSheetBehavior?.isDraggable = isDraggable
    }
}
