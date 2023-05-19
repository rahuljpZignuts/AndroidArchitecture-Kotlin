package com.rahul.kotlin.architecture.core.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.rahul.kotlin.architecture.extension.onBackPressed

/**
 * Base abstract fragment that should be used as parent class by all fragments in this app.
 * This can be helpful in providing boiler plate code and basic functionality like inflation,
 * tracking, etc.
 */
abstract class BaseFragment : Fragment() {
    /**
     * Layout resource id of the layout to be used by inherited fragment.
     */
    protected abstract val viewResourceId: Int

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

    fun navigateTo(navDirections: NavDirections, vararg sharedElements: Pair<View, String>) {
        findNavController().navigate(navDirections, FragmentNavigatorExtras(*sharedElements))
    }

    fun parentNavigateTo(navDirections: NavDirections, vararg sharedElements: Pair<View, String>) {
        parentFragment?.let { parent ->
            parent.parentFragment?.findNavController()
                ?.navigate(navDirections, FragmentNavigatorExtras(*sharedElements))
        }
    }

    fun handleBackButton() {
        if (!findNavController().navigateUp()) onBackPressed()
    }
}
