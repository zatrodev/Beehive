package com.example.beehive.ui.password.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import com.example.beehive.data.passwords.Password
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.home.components.PasswordCard
import com.example.beehive.ui.navigation.SharedElementTransition

@Composable
fun PasswordsGrid(
    passwords: List<Password>,
    onDelete: (Int) -> Unit,
    onEdit: (Int) -> Unit,
    sharedElementTransition: SharedElementTransition,
) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(MediumPadding)) {
        items(passwords) { password ->
            PasswordCard(
                id = password.id,
                username = password.username,
                password = password.password,
                onDelete = onDelete,
                onEdit = onEdit,
                sharedElementTransition = sharedElementTransition
            )
        }
    }
}