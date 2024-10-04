package com.example.beehive.ui.home.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.PasswordCard
import com.example.beehive.ui.navigation.SharedElementTransition

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PasswordsGrid(
    credentials: List<CredentialAndUser>,
    onDelete: (Int) -> Unit,
    onEdit: (Int, Int) -> Unit,
    sharedElementTransition: SharedElementTransition,
) {
    FlowRow(
        maxItemsInEachRow = 2,
        modifier = Modifier.padding(vertical = SmallPadding, horizontal = SmallPadding)
    ) {
        credentials.map { credentialAndUser ->
            PasswordCard(
                id = credentialAndUser.credential.id,
                username = credentialAndUser.credential.username,
                password = credentialAndUser.credential.password,
                user = credentialAndUser.user,
                onDelete = onDelete,
                onEdit = onEdit,
                sharedElementTransition = sharedElementTransition
            )
        }
    }
}