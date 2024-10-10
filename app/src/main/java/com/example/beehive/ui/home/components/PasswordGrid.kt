package com.example.beehive.ui.home.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.data.credential.PasswordApp
import com.example.beehive.data.user.User
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.PasswordCard
import com.example.beehive.ui.navigation.SharedElementTransition

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PasswordsGrid(
    credentials: List<CredentialAndUser>,
    groupingKey: Any,
    onDelete: (Int) -> Unit,
    onEdit: (Int, Int) -> Unit,
    sharedElementTransition: SharedElementTransition,
) {
    FlowRow(
        maxItemsInEachRow = 2,
        modifier = Modifier.padding(bottom = SmallPadding, start = SmallPadding, end = SmallPadding)
    ) {
        credentials.map { credentialAndUser ->
            when (groupingKey) {
                is PasswordApp -> PasswordCard(
                    id = credentialAndUser.credential.id,
                    title = credentialAndUser.user.email,
                    subtitle = credentialAndUser.credential.username,
                    password = credentialAndUser.credential.password,
                    userId = credentialAndUser.user.id,
                    onDelete = onDelete,
                    onEdit = onEdit,
                    sharedElementTransition = sharedElementTransition
                )

                is User -> PasswordCard(
                    id = credentialAndUser.credential.id,
                    title = credentialAndUser.credential.app.name,
                    subtitle = credentialAndUser.credential.username,
                    icon = credentialAndUser.credential.app.icon,
                    password = credentialAndUser.credential.password,
                    userId = credentialAndUser.user.id,
                    onDelete = onDelete,
                    onEdit = onEdit,
                    sharedElementTransition = sharedElementTransition
                )
            }
        }
    }
}