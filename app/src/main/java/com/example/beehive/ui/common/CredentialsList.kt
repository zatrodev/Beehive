package com.example.beehive.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.data.credential.PasswordApp
import com.example.beehive.data.user.User
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.home.ShareableCredentialData
import com.example.beehive.ui.navigation.SharedElementTransition

@Composable
fun CredentialsList(
    credentials: List<CredentialAndUser>,
    groupingKey: Any,
    isSharingMode: Boolean,
    enableShareMode: () -> Unit,
    addSelectedCredential: (ShareableCredentialData) -> Unit,
    removeSelectedCredential: (ShareableCredentialData) -> Unit,
    onDelete: (Int) -> Unit,
    onEdit: (Int, Int) -> Unit,
    sharedElementTransition: SharedElementTransition,
) {
    Column(
        modifier = Modifier
            .padding(bottom = SmallPadding, start = SmallPadding, end = SmallPadding)
    ) {
        credentials.map { credentialAndUser ->
            when (groupingKey) {
                is PasswordApp -> CredentialCard(
                    id = credentialAndUser.credential.id,
                    title = credentialAndUser.user.email,
                    subtitle = credentialAndUser.credential.username,
                    password = credentialAndUser.credential.password,
                    userId = credentialAndUser.user.id,
                    isSharingMode = isSharingMode,
                    enableShareMode = enableShareMode,
                    addSelectedCredential = {
                        addSelectedCredential(
                            ShareableCredentialData(
                                credentialAndUser.credential.app.name,
                                credentialAndUser.user.email,
                                credentialAndUser.credential.password
                            )
                        )
                    },
                    removeSelectedCredential = {
                        removeSelectedCredential(
                            ShareableCredentialData(
                                credentialAndUser.credential.app.name,
                                credentialAndUser.user.email,
                                credentialAndUser.credential.password
                            )
                        )
                    },
                    onDelete = onDelete,
                    onEdit = onEdit,
                    sharedElementTransition = sharedElementTransition,
                )

                is User -> CredentialCard(
                    id = credentialAndUser.credential.id,
                    title = credentialAndUser.credential.app.name,
                    subtitle = credentialAndUser.credential.username,
                    icon = credentialAndUser.credential.app.icon,
                    password = credentialAndUser.credential.password,
                    userId = credentialAndUser.user.id,
                    isSharingMode = isSharingMode,
                    enableShareMode = enableShareMode,
                    addSelectedCredential = {
                        addSelectedCredential(
                            ShareableCredentialData(
                                credentialAndUser.credential.app.name,
                                credentialAndUser.user.email,
                                credentialAndUser.credential.password
                            )
                        )
                    },
                    removeSelectedCredential = {
                        removeSelectedCredential(
                            ShareableCredentialData(
                                credentialAndUser.credential.app.name,
                                credentialAndUser.user.email,
                                credentialAndUser.credential.password
                            )
                        )
                    },
                    onDelete = onDelete,
                    onEdit = onEdit,
                    sharedElementTransition = sharedElementTransition,
                )
            }
        }
    }
}