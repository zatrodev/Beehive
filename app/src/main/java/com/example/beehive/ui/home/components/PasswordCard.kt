package com.example.beehive.ui.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.beehive.ui.Dimensions.IconSize
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.theme.BeehiveTheme

@Composable
fun PasswordCard(siteName: String, url: String, isHighlight: Boolean) {
    val size by animateDpAsState(if (isHighlight) 150.dp else 100.dp, label = "icon size")
    val siteIconSource =
        "https://t0.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=$url&size=256"

    Column(
        modifier = Modifier.fillMaxHeight(0.8f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(siteIconSource),
            contentDescription = siteName,
            modifier = Modifier
                .animateContentSize()
                .size(size)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = siteName, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun PasswordCard() {
    Box(
        modifier = Modifier
            .padding(SmallPadding)
            .background(color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = "Facebook",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.padding(SmallPadding)
        )
        Row {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    Icons.Filled.Edit, contentDescription = "Edit", modifier = Modifier.size(
                        IconSize
                    )
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    Icons.Filled.Delete, contentDescription = "Delete", modifier = Modifier.size(
                        IconSize
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewPasswordCard() {
    BeehiveTheme {
        PasswordCard()
    }
}