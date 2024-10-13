package com.example.beehive.ui.credential.deleted

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.ActionIconSize
import com.example.beehive.ui.Dimensions.CenterIconSize
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.DrawerItemsManager
import com.example.beehive.ui.DrawerItemsManager.DELETED_INDEX
import com.example.beehive.ui.common.BeehiveDrawer
import com.example.beehive.ui.common.BeehiveTopBar
import com.example.beehive.ui.common.ErrorScreen
import com.example.beehive.ui.common.LoadingScreen
import com.example.beehive.ui.credential.components.DeletedPasswordCard
import com.example.beehive.ui.home.components.ConfirmationDialog
import com.example.beehive.utils.getDaysDifferenceFromNow

@Composable
fun DeletedCredentialsScreen(
    restartApp: () -> Unit,
    viewModel: DeletedCredentialsViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is DeletedCredentialsUiState.Loading -> LoadingScreen()
        is DeletedCredentialsUiState.Error -> ErrorScreen(
            errorMessage = state.errorMessage,
            onRetry = restartApp
        )

        is DeletedCredentialsUiState.Ready -> DeletedCredentialsScreenReady(
            credentials = state.deletedCredentials,
            onDelete = viewModel::deleteCredential,
            onDeleteAll = viewModel::deleteAllCredentials,
            onRestore = viewModel::restoreCredential
        )
    }
}

@Composable
fun DeletedCredentialsScreenReady(
    credentials: List<CredentialAndUser>,
    onDelete: (Int) -> Unit,
    onDeleteAll: () -> Unit,
    onRestore: (Int) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var deleteAllConfirmation by remember { mutableStateOf(false) }

    BeehiveDrawer(
        drawerState = drawerState,
        selectedIndex = DELETED_INDEX,
        items = DrawerItemsManager.allItems
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                BeehiveTopBar(
                    title = stringResource(R.string.deleted_credentials_title),
                    drawerState = drawerState
                ) {
                    credentials.isEmpty().let {
                        IconButton(onClick = {
                            deleteAllConfirmation = true
                        }, modifier = Modifier.size(ActionIconSize)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete_all),
                                contentDescription = "delete all",
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            DeletedCredentialsContent(
                credentials = credentials,
                onDelete = onDelete,
                onRestore = onRestore,
                modifier = Modifier.padding(innerPadding)
            )
            if (deleteAllConfirmation) {
                ConfirmationDialog(
                    title = stringResource(R.string.delete_all_from_trash_title),
                    message = stringResource(R.string.delete_all_from_trash_question),
                    onConfirm = {
                        onDeleteAll()
                        deleteAllConfirmation = false
                    },
                    onCancel = {
                        deleteAllConfirmation = false
                    },
                )
            }
        }
    }
}

@Composable
fun DeletedCredentialsContent(
    credentials: List<CredentialAndUser>,
    onDelete: (Int) -> Unit,
    onRestore: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.navigationBarsPadding()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(MediumPadding)
        ) {
            Text(
                text = stringResource(R.string.delete_from_trash_info),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(MediumPadding)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(SmallPadding)
        ) {
            if (credentials.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_empty_trash),
                        contentDescription = "Empty Trash",
                        tint = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(CenterIconSize)
                    )
                    Text(
                        text = "No rogue bees here.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(top = MediumPadding)
                    )
                }

                return@Box
            }

            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(credentials) { credential ->
                    DeletedPasswordCard(
                        id = credential.credential.id,
                        username = credential.credential.username,
                        appName = credential.credential.app.name,
                        user = credential.user,
                        remainingDays = getDaysDifferenceFromNow(credential.credential.deletionDate!!),
                        onDelete = onDelete,
                        onRestore = onRestore
                    )
                }
            }
        }
    }
}

