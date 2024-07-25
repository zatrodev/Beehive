package com.example.beehive.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.beehive.ui.Dimensions.ExtraSmallPadding
import com.example.beehive.ui.Dimensions.IconSize
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.PasswordCardWidth
import com.example.beehive.ui.Dimensions.RoundedCornerShape
import com.example.beehive.ui.Dimensions.ShadowElevation
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.navigation.SharedElementTransition


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PasswordCard(
    modifier: Modifier = Modifier,
    id: Int? = null,
    name: String,
    password: String,
    showPassword: Boolean = false,
    onDelete: (Int) -> Unit = {},
    onEdit: (Int) -> Unit = {},
    sharedElementTransition: SharedElementTransition
) {
    var localShowPassword by remember { mutableStateOf(showPassword) }
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    val isEditing = id == null
    val interactionSource = remember { MutableInteractionSource() }
    val passwordModifier = if (isEditing) modifier else Modifier.clickable(
        interactionSource = interactionSource,
        indication = null
    ) {
        localShowPassword = !localShowPassword
    }
    val density = LocalDensity.current

    with(sharedElementTransition.sharedTransitionScope) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(RoundedCornerShape),
            modifier = passwordModifier
                .sharedElement(
                    sharedElementTransition.sharedTransitionScope.rememberSharedContentState(key = password),
                    animatedVisibilityScope = sharedElementTransition.animatedContentScope,
                )
                .size(PasswordCardWidth)
                .padding(SmallPadding)
                .shadow(
                    elevation = ShadowElevation,
                    shape = RoundedCornerShape(RoundedCornerShape)
                )

        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(SmallPadding)
                )
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ExtraSmallPadding)
            ) {
                IconButton(
                    onClick = { if (id != null) onEdit(id) },
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
                    onClick = { if (id != null) deleteConfirmationRequired = true },
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
            if (deleteConfirmationRequired) {
                DeleteConfirmationDialog(
                    onDeleteConfirm = {
                        onDelete(id!!)
                        deleteConfirmationRequired = false
                    }, onDeleteCancel = {
                        deleteConfirmationRequired = false
                    },
                    modifier = Modifier.padding(MediumPadding)
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
                .padding(SmallPadding)
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = password,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.inverseOnSurface
                ),
            )
        }
    }
}