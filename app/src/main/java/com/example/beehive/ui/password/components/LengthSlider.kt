package com.example.beehive.ui.password.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.beehive.ui.Dimensions.LargePadding

@Composable
fun LengthSlider(
    length: Int,
    onLengthChange: (Float) -> Unit,
) {
    Slider(
        value = length.toFloat(),
        onValueChange = onLengthChange,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.tertiary,
            activeTrackColor = MaterialTheme.colorScheme.tertiary,
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent
        ),
        steps = 99,
        valueRange = 1f..100f,
        modifier = Modifier.padding(horizontal = LargePadding)
    )
}

