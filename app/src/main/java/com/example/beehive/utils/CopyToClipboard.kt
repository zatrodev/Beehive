package com.example.beehive.utils

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import android.widget.Toast

fun copyToClipboard(
    context: Context,
    text: String,
    isSensitive: Boolean = false,
) {
    val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(
        "",
        text
    )

    if (isSensitive)
        clipData.apply {
            description.extras = PersistableBundle().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
                } else {
                    putBoolean("android.content.extra.IS_SENSITIVE", true)
                }
            }
        }

    clipboardManager.setPrimaryClip(clipData)

    Toast.makeText(
        context,
        "Copied to clipboard",
        Toast.LENGTH_SHORT
    ).show()
}