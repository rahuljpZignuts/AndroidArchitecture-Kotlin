package com.test.rahul.extension

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

fun RecyclerView.disableItemAnimator() {
    itemAnimator?.changeDuration = 0
    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}
