package com.rahul.kotlin.architecture.extension

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

fun RecyclerView.disableItemAnimator() {
    itemAnimator?.changeDuration = 0
    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}
