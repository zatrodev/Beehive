package com.example.beehive.ui.password.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import com.example.beehive.ui.Dimensions.ExtraSmallPadding
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.RoundedCornerShape

@Composable
fun PasswordDisplay(
    password: String,
    onPasswordChange: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(start = LargePadding, end = LargePadding, bottom = LargePadding)
            .fillMaxWidth(),
        shape = RoundedCornerShape(RoundedCornerShape),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Box {
            Text(
                text = password.length.toString(),
                modifier = Modifier.padding(ExtraSmallPadding),
                color = MaterialTheme.colorScheme.onSurface
            )
            BasicTextField(
                value = password,
                onValueChange = onPasswordChange,
                maxLines = 1,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = LargePadding,
                        top = LargePadding,
                        end = LargePadding
                    ),
            )
        }
    }
}