package com.example.beehive.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.beehive.data.users.User
import com.example.beehive.ui.Dimensions.ExtraSmallPadding
import com.example.beehive.ui.Dimensions.UserIconSize

@Composable
fun UserButton(
    user: User,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.textButtonColors(
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.onSurface // Set content color directly
        ),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(ExtraSmallPadding)
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = user.email,
                tint = contentColor,
                modifier = Modifier.size(UserIconSize)
            )
            if (backgroundColor == Color.Transparent)
                Text(
                    text = user.email.split("@")[0],
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Clip
                )
        }
    }
}