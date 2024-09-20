package com.example.beehive.utils

import android.content.Context
import android.content.res.Configuration

fun isDarkMode(context: Context): Boolean {
    return context.resources?.configuration?.uiMode?.and(
        Configuration.UI_MODE_NIGHT_MASK
    ) == Configuration.UI_MODE_NIGHT_YES
}