package com.example.beehive.ui.credential.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.beehive.R
import com.example.beehive.data.user.User
import com.example.beehive.ui.Dimensions.ExtraSmallPadding
import com.example.beehive.ui.Dimensions.IconSize
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.PasswordCardHeight
import com.example.beehive.ui.Dimensions.RoundedCornerShape
import com.example.beehive.ui.Dimensions.ShadowElevation
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.ConditionalStyleText
import com.example.beehive.ui.common.DisplayPassword
import com.example.beehive.ui.home.components.ConfirmationDialog
import com.example.beehive.utils.isDarkMode

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun DeletedPasswordCard(
    id: Int,
    username: String,
    appName: String,
    user: User,
    remainingDays: Long,
    onDelete: (Int) -> Unit,
    onRestore: (Int) -> Unit,
) {
    var showPassword by remember { mutableStateOf(false) }
    var deleteConfirmation by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val cardWidth: Dp =
        ((LocalConfiguration.current.screenWidthDp.dp - MediumPadding - SmallPadding * 2) / 2)

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(RoundedCornerShape),
        modifier = Modifier
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) {
                showPassword = !showPassword
            }
            .width(cardWidth)
            .height(PasswordCardHeight)
            .padding(SmallPadding)
            .shadow(
                elevation = if (isDarkMode(LocalContext.current)) 0.dp else ShadowElevation,
                shape = RoundedCornerShape(RoundedCornerShape)
            )

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(MediumPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = user.email,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            ConditionalStyleText(
                text = username,
                fontSize = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(SmallPadding),
            modifier = Modifier
                .fillMaxWidth()
                .padding(ExtraSmallPadding)
        ) {
            Badge(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Text(
                    text = "$remainingDays days",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    onRestore(id)
                },
                modifier = Modifier.size(IconSize)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_restore),
                    contentDescription = "Restore",
                    modifier = Modifier.size(IconSize),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(
                onClick = { deleteConfirmation = true },
                modifier = Modifier.size(IconSize)
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(IconSize),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        AnimatedVisibility(
            visible = showPassword,
            enter = slideInVertically {
                with(density) { -100.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            DisplayPassword(password = appName)
        }
        if (deleteConfirmation) {
            ConfirmationDialog(
                title = stringResource(R.string.delete_title),
                message = stringResource(R.string.delete_from_trash_question),
                onConfirm = {
                    onDelete(id)
                    deleteConfirmation = false
                },
                onCancel = {
                    deleteConfirmation = false
                },
            )
        }
    }
}