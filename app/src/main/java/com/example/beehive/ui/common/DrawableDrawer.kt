package com.example.beehive.ui.common

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.example.beehive.ui.Dimensions.InstalledAppIconSize
import kotlin.math.roundToInt

@Composable
fun DrawableDrawer(
    icon: Drawable,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .requiredSize(InstalledAppIconSize)
            .drawBehind {
                drawIntoCanvas { canvas ->
                    icon.let {
                        it.setBounds(
                            0,
                            0,
                            size.width.roundToInt(),
                            size.height.roundToInt()
                        )
                        it.draw(canvas.nativeCanvas)
                    }
                }
            }
    )
}