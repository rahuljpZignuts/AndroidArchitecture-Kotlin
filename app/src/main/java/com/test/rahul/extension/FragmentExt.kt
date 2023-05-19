package com.test.rahul.extension

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.test.rahul.data.enums.DEFAULT_TRANSITION_ANIM
import com.test.rahul.data.enums.TransitionAnim
import com.test.rahul.utils.FileUtils
import timber.log.Timber
import java.io.File

fun <T> Fragment.launchActivity(
    destination: Class<T>,
    flags: Int? = null,
    anim: TransitionAnim? = DEFAULT_TRANSITION_ANIM,
    extras: Bundle.() -> Unit = {},
) {
    requireActivity().launchActivity(destination, flags, anim, extras)
}

fun Fragment.launchIntent(
    intent: Intent,
    transitionAnim: TransitionAnim? = DEFAULT_TRANSITION_ANIM,
) {
    startActivity(intent)
    transitionAnim?.let { activity?.overridePendingTransition(it) }
}

fun Fragment.openImageChooserIntent(
    imageUri: Uri,
    requestLauncher: ActivityResultLauncher<Intent>,
) {
    val intents = mutableListOf<Intent>()
    val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
        it.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
    }
    addIntentToList(requireContext(), intents, pickIntent)
    addIntentToList(requireContext(), intents, captureIntent)
    if (intents.isNotEmpty()) {
        val chooserIntent = Intent.createChooser(intents.removeAt(intents.lastIndex), "")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())
        chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        requestLauncher.launch(chooserIntent)
    }
}

@SuppressLint("QueryPermissionsNeeded")
private fun addIntentToList(context: Context, list: MutableList<Intent>, intent: Intent) {
    val resInfo = context.queryIntentActivities(intent, 0)
    for (ri in resInfo) {
        list.add(Intent(intent).also { it.setPackage(ri.activityInfo.packageName) })
    }
}


fun Fragment.openExternalURI(uriString: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uriString)))
    } catch (ex: ActivityNotFoundException) {
        Timber.e(ex)
    }
}

fun Fragment.openFilePickerIntent(
    chooserTitle: String = "",
    supportedTypes: Array<String> = emptyArray(),
    requestLauncher: ActivityResultLauncher<Intent>,
    onTriggered: (File, File) -> Unit = { _, _ -> },
) {
    val intents = mutableListOf<Intent>()
    val context = requireContext()
    //to create file and get Uri
    val getExternalFile: (String) -> File = { ext ->
        val externalFileName = FileUtils.generateFileName(ext)
        context.createFile(FileUtils.EXTERNAL_FILES_DIRECTORY, externalFileName)
    }

    //intents
    val pickFileIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "*/*"
        if (supportedTypes.isNotEmpty()) {
            putExtra(Intent.EXTRA_MIME_TYPES, supportedTypes)
        }
    }
    val imageFile = getExternalFile("jpg")
    val captureImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        putExtra(MediaStore.EXTRA_OUTPUT, imageFile.toUri(context))
    }
    val videoFile = getExternalFile("mp4")
    val captureVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
        putExtra(MediaStore.EXTRA_OUTPUT, videoFile.toUri(context))
    }
    addIntentToList(context, intents, captureImageIntent)
    addIntentToList(context, intents, captureVideoIntent)
    //chooser
    if (intents.isNotEmpty()) {
        val chooserIntent = Intent.createChooser(pickFileIntent, chooserTitle).apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        requestLauncher.launch(chooserIntent)
        onTriggered.invoke(imageFile, videoFile)
    }
}

fun Fragment.onBackPressed() {
    activity?.onBackPressedDispatcher?.onBackPressed()
}

fun Fragment.addBackPressedCallback(
    lifeCycleOwner: LifecycleOwner = viewLifecycleOwner,
    onBackPress: () -> Unit = {},
) {
    activity?.onBackPressedDispatcher?.addCallback(lifeCycleOwner) { onBackPress.invoke() }
}

fun Fragment.addBackPressedCallback(
    lifeCycleOwner: LifecycleOwner = viewLifecycleOwner,
    callback: OnBackPressedCallback,
) {
    activity?.onBackPressedDispatcher?.addCallback(lifeCycleOwner, callback)
}
