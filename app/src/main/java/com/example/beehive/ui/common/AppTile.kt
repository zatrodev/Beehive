package com.example.beehive.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.beehive.R
import com.example.beehive.ui.Dimensions.InstalledAppIconSize
import com.example.beehive.ui.Dimensions.SmallPadding


@Composable
fun AppTile(
    name: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    icon: Any? = null,
    onClick: (() -> Unit)? = null,
    onNavigateToViewPassword: ((String) -> Unit)? = null,
    packageName: String = "",
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .clickable {
                onClick?.invoke()
                onNavigateToViewPassword?.invoke(packageName)
            }
    ) {
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            modifier = Modifier.padding(SmallPadding)
        ) {
            when (icon) {
                is ImageBitmap -> Image(
                    bitmap = icon,
                    contentDescription = "app",
                    modifier = Modifier.size(InstalledAppIconSize)
                )

                is ImageVector -> Icon(
                    imageVector = icon,
                    contentDescription = "user",
                    tint = contentColor,
                )

                else -> Icon(
                    painterResource(R.drawable.ic_app),
                    contentDescription = "app",
                    modifier = Modifier.size(InstalledAppIconSize)
                )

            }

            if (name.isNotBlank())
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = SmallPadding)
                )
        }
    }
}