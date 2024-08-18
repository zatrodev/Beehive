package com.example.beehive.ui.home.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.example.beehive.ui.Dimensions.InstalledAppIconSize
import com.example.beehive.ui.Dimensions.RoundedCornerShape
import com.example.beehive.ui.Dimensions.SmallPadding
import kotlin.math.roundToInt

@Composable
fun PasswordTile(
    name: String,
    uri: String,
    userId: Int,
    icon: Drawable?,
    onNavigateToViewPassword: (String, Int) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(RoundedCornerShape),
        modifier = Modifier
            .fillMaxWidth()
            .padding(SmallPadding)
            .clickable {
                onNavigateToViewPassword(uri, userId)
            }
    ) {
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            modifier = Modifier.padding(SmallPadding)
        ) {
            if (icon != null)
                Box(
                    modifier = Modifier
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
                        })
            else
                Icon(imageVector = Icons.Default.Person, contentDescription = null)

            Text(
                text = name,
                modifier = Modifier.padding(SmallPadding),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}