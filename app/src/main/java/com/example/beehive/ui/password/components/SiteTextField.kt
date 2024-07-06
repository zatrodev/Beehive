package com.example.beehive.ui.password.components

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.beehive.ui.Dimensions.IndicatorLineThickness
import com.example.beehive.ui.Dimensions.SmallPadding

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteTextField(
    site: String,
    onSiteChange: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .width(280.dp)
            .padding(SmallPadding)
    ) {

        TextField(
            value = site,
            onValueChange = onSiteChange,
            maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            ),
            label = {
                Text(
                    text = "Site", style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ), color = MaterialTheme.colorScheme.onSurface
                )
            },
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier
                .indicatorLine(
                    enabled = true,
                    isError = false,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                    ),
                    interactionSource = MutableInteractionSource(),
                    unfocusedIndicatorLineThickness = IndicatorLineThickness,
                ),

            )
    }

}
