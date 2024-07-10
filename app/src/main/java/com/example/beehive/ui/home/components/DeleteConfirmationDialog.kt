package com.example.beehive.ui.home.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.beehive.R
import com.example.beehive.ui.common.BeehiveTextButton

@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit, onDeleteCancel: () -> Unit, modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        text = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier,
        dismissButton = {
            BeehiveTextButton(text = stringResource(R.string.no), onClick = onDeleteCancel)
        },
        confirmButton = {
            BeehiveTextButton(text = stringResource(R.string.yes), onClick = onDeleteConfirm)
        })
}