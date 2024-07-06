package com.example.beehive.ui.password.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.beehive.ui.Dimensions.MediumPadding

@Composable
fun LengthSlider(
    length: Int,
    onLengthChange: (Float) -> Unit
) {
    Slider(
        value = length.toFloat(),
        onValueChange = onLengthChange,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.tertiary,
            activeTrackColor = MaterialTheme.colorScheme.tertiary,
            inactiveTrackColor = MaterialTheme.colorScheme.tertiary,
        ),
        valueRange = 1f..100f,
        modifier = Modifier.padding(horizontal = MediumPadding)
    )
}

