package com.example.beehive.utils

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.WindowNode

val AssistStructure.windows: Sequence<WindowNode>
    get() = sequence {
        for (i in 0 until windowNodeCount) {
            yield(getWindowNodeAt(i))
        }
    }