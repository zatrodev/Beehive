package com.example.beehive.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import com.example.beehive.R

@Composable
fun ConditionalStyleText(
    text: String,
    fontSize: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.ifBlank { stringResource(R.string.no_username) },
        style = fontSize.copy(
            fontStyle = if (text.isBlank()) FontStyle.Italic else FontStyle.Normal,
            color = if (text.isBlank()) MaterialTheme.colorScheme.outlineVariant
            else color
        ),
        modifier = modifier
    )
}