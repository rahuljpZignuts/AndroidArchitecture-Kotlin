package com.test.rahul.data.enums

import androidx.annotation.AnimRes
import com.test.rahul.R

enum class TransitionAnim(
    @AnimRes val enterAnimLTR: Int,
    @AnimRes val exitAnimLTR: Int,
    @AnimRes val enterAnimRTL: Int,
    @AnimRes val exitAnimRTL: Int,
) {
    SLIDE_IN_START_OUT_END(
        R.anim.slide_in_start,
        R.anim.slide_out_end,
        R.anim.slide_in_end,
        R.anim.slide_out_start,
    ),
    SLIDE_IN_END_OUT_START(
        R.anim.slide_in_end,
        R.anim.slide_out_start,
        R.anim.slide_in_start,
        R.anim.slide_out_end,
    ),
    FADE_IN(
        android.R.anim.fade_in,
        android.R.anim.fade_out,
        android.R.anim.fade_in,
        android.R.anim.fade_out,
    ),
}

val DEFAULT_TRANSITION_ANIM = TransitionAnim.SLIDE_IN_START_OUT_END
