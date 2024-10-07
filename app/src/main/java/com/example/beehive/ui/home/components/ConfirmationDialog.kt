package com.example.beehive.ui.home.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.beehive.R
import com.example.beehive.ui.common.BeehiveTextButton

@Composable
fun ConfirmationDialog(
    message: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    title: String? = null,
) {
    AlertDialog(onDismissRequest = { },
        title = { if (title != null) Text(title) },
        text = { Text(message) },
        dismissButton = {
            BeehiveTextButton(text = stringResource(R.string.cancel), onClick = onCancel)
        },
        confirmButton = {
            BeehiveTextButton(text = stringResource(R.string.confirm), onClick = onConfirm)
        })
}