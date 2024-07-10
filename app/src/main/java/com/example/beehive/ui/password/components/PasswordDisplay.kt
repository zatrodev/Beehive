package com.example.beehive.ui.password.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .fillMaxWidth()
            .height(75.dp),
        shape = RoundedCornerShape(RoundedCornerShape),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Box {
            Text(
                text = password.length.toString(),
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = password,
                maxLines = 1,
                modifier = Modifier.padding(LargePadding),
                fontSize = fontSizeValue.sp,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}