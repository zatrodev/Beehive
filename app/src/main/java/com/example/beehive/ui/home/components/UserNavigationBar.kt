package com.example.beehive.ui.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.beehive.R
import com.example.beehive.data.users.User
import com.example.beehive.ui.Dimensions.ExtraSmallPadding
import com.example.beehive.ui.Dimensions.NavIconSIze

@Composable
fun UserNavigationBar(
    users: List<User>,
    onClick: (User, Int) -> Unit,
    onAddPasswordClick: () -> Unit,
    onAddUserClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(NavIconSIze),
        actions = {
            users.mapIndexed { index, user ->
                UserButton(
                    user = user,
                    onClick = { onClick(user, index) },
                    modifier = Modifier.width(NavIconSIze)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { expanded = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(ExtraSmallPadding)
                )
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.add_user_label)) },
                    onClick = {
                        expanded = false
                        onAddUserClick()
                    },
                )

                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.add_password_label)) },
                    onClick = {
                        expanded = false
                        onAddPasswordClick()
                    }
                )
            }
        }
    )
}