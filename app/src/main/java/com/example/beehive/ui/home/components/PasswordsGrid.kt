package com.example.beehive.ui.home.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import com.example.beehive.data.Password
import com.example.beehive.ui.Dimensions.SmallPadding

@Composable
fun PasswordsGrid(
    passwords: List<Password>,
    onDelete: (Int) -> Unit,
    onEdit: (Int) -> Unit
) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(SmallPadding)) {
        items(passwords) { password ->
            PasswordCard(
                id = password.id,
                site = password.site,
                password = password.password,
                onDelete = onDelete,
                onEdit = onEdit,
            )
        }
    }
}