package com.rahul.kotlin.architecture.extension

import android.app.Dialog
import android.content.DialogInterface
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import com.rahul.kotlin.architecture.lifecycle.bo.StringResource
import com.rahul.kotlin.architecture.lifecycle.bo.getText

fun AlertDialog.Builder.setTitle(title: StringResource?) {
    title?.getText(context)?.let { setTitle(it) }
}

fun AlertDialog.Builder.setMessage(message: StringResource?) {
    message?.getText(context)?.let { setMessage(it) }
}

fun AlertDialog.Builder.setPositiveButton(
    buttonText: StringResource?,
    listener: DialogInterface.OnClickListener,
) {
    buttonText?.getText(context)?.let { setPositiveButton(it, listener) }
}

fun AlertDialog.Builder.setNegativeButton(
    buttonText: StringResource?,
    listener: DialogInterface.OnClickListener,
) {
    buttonText?.getText(context)?.let { setNegativeButton(it, listener) }
}

fun Dialog.setAnchorView(
    anchorViewLocation: IntArray?, anchorViewSize: IntArray?, width: Int, height: Int,
) {
    if (anchorViewLocation == null || anchorViewSize == null) {
        return
    }
    this.window?.let { window ->
        window.attributes = window.attributes?.apply {
            gravity = Gravity.TOP or Gravity.START
            x = ((anchorViewLocation[0] + anchorViewSize[0]) / 2) - width
            y = ((anchorViewLocation[1] + anchorViewSize[1]) / 2) - height
        }
    }
}
