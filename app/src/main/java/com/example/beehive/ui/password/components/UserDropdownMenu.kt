package com.example.beehive.ui.password.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.beehive.data.users.User
import com.example.beehive.ui.Dimensions.NavIconSIze
import com.example.beehive.ui.Dimensions.UserIconContainerHeight
import com.example.beehive.ui.home.components.UserButton

@Composable
fun UserDropdownMenu(
    activeUser: User,
    users: List<User>,
    onClick: (User) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        UserButton(
            user = activeUser,
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            onClick = { expanded = true },
            modifier = Modifier
                .width(NavIconSIze)
                .height(UserIconContainerHeight)
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
    }
}