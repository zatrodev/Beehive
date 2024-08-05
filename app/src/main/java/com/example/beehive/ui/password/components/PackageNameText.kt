package com.example.beehive.ui.password.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import com.example.beehive.R

@Composable
fun PackageNameText(packageName: String) {
    Text(
        text = packageName.ifEmpty { stringResource(R.string.package_name_placeholder) },
        style = MaterialTheme.typography.labelLarge.copy(
            color = MaterialTheme.colorScheme.tertiary,
            fontStyle = FontStyle.Italic
        )
    )
}