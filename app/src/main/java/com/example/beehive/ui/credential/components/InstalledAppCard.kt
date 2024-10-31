package com.example.beehive.ui.credential.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.example.beehive.ui.Dimensions.ExtraSmallPadding
import com.example.beehive.ui.Dimensions.InstalledAppIconSize

@Composable
fun InstalledAppCard(
    appName: String,
    appIcon: Bitmap,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier.clickable {
            onClick()
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(ExtraSmallPadding)
        ) {
            Image(
                bitmap = appIcon.asImageBitmap(), contentDescription = "", modifier = Modifier.size(
                    InstalledAppIconSize
                )
            )
            Text(text = appName, modifier = modifier, style = MaterialTheme.typography.labelMedium)
        }
    }
}