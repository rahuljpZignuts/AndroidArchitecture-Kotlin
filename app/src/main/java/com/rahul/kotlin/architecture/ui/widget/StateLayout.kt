package com.rahul.kotlin.architecture.ui.widget

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import com.rahul.kotlin.architecture.R
import com.rahul.kotlin.architecture.data.enums.RequestState
import com.rahul.kotlin.architecture.databinding.ViewEmptyStateBinding
import com.rahul.kotlin.architecture.databinding.ViewEmptyStateTvBinding
import com.rahul.kotlin.architecture.state.StateBus
import com.rahul.kotlin.architecture.ui.widget.helper.DefaultDataStateProvider
import com.rahul.kotlin.architecture.ui.widget.helper.EmptyStateProvider
import com.rahul.kotlin.architecture.ui.widget.helper.ProgressStateProvider
import com.rahul.kotlin.architecture.ui.widget.helper.SkeletonStateProvider

/**
 * State layout is a wrapper layout that makes switching between different states easier by
 * providing boiler plate code and simple observers on [RequestState].
 */
class StateLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private val stateProviders: MutableMap<State, StateProvider> = mutableMapOf()
    private val behaveAsOverlay: Boolean
    private val defaultStateProvider: DefaultDataStateProvider?
    private var isInInitMode = true
    private var isStateLocked = false

    /**
     * Creates default mapping most commonly used throughout the app.
     */
    private val defaultStateMapper = { state: RequestState? ->
        when (state) {
            RequestState.SUCCESS -> State.SUCCESS
            RequestState.FAILURE,
            RequestState.INVALID,
            -> State.FAILURE
            RequestState.FETCHING -> State.SKELETON
            RequestState.IN_PROGRESS -> State.PROGRESS
            RequestState.EMPTY -> State.EMPTY
            RequestState.NETWORK_ERROR -> State.CONNECTIVITY
            else -> State.DATA
        }
    }

    /**
     * State listener to received callbacks on state and UI updates. Called with pair of state
     * to its visibility whenever it is changed.
     */
    private val _stateVisibilityObservable: MutableLiveData<Pair<State, Boolean>> =
        MutableLiveData()
    val stateVisibilityObservable: LiveData<Pair<State, Boolean>> = _stateVisibilityObservable

    init {
        val inflater = LayoutInflater.from(context)
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.StateLayout, 0, 0
        ).apply {
            try {
                val emptyStateProvider by lazy {
                    if (StateBus.isTvDevice) {
                        EmptyStateProvider<ViewEmptyStateTvBinding>(
                            inflater,
                            this@StateLayout,
                            getResourceId(
                                R.styleable.StateLayout_emptyState_layoutResource,
                                R.layout.stub_empty_state_tv,
                            ),
                        ).also { it.loadXMLAttributes(this) }
                    } else {
                        EmptyStateProvider<ViewEmptyStateBinding>(
                            inflater,
                            this@StateLayout,
                            getResourceId(
                                R.styleable.StateLayout_emptyState_layoutResource,
                                R.layout.stub_empty_state,
                            ),
                        ).also { it.loadXMLAttributes(this) }
                    }
                }
                behaveAsOverlay = getBoolean(R.styleable.StateLayout_behaviour_asOverlay, false)
                if (getBoolean(R.styleable.StateLayout_default_stateProvider, true)) {
                    defaultStateProvider = DefaultDataStateProvider()
                    defaultStateProvider.showProgressOverlay =
                        getBoolean(R.styleable.StateLayout_shouldProgressOverlay, true)
                    stateProviders[State.DATA] = defaultStateProvider
                    stateProviders[State.SUCCESS] = defaultStateProvider
                    stateProviders[State.FAILURE] = defaultStateProvider
                } else defaultStateProvider = null
                val defaultEmptyState =
                    getBoolean(R.styleable.StateLayout_default_emptyState, false)
                if (defaultEmptyState) {
                    stateProviders[State.EMPTY] = emptyStateProvider
                }
                if (getBoolean(
                        R.styleable.StateLayout_default_connectivityState,
                        defaultEmptyState
                    )
                ) {
                    stateProviders[State.CONNECTIVITY] =
                        stateProviders[State.EMPTY] ?: emptyStateProvider
                }
                if (getBoolean(R.styleable.StateLayout_default_progressState, true)) {
                    stateProviders[State.PROGRESS] = if (StateBus.isTvDevice) {
                        ProgressStateProvider<ViewEmptyStateTvBinding>(
                            inflater,
                            this@StateLayout,
                            getResourceId(
                                R.styleable.StateLayout_progressState_layoutResource,
                                R.layout.view_progress_circular_overlay_tv,
                            ),
                        )
                    } else {
                        ProgressStateProvider<ViewEmptyStateBinding>(
                            inflater,
                            this@StateLayout,
                            getResourceId(
                                R.styleable.StateLayout_progressState_layoutResource,
                                R.layout.view_progress_circular_overlay,
                            ),
                        )
                    }
                    if (isInEditMode) {
                        (stateProviders[State.PROGRESS] as? ProgressStateProvider<*>)?.hide(State.PROGRESS)
                    }
                }
                if (getBoolean(R.styleable.StateLayout_default_skeletonState, false)) {
                    stateProviders[State.SKELETON] = SkeletonStateProvider()
                }
            } finally {
                recycle()
            }
        }
        layoutTransition = LayoutTransition()
        isInInitMode = false
    }

    /**
     * Changes the state visibility only if it is needed by comparing the provided values.
     *
     * @param currentState the state actually visible to user.
     * @param targetState the state that should be visible to user.
     * @return true if the expected state is now visible to user; false if the provider doesn't
     * exist for the requested state.
     */
    private fun setStateVisibility(currentState: State, targetState: State): Boolean {
        val stateProvider: StateProvider = stateProviders[currentState] ?: return false
        val visible: Boolean = stateProvider.isVisible(currentState)
        if (visible xor stateProvider.shouldBeVisible(
                currentState, targetState, stateProviders[targetState]
            )
        ) {
            if (visible) stateProvider.hide(currentState)
            else stateProvider.show(currentState)
            _stateVisibilityObservable.value = currentState to !visible
        }
        return visible
    }

    /**
     * Update states visibility based on provided state, can be used to updates states without
     * observers.
     *
     * @param state expected state to show the user.
     */
    fun setState(state: State, locked: Boolean = false) {
        setStateInternal(state, locked)
    }

    /**
     * Set states and self visibility based on the expected state and other view attributes.
     *
     * @param state expected state to show the user.
     */
    private fun setStateInternal(state: State, locked: Boolean) {
        synchronized(state) {
            if (isStateLocked) return
            isVisible = State.values()
                .map { setStateVisibility(it, state) }
                .any { it } || !behaveAsOverlay
            isStateLocked = locked
        }
    }

    /**
     * Setups this layout with request state by observing the data within the provided lifecycle
     * owner. This should be done once within the lifetime of the view as it adds required
     * observers by mapping [RequestState] to [State] and handle future updates automatically.
     *
     * @param lifecycleOwner reference to lifecycle owner to avoid unexpected crashes.
     * @param stateObservable observable to request state so that view state can be maintained in
     * synchronization to its data.
     */
    fun setupWithRequestState(
        lifecycleOwner: LifecycleOwner,
        stateObservable: LiveData<RequestState>,
        transformer: (RequestState) -> State = defaultStateMapper,
    ) = stateObservable.distinctUntilChanged().observe(
        lifecycleOwner
    ) { state -> setStateInternal(transformer(state), locked = false) }

    /**
     * Get specified state provider to provide any option to customize default providers at runtime.
     *
     * @param state to get provider for.
     * @return desired state provider if available; null otherwise.
     */
    fun getStateProvider(state: State): StateProvider? {
        return stateProviders[state]
    }

    /**
     * Sets state provider for the provided state for customization.
     *
     * @param state to set provider for.
     * @param provider to be used for the provided state.
     */
    fun setStateProvider(state: State, provider: StateProvider?) {
        stateProviders[state]?.detach(this)
        if (provider == null) stateProviders.remove(state)
        else stateProviders[state] = provider
    }

    /**
     * State enum to identify each state independently.
     */
    enum class State {
        /**
         * Connectivity state implies that action or request could not be completed because of
         * network unavailability.
         */
        CONNECTIVITY,

        /**
         * Data state shows that everything is loaded successfully and original content should be
         * shown.
         */
        DATA,

        /**
         * Success state refers to state when there is completion state to show.
         */
        SUCCESS,

        /**
         * Failure state refers to state when the requested action could not be completed.
         */
        FAILURE,

        /**
         * Empty state refers to state when there is no content to show.
         */
        EMPTY,

        /**
         * Progress state indicates that action or request is loading.
         */
        PROGRESS,

        /**
         * Skeleton state is list specific [PROGRESS] state for smoother experience.
         */
        SKELETON;
    }

    /**
     * State provider provides option to customize UI for any corresponding state with a few
     * simple methods implementation.
     */
    interface StateProvider {
        /**
         * Called when provider is being detached to replace with another provider. It should
         * remove any views added only by this provider to the parent.
         *
         * @param parent reference to parent container view.
         */
        fun detach(parent: ViewGroup)

        /**
         * Checks if the state should be tied up with another state to give more flexibility for
         * handling different states e.g. Skeleton state alone may be useless without data state.
         *
         * @param currentState the state actually visible to user.
         * @param targetState the state that should be visible to user.
         * @param targetStateProvider provider attached for [targetState].
         * @return true if the group should be visible for target state; false otherwise.
         */
        fun shouldBeVisible(
            currentState: State,
            targetState: State,
            targetStateProvider: StateProvider?,
        ): Boolean {
            return currentState == targetState
        }

        /**
         * Indicates if the UI for this provider is visible or not. This can be used for
         * optimizations and state checks to validate the right UI.
         *
         * @param state to show the visibility for.
         */
        fun isVisible(state: State): Boolean

        /**
         * Request to show UI for desired state. This provides option to use single provider for
         * multiple state.
         *
         * @param state to show the UI for.
         */
        fun show(state: State)

        /**
         * Request to hide UI for specified state. This provides option to use single provider for
         * multiple state.
         *
         * @param state to hide the UI for.
         */
        fun hide(state: State)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        val states = (params as? StateLayoutParams)?.states ?: emptyList()
        if (states.isEmpty()) {
            if (!isInInitMode && defaultStateProvider?.contentView == null) {
                defaultStateProvider?.contentView = child
            }
        } else {
            if (states.contains(State.DATA)) defaultStateProvider?.contentView = child
            if (states.contains(State.SUCCESS)) defaultStateProvider?.successView = child
            if (states.contains(State.FAILURE)) defaultStateProvider?.failureView = child
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return attrs?.let { StateLayoutParams(context, it) } ?: super.generateLayoutParams(attrs)
    }

    private class StateLayoutParams(
        context: Context, attrs: AttributeSet,
    ) : LayoutParams(context, attrs) {
        val states = mutableListOf<State>()

        init {
            context.obtainStyledAttributes(attrs, R.styleable.StateLayout_Layout).apply {
                try {
                    if (getBoolean(R.styleable.StateLayout_Layout_layout_state_content, false)) {
                        states.add(State.DATA)
                    }
                    if (getBoolean(R.styleable.StateLayout_Layout_layout_state_success, false)) {
                        states.add(State.SUCCESS)
                    }
                    if (getBoolean(R.styleable.StateLayout_Layout_layout_state_failure, false)) {
                        states.add(State.FAILURE)
                    }
                } finally {
                    recycle()
                }
            }
        }
    }

    fun <T> setEmptyState(list: List<T?>) {
        setState(if (list.isEmpty()) State.EMPTY else State.SUCCESS)
    }
}
