package com.example.beehive.ui.credential.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun OptionRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    isEnabled: Boolean,
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(135.dp)) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = isEnabled,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primaryContainer,
                checkmarkColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}