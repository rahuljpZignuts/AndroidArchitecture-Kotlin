package com.rahul.kotlin.architecture.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.SystemClock
import android.util.LayoutDirection
import android.view.KeyEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewStub
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.snackbar.Snackbar
import kotlin.math.hypot
import kotlin.math.roundToInt

const val CONFIG_ANIM_TIME_SHORT = 200L
const val CONFIG_ANIM_TIME_MEDIUM = 400L
const val CONFIG_ANIM_TIME_LONG = 500L
const val CONFIG_ANIM_TIME_DELAYED = 1_000L

val ViewStub.isInflated: Boolean get() = (parent == null)

val View.isViewDirectionRTL: Boolean
    get() = layoutDirection == LayoutDirection.RTL

fun View.fadeVisibility(visible: Boolean, duration: Long = CONFIG_ANIM_TIME_DELAYED) {
    if (isVisible == visible) return
    if (visible) fadeIn(duration = duration)
    else fadeOut(duration = duration)
}

fun View.slideVisibility(visible: Boolean, duration: Long = CONFIG_ANIM_TIME_DELAYED) {
    if (isVisible == visible) return
    if (visible) slideIn(duration = duration)
    else slideOut(duration = duration)
}

fun View.slideIn(duration: Long) {
    if (isVisible) return
    visibility = View.VISIBLE
    AnimatorSet().also {
        it.playTogether(
            ObjectAnimator.ofFloat(this, View.TRANSLATION_X, width.toFloat(), 0f),
            ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f)
        )
        it.duration = duration
        it.start()
    }
}

fun View.slideOut(duration: Long) {
    if (!isVisible) return
    AnimatorSet().also {
        it.playTogether(
            ObjectAnimator.ofFloat(this, View.TRANSLATION_X, 0f, width.toFloat().unaryMinus()),
            ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f)
        )
        it.duration = duration
        it.doOnEnd {
            visibility = View.GONE
        }
        it.start()
    }
}

fun View.fadeIn(duration: Long) {
    if (isVisible) return
    alpha = 0F
    visibility = View.VISIBLE
    animate().alpha(1F)
        .setDuration(duration)
        .setListener(null)
        .start()
}

fun View.fadeOut(duration: Long) {
    if (!isVisible) return
    animate()
        .alpha(0F)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                visibility = View.GONE
            }
        })
        .start()
}

fun View.circularRevealVisibility(visible: Boolean) {
    if (isVisible == visible) return
    if (visible) circularRevealShow()
    else circularRevealHide()
}

fun View.circularRevealShow() {
    if (isVisible) return
    // get the center for the clipping circle
    val cx = width / 2
    val cy = height / 2
    // get the final radius for the clipping circle
    val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
    // create the animator for this view (the start radius is zero)
    val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0f, finalRadius)
    // make the view visible and start the animation
    visibility = View.VISIBLE
    anim.start()
}

fun View.circularRevealHide() {
    if (!isVisible) return
    // get the center for the clipping circle
    val cx = width / 2
    val cy = height / 2
    // get the initial radius for the clipping circle
    val initialRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
    // create the animation (the final radius is zero)
    val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, initialRadius, 0f)
    // make the view invisible when the animation is done
    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            visibility = View.INVISIBLE
        }
    })
    // start the animation
    anim.start()
}

fun View.crossFade(targetView: View, duration: Long = CONFIG_ANIM_TIME_LONG) {
    android.R.integer.config_longAnimTime
    if (isVisible) {
        targetView.visibility = View.GONE
        return
    }
    // Set the main view to 0% opacity but visible, so that it is visible (but fully transparent)
    // during the animation
    alpha = 0f
    visibility = View.VISIBLE
    // Animate the content view to 100% opacity, and clear any animation
    // listener set on the view.
    animate()
        .alpha(1F)
        .setDuration(duration)
        .setListener(null)
    // Animate the target view to 0% opacity. After the animation ends, set its visibility to GONE
    // as an optimization step (it won't participate in layout passes, etc.)
    targetView.animate()
        .alpha(0F)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                targetView.visibility = View.GONE
            }
        })
}

fun View.setLayoutDirection(isRTL: Boolean) {
    layoutDirection = if (isRTL) View.LAYOUT_DIRECTION_RTL
    else View.LAYOUT_DIRECTION_LTR
}

fun View.setDimensionRatio(ratio: String) {
    updateLayoutParams<ConstraintLayout.LayoutParams> {
        dimensionRatio = ratio
    }
}

fun View.updateViewPaddings(start: Float, end: Float, top: Float, bottom: Float) {
    val scale = context.resources.displayMetrics.density
    val startPadding = (start.times(scale).plus(0.5f)).roundToInt()
    val endPadding = (end.times(scale).plus(0.5f)).roundToInt()
    val topPadding = (top.times(scale).plus(0.5f)).roundToInt()
    val bottomPadding = (bottom.times(scale).plus(0.5f)).roundToInt()
    setPaddingRelative(startPadding, topPadding, endPadding, bottomPadding)
}

fun View.showSnackBar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}

fun View.showActionSnackBar(
    message: String,
    actionTitle: String,
    actionCallback: (View) -> Unit,
) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(actionTitle, actionCallback)
        .show()
}

fun View.safeClickListener(debounceTime: Long = 1000L, action: (View) -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) {
                return
            }
            lastClickTime = SystemClock.elapsedRealtime()
            action(v)
        }
    })
}

fun View.safeClickListener(debounceTime: Long = 1000L, onClickListener: View.OnClickListener) {
    this.safeClickListener(debounceTime) { onClickListener.onClick(it) }
}

fun View.onDpadActionsListener(
    onLeft: () -> Boolean = { false },
    onRight: () -> Boolean = { false },
    onUp: () -> Boolean = { false },
    onDown: () -> Boolean = { false },
) {
    setOnKeyListener { _, _, event ->
        if (event.action == KeyEvent.ACTION_UP) {
            return@setOnKeyListener false
        }
        when (event.keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> onLeft()
            KeyEvent.KEYCODE_DPAD_RIGHT -> onRight()
            KeyEvent.KEYCODE_DPAD_UP -> onUp()
            KeyEvent.KEYCODE_DPAD_DOWN -> onDown()
            else -> false
        }
    }
}

fun View.rotate(
    finalValue: Float,
    animate: Boolean = true,
    animationDuration: Long = if (animate) 300L else 0L,
) {
    if (rotation == finalValue) return
    ObjectAnimator
        .ofFloat(this, View.ROTATION, rotation, finalValue)
        .setDuration(animationDuration)
        .start()
}
