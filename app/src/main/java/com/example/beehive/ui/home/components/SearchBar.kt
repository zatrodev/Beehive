package com.example.beehive.ui.home.components

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import com.example.beehive.R
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.SmallPadding

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onValueChanged: (String) -> Unit = {},
) {
    val surfaceColor = MaterialTheme.colorScheme.tertiaryContainer
    val textColor = MaterialTheme.colorScheme.onTertiaryContainer

    Surface(
        color = surfaceColor,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = LargePadding,
                end = LargePadding,
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
                    start = MediumPadding,
                )
            )

            var isFocused by remember { mutableStateOf(false) }


            BasicTextField(
                value = query,
                onValueChange = onValueChanged,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = textColor
                ),
                cursorBrush = SolidColor(textColor),
                modifier = Modifier.onFocusChanged {
                    isFocused = it.isFocused
                }
            ) { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = query,
                    innerTextField = innerTextField,
                    singleLine = true,
                    enabled = true,
                    interactionSource = MutableInteractionSource(),
                    placeholder = {
                        if (!isFocused)
                            Text(
                                text = stringResource(R.string.search_bar_placeholder),
                                color = textColor,
                                style = MaterialTheme.typography.bodySmall
                            )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        focusedContainerColor = surfaceColor,
                        unfocusedContainerColor = surfaceColor,
                        focusedIndicatorColor = surfaceColor,
                        unfocusedIndicatorColor = surfaceColor,
                        unfocusedTextColor = textColor,
                        cursorColor = textColor,
                    ),
                    visualTransformation = VisualTransformation.None,
                    contentPadding = PaddingValues(SmallPadding)
                )
            }

        }
    }
}