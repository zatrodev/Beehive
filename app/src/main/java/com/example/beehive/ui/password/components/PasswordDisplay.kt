package com.example.beehive.ui.password.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.beehive.ui.Dimensions.ExtraSmallPadding
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.RoundedCornerShape

@Composable
fun PasswordDisplay(password: String) {
    val fontSizeValue by remember(password) {
        derivedStateOf {
            18f - (0.06f * (password.length))
        }
    }
    Surface(
        modifier = Modifier
            .padding(LargePadding)
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
            Text(
                text = password,
                maxLines = 1,
                modifier = Modifier.padding(
                    start = LargePadding,
                    top = LargePadding,
                    end = LargePadding
                ),
                fontSize = fontSizeValue.sp,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}