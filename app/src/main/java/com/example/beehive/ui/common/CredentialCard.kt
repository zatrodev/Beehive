package com.example.beehive.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.beehive.R
import com.example.beehive.ui.Dimensions.ExtraSmallPadding
import com.example.beehive.ui.Dimensions.IconSize
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.PasswordCardHeight
import com.example.beehive.ui.Dimensions.RoundedCornerShape
import com.example.beehive.ui.Dimensions.ShadowElevation
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.home.components.ConfirmationDialog
import com.example.beehive.ui.navigation.SharedElementTransition
import com.example.beehive.utils.isDarkMode


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun CredentialCard(
    modifier: Modifier = Modifier,
    id: Int? = null,
    title: String,
    subtitle: String,
    icon: ImageBitmap? = null,
    password: String,
    userId: Int,
    showPassword: Boolean = false,
    isSharingMode: Boolean = false,
    enableShareMode: () -> Unit = {},
    addSelectedCredential: () -> Unit = {},
    removeSelectedCredential: () -> Unit = {},
    onDelete: (Int) -> Unit = {},
    onEdit: (Int, Int) -> Unit = { _: Int, _: Int -> },
    sharedElementTransition: SharedElementTransition,
) {
    var localShowPassword by remember { mutableStateOf(showPassword) }
    var deleteConfirmation by rememberSaveable { mutableStateOf(false) }
    var selected by remember { mutableStateOf(isSharingMode) }
    val isEditing = id == null
    val interactionSource = remember { MutableInteractionSource() }
    val density = LocalDensity.current

    val cardModifier = if (isEditing) modifier else modifier
        .height(PasswordCardHeight)
        .padding(SmallPadding)
        .combinedClickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                if (isSharingMode) {
                    if (selected)
                        removeSelectedCredential()
                    else
                        addSelectedCredential()

                    selected = !selected
                } else {
                    localShowPassword = !localShowPassword
                }
            },
            onLongClick = {
                if (isSharingMode)
                    return@combinedClickable

                addSelectedCredential()
                enableShareMode()
                selected = true
            },
        )

    LaunchedEffect(isSharingMode) {
        if (!isSharingMode) selected = false
    }

    with(sharedElementTransition.sharedTransitionScope) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.small,
            modifier = cardModifier
                .sharedElement(
                    sharedElementTransition.sharedTransitionScope.rememberSharedContentState(key = password),
                    animatedVisibilityScope = sharedElementTransition.animatedContentScope,
                )
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
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                ConditionalStyleText(
                    text = subtitle,
                    fontSize = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(ExtraSmallPadding),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ExtraSmallPadding)
            ) {
                Box {
                    icon?.let {
                        Image(
                            bitmap = it,
                            contentDescription = null,
                            modifier = Modifier.size(IconSize)
                        )
                    }
                    if (isSharingMode)
                        SelectedIndicator(selected = selected)
                }


                Spacer(modifier = Modifier.weight(1f))
                if (!isEditing) {
                    IconButton(
                        onClick = { onEdit(id!!, userId) },
                        modifier = Modifier.size(IconSize)
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(IconSize),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

            }
            AnimatedVisibility(
                visible = if (isEditing) showPassword else localShowPassword,
                enter = slideInVertically {
                    with(density) { -100.dp.roundToPx() }
                } + expandVertically(
                    expandFrom = Alignment.Top
                ) + fadeIn(
                    initialAlpha = 0.3f
                ),
                exit = slideOutVertically() + shrinkVertically() + fadeOut()
            ) {
                DisplayPassword(password = password)
            }
            if (deleteConfirmation) {
                ConfirmationDialog(
                    title = stringResource(R.string.delete_title),
                    message = stringResource(R.string.delete_question),
                    onConfirm = {
                        onDelete(id!!)
                        deleteConfirmation = false
                    },
                    onCancel = {
                        deleteConfirmation = false
                    },
                )
            }
        }
    }
}

@Composable
fun DisplayPassword(password: String) {
    Surface(
        color = MaterialTheme.colorScheme.inverseSurface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(SmallPadding)
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = password,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.inverseOnSurface
                ),
            )
        }
    }
}