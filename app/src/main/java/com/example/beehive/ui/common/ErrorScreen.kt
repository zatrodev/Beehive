package com.example.beehive.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.beehive.R
import com.example.beehive.ui.Dimensions.SmallPadding

@Composable
fun ErrorScreen(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = stringResource(R.string.an_error_has_occurred),
                modifier = Modifier.padding(SmallPadding)
            )
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
            )
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.retry_label))
            }
        }
    }
}