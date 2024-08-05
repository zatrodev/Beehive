package com.example.beehive.ui.password.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.example.beehive.ui.Dimensions.ExtraSmallPadding
import com.example.beehive.ui.Dimensions.InstalledAppIconSize
import kotlin.math.roundToInt

@Composable
fun InstalledAppCard(
    appName: String,
    appIcon: Drawable,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
            Box(
                modifier = Modifier
                    .requiredSize(InstalledAppIconSize)
                    .drawBehind {
                        drawIntoCanvas { canvas ->
                            appIcon.let {
                                it.setBounds(
                                    0,
                                    0,
                                    size.width.roundToInt(),
                                    size.height.roundToInt()
                                )
                                it.draw(canvas.nativeCanvas)
                            }
                        }
                    })
            Text(text = appName, modifier = modifier, style = MaterialTheme.typography.labelMedium)
        }
    }
}