package com.example.beehive.ui.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.ShadowElevation

@Composable
fun SearchBar(
    query: String,
    onValueChanged: (String) -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {}
) {
    Surface(
        shadowElevation = ShadowElevation,
        color = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(54.dp)
            .clip(CircleShape),

        ) {
        Row {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.padding(MediumPadding)
            )

            TextField(
                value = query,
                onValueChange = onValueChanged,
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                    focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onTertiary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.onFocusChanged {
                    onFocusChanged(it.isFocused)
                }
            )

        }
    }
}