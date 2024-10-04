package com.example.beehive.ui.credential.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.beehive.R
import com.example.beehive.data.user.User
import com.example.beehive.ui.Dimensions.DropdownMenuBaseHeight
import com.example.beehive.ui.Dimensions.DropdownMenuMaxHeight
import com.example.beehive.ui.Dimensions.UserIconContainerHeight
import com.example.beehive.ui.Dimensions.UserIconContainerWidth
import com.example.beehive.ui.Dimensions.UserIconSize
import com.example.beehive.ui.home.components.UserButton

@Composable
fun UserDropdownMenu(
    activeUser: User?,
    users: List<User>,
    onClick: (User) -> Unit,
    onNavigateToAddUser: () -> Unit,
    isError: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        UserButton(
            user = activeUser,
            backgroundColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
            contentColor = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer,
            onClick = { expanded = true },
            modifier = Modifier
                .width(UserIconContainerWidth)
                .height(UserIconContainerHeight)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Column(
                modifier = Modifier
                    .heightIn(
                        min = DropdownMenuBaseHeight * users.size,
                        max = DropdownMenuMaxHeight
                    )
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                users.map { user ->
                    DropdownMenuItem(
                        text = { Text(text = user.email) },
                        onClick = {
                            expanded = false
                            onClick(user)
                        },
                    )
                }
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.add_user)) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_user),
                        contentDescription = "add user",
                        modifier = Modifier.size(UserIconSize),
                    )
                },
                onClick = {
                    expanded = false
                    onNavigateToAddUser()
                })
        }
    }
}