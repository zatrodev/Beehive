package com.example.beehive.ui.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.beehive.domain.GetPasswordsWithIconsOfUserUseCase.PasswordWithIcon
import com.example.beehive.ui.Dimensions.MediumPadding

@Composable
fun PasswordsList(
    passwords: List<PasswordWithIcon>,
    onNavigateToViewPassword: (String, Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(MediumPadding)
    ) {
        items(passwords) { password ->
            PasswordTile(
                name = password.self.name,
                uri = password.self.uri,
                userId = password.self.userId,
                icon = password.icon,
                onNavigateToViewPassword = onNavigateToViewPassword
            )
        }
    }
}