package com.rahul.kotlin.architecture.ui.widget.helper

import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.faltenreich.skeletonlayout.createSkeleton
import com.rahul.kotlin.architecture.R
import com.rahul.kotlin.architecture.databinding.ViewEmptyStateBinding
import com.rahul.kotlin.architecture.databinding.ViewEmptyStateTvBinding
import com.rahul.kotlin.architecture.extension.capitalise
import com.rahul.kotlin.architecture.extension.isInflated
import com.rahul.kotlin.architecture.extension.runSafe
import com.rahul.kotlin.architecture.ui.widget.StateLayout
import java.util.Locale

/**
 * Base state provider that provides boiler plate code and can be used with any layout.
 *
 * @param VB generic type of layout binding.
 *
 * @param inflater reference to layout inflater.
 * @param parent reference to parent view.
 * @param layoutResId layout resource id to be inflated.
 */
abstract class ViewStateProvider<VB : ViewDataBinding>(
    inflater: LayoutInflater,
    parent: ViewGroup,
    @LayoutRes layoutResId: Int,
) : StateLayout.StateProvider {

    protected val binding: VB = DataBindingUtil.inflate(
        inflater, layoutResId, parent, true,
    )

    override fun detach(parent: ViewGroup) {
        parent.removeView(binding.root)
    }

    override fun isVisible(state: StateLayout.State): Boolean {
        return binding.root.isVisible
    }

    @CallSuper
    override fun show(state: StateLayout.State) {
        binding.root.isVisible = true
        // Bring view to front so that its visibility is not blocked by any other view.
        binding.root.bringToFront()
    }

    @CallSuper
    override fun hide(state: StateLayout.State) {
        binding.root.isVisible = false
    }
}

/**
 * Base state provider that enforces using a [ViewStub] for efficient loading of views.
 *
 * @param VB generic type of layout binding.
 *
 * @param inflater reference to layout inflater.
 * @param parent reference to parent view.
 * @param layoutResId layout resource id to be inflated.
 * @param state state to bind this provider with.
 */
abstract class ViewStubStateProvider<VB : ViewDataBinding>(
    inflater: LayoutInflater,
    parent: ViewGroup,
    @LayoutRes layoutResId: Int,
    state: StateLayout.State,
) : StateLayout.StateProvider, ViewStub.OnInflateListener {

    private val viewStub: ViewStub = createViewStub(inflater, parent, layoutResId, state)
    protected lateinit var binding: VB

    override fun detach(parent: ViewGroup) {
        parent.removeView(
            if (viewStub.isInflated) binding.root
            else viewStub
        )
    }

    override fun onInflate(viewStub: ViewStub?, view: View?) {
        binding = DataBindingUtil.bind(view!!)!!
    }

    override fun isVisible(state: StateLayout.State): Boolean {
        return viewStub.isInflated && binding.root.isVisible
    }

    @CallSuper
    override fun show(state: StateLayout.State) {
        if (viewStub.isInflated) {
            binding.root.isVisible = true
        } else {
            viewStub.inflate()
        }
        // Bring view to front so that its visibility is not blocked by any other view.
        binding.root.bringToFront()
    }

    @CallSuper
    override fun hide(state: StateLayout.State) {
        if (viewStub.isInflated) {
            binding.root.isVisible = false
        }
    }
}

/**
 * Extension function to inflate and bind state layouts wrapped in [ViewStub].
 */
fun ViewStubStateProvider<*>.createViewStub(
    inflater: LayoutInflater,
    parent: ViewGroup,
    resId: Int,
    state: StateLayout.State,
): ViewStub {
    val root: View = inflater.inflate(resId, parent, false)
    if (root !is ViewStub) {
        throw RuntimeException(
            "Cannot cast ${root::class.simpleName} to ViewStub for " +
                    "${state.name.capitalise(Locale.ENGLISH)}StateLayout. " +
                    "State layouts must be wrapped in ViewStub for increased performance."
        )
    }
    parent.addView(root)
    root.setOnInflateListener(this)
    return root
}

/**
 * Default progress state provider to show consistent loading state throughout the app.
 */
class ProgressStateProvider<VB : ViewDataBinding>(
    inflater: LayoutInflater, parent: ViewGroup, @LayoutRes layoutResId: Int,
) : ViewStateProvider<VB>(
    inflater, parent, layoutResId,
)

/**
 * Default empty state provider that provides option to override image and text field values using
 * xml attributes as well as programmatically.
 */
class EmptyStateProvider<VB : ViewDataBinding>(
    inflater: LayoutInflater, parent: ViewGroup, @LayoutRes layoutResId: Int,
) : ViewStubStateProvider<VB>(
    inflater, parent, layoutResId, StateLayout.State.EMPTY
) {
    private var emptyStateImageDrawable: Drawable? = null
    private var emptyStateText: String? = null
    private var emptyStateSubtext: String? = null

    /**
     * Loads attributes from view xml.
     */
    fun loadXMLAttributes(typedArray: TypedArray) {
        runSafe {
            emptyStateImageDrawable = typedArray.getDrawable(
                R.styleable.StateLayout_emptyState_imageResource
            )
            emptyStateText = typedArray.getString(R.styleable.StateLayout_emptyState_titleText)
            emptyStateSubtext = typedArray.getString(
                R.styleable.StateLayout_emptyState_descriptionText
            )
        }
    }

    override fun show(state: StateLayout.State) {
        super.show(state)
        when (state) {
            StateLayout.State.CONNECTIVITY -> {
                (binding as? ViewEmptyStateBinding)?.apply {
                    emptyStateTitleView.text = ""
                    emptyStateDescriptionView.setText(R.string.error_message_connectivity)
                    emptyStateTitleView.isVisible = false
                    emptyStateDescriptionView.isVisible = true
                    emptyStateImageView.setImageResource(R.drawable.img_connectivity_error)
                }
                (binding as? ViewEmptyStateTvBinding)?.apply {

                }
            }
            StateLayout.State.EMPTY -> {
                (binding as? ViewEmptyStateBinding)?.apply {
                    emptyStateTitleView.text = emptyStateText
                    emptyStateTitleView.isVisible = !emptyStateText.isNullOrBlank()
                    emptyStateDescriptionView.text = emptyStateSubtext
                    emptyStateDescriptionView.isVisible =
                        !emptyStateSubtext.isNullOrBlank()
                    if (emptyStateImageDrawable != null) {
                        emptyStateImageView.setImageDrawable(emptyStateImageDrawable)
                    } else {
                        emptyStateImageView.setImageResource(R.drawable.img_empty_content)
                    }
                }
                (binding as? ViewEmptyStateTvBinding)?.apply {

                }
            }
            else -> throw RuntimeException("${state.name} not supported by ${this::class.simpleName}")
        }
    }
}

/**
 * Skeleton state provider exposes option to add skeleton loading to any view. It simplifies
 * setting up with [RecyclerView] and adding multiple views to enable skeleton loading on them.
 */
class SkeletonStateProvider : StateLayout.StateProvider {
    private val skeletons: MutableList<Pair<View, Skeleton>> by lazy { mutableListOf() }
    override fun isVisible(state: StateLayout.State): Boolean = skeletons.any {
        it.second.isSkeleton()
    }

    override fun show(state: StateLayout.State) = skeletons.forEach {
        it.second.showSkeleton()
    }

    override fun hide(state: StateLayout.State) = skeletons.forEach {
        it.second.showOriginal()
    }

    override fun detach(parent: ViewGroup) {
        skeletons.forEach { it.second.showOriginal() }
        skeletons.clear()
    }

    /**
     * Adds view to list of skeletons
     */
    fun addSkeletonView(view: View, skeleton: Skeleton = view.createSkeleton()) {
        skeletons.add(view to skeleton)
    }

    /**
     * Same as addSkeletonView; but clears the previously added skeletons before adding new ones.
     */
    fun setSkeletonView(view: View, skeleton: Skeleton = view.createSkeleton()) {
        skeletons.clear()
        skeletons.add(view to skeleton)
    }

    /**
     * Same as setSkeletonView for view; but provides additional options to simplify skeleton
     * views for RecyclerViews.
     */
    fun setSkeletonView(
        view: RecyclerView,
        @LayoutRes layoutResId: Int,
        itemCount: Int = DEFAULT_LIST_SKELETON_ITEM_COUNT,
    ) {
        skeletons.clear()
        skeletons.add(view to view.applySkeleton(layoutResId, itemCount))
    }

    companion object {
        private const val DEFAULT_LIST_SKELETON_ITEM_COUNT = 10
    }
}

/**
 * Extension method to get skeleton state provider in a simpler way.
 */
fun StateLayout.getSkeletonStateProvider(): SkeletonStateProvider {
    return getStateProvider(StateLayout.State.SKELETON) as SkeletonStateProvider
}

/**
 * Default data state provider that provides support for appearing/disappearing of content based
 * on the root class. e.g. list views are automatically hidden for empty states.
 */
class DefaultDataStateProvider : StateLayout.StateProvider {
    internal var contentView: View? = null
    internal var successView: View? = null
    internal var failureView: View? = null

    internal var showProgressOverlay: Boolean = true

    /**
     * Finds the best match from available views for provided state
     */
    private fun getRootView(state: StateLayout.State): View? = when (state) {
        StateLayout.State.SUCCESS -> successView
        StateLayout.State.FAILURE -> failureView
        else -> contentView
    }

    override fun detach(parent: ViewGroup) {
        // Do nothing
    }

    override fun shouldBeVisible(
        currentState: StateLayout.State,
        targetState: StateLayout.State,
        targetStateProvider: StateLayout.StateProvider?,
    ): Boolean {
        // Ideal behaviour of showing data layout satisfying requirements in most cases;
        // Data state may fake visibility if needed. The snippet assures that only one view should
        // be visible at one time, keeping in mind that one or may refer to the same views in layout
        return when (currentState) {
            StateLayout.State.SUCCESS,
            StateLayout.State.FAILURE,
            -> currentState == targetState && getRootView(currentState) != null
            else -> when (targetState) {
                StateLayout.State.EMPTY,
                StateLayout.State.CONNECTIVITY,
                -> targetStateProvider == null
                StateLayout.State.SUCCESS,
                StateLayout.State.FAILURE,
                -> getRootView(targetState).let { it == null || it == contentView }
                StateLayout.State.PROGRESS -> showProgressOverlay
                else -> true
            }
        }
    }

    override fun isVisible(state: StateLayout.State): Boolean {
        // Check visibility only for view matching with state
        return getRootView(state)?.isVisible == true
    }

    override fun show(state: StateLayout.State) {
        // Show only matching view
        getRootView(state)?.apply {
            isVisible = true
            // Bring view to front so that its visibility is not blocked by any other view.
            bringToFront()
        }
    }

    override fun hide(state: StateLayout.State) {
        // Hide only matching view
        getRootView(state)?.isVisible = false
    }
}
