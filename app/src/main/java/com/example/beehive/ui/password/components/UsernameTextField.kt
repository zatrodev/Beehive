package com.example.beehive.ui.password.components

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun UsernameTextField(
    username: String,
    onUsernameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(value = username, onValueChange = onUsernameChange)
}
