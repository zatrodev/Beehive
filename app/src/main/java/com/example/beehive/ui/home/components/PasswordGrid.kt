package com.example.beehive.ui.home.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.beehive.data.credential.Credential
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.credential.components.PasswordCard
import com.example.beehive.ui.navigation.SharedElementTransition

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PasswordsGrid(
    credentials: List<Credential>,
    onDelete: (Int) -> Unit,
    onEdit: (Int) -> Unit,
    sharedElementTransition: SharedElementTransition,
) {
    FlowRow(
        maxItemsInEachRow = 2,
        modifier = Modifier.padding(vertical = SmallPadding, horizontal = SmallPadding)
    ) {
        credentials.map { credential ->
            PasswordCard(
                id = credential.id,
                username = credential.username,
                password = credential.password,
                onDelete = onDelete,
                onEdit = onEdit,
                sharedElementTransition = sharedElementTransition
            )
        }
    }
}