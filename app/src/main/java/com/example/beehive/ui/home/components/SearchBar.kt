package com.example.beehive.ui.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import com.example.beehive.R
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.SearchBarCornerShape

@Composable
fun SearchBar(
    query: String,
    onValueChanged: (String) -> Unit = {},
) {
    val surfaceColor = MaterialTheme.colorScheme.tertiaryContainer
    val textColor = MaterialTheme.colorScheme.onTertiaryContainer

    Surface(
        color = surfaceColor,
        shape = RoundedCornerShape(SearchBarCornerShape),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = LargePadding,
                end = LargePadding,
                bottom = LargePadding
            )

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = textColor,
                modifier = Modifier.padding(
                    start = LargePadding,
                )
            )

            var isFocused by remember { mutableStateOf(false) }

            TextField(
                value = query,
                onValueChange = onValueChanged,
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = surfaceColor,
                    unfocusedContainerColor = surfaceColor,
                    focusedIndicatorColor = surfaceColor,
                    unfocusedIndicatorColor = surfaceColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                placeholder = {
                    if (!isFocused) {
                        Text(
                            text = stringResource(R.string.search_bar_placeholder),
                            color = textColor
                        )
                    }
                },
                modifier = Modifier.onFocusChanged {
                    isFocused = it.isFocused
                }
            )

        }
    }
}