package com.rahul.kotlin.architecture.extension

import android.content.res.ColorStateList
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadImage(url: String, @DrawableRes placeHolder: Int) {
    Glide.with(this).load(url)
        .apply(RequestOptions().circleCrop())
        .placeholder(placeHolder)
        .error(placeHolder)
        .into(this)
}

fun ImageView.loadImage(uri: Uri, @DrawableRes placeHolder: Int) {
    Glide.with(this).load(uri)
        .apply(RequestOptions().circleCrop())
        .placeholder(placeHolder)
        .error(placeHolder)
        .into(this)
}

fun ImageView.load(url: String, @DrawableRes placeHolder: Int) {
    Glide.with(this).load(url)
        .placeholder(placeHolder)
        .error(placeHolder)
        .into(this)
}

fun ImageView.setImageTint(color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

fun ImageView.loadWithVisibility(url: String?) {
    isVisible = !url.isNullOrEmpty()
    Glide.with(this)
        .load(url ?: "")
        .transition(withCrossFade())
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(this)
}
