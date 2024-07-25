package com.example.beehive.ui.password.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.beehive.R
import com.example.beehive.ui.Dimensions.IndicatorLineThickness
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.RoundedCornerShape

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameTextField(
    name: String,
    onNameChange: (String) -> Unit,
    isError: Boolean,
    showSearchDialog: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .padding(MediumPadding)
            .clip(RoundedCornerShape(RoundedCornerShape))
            .clickable(
                enabled = true,
                onClick = showSearchDialog
            )
    ) {
        TextField(
            value = name,
            onValueChange = onNameChange,
            maxLines = 1,
            colors = TextFieldDefaults.colors(
                disabledContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            ),
            enabled = false,
            label = {
                Text(
                    text = stringResource(R.string.name_placeholder),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier
                .indicatorLine(
                    enabled = true,
                    isError = isError,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                    ),
                    interactionSource = MutableInteractionSource(),
                    unfocusedIndicatorLineThickness = IndicatorLineThickness,
                ),

            )
        if (isError)
            Text(
                text = stringResource(R.string.error_message),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.error
                )
            )

    }

}
