package com.example.beehive.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.beehive.ui.Dimensions.CheckIconSize

@Composable
fun SelectedIndicator(
    selected: Boolean,
) {
    Badge(
        containerColor = if (selected) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier.size(CheckIconSize)
            )
        }
    }
}