package com.example.beehive.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.beehive.data.users.User
import com.example.beehive.ui.Dimensions.ExtraSmallPadding

@Composable
fun UserNavigationBar(
    users: List<User>,
    onClick: (User, Int) -> Unit,
    onAddPasswordClick: () -> Unit
) {
    BottomAppBar(
        actions = {
            users.mapIndexed { index, user ->
                Surface(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(100.dp)
                ) {
                    IconButton(
                        onClick = { onClick(user, index) },
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = user.name,
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = user.name,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPasswordClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(ExtraSmallPadding)
                )

            }
        }
    )
}