package com.example.beehive.ui.password.view

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.common.ErrorScreen
import com.example.beehive.ui.common.LoadingScreen
import com.example.beehive.ui.home.components.PasswordTile
import com.example.beehive.ui.navigation.SharedElementTransition
import com.example.beehive.ui.password.components.PasswordsGrid

@Composable
fun ViewPasswordScreen(
    onBack: () -> Unit,
    onNavigateToEditPassword: (Int, Int) -> Unit,
    sharedElementTransition: SharedElementTransition,
    viewModel: ViewPasswordViewModel = viewModel(
        factory = BeehiveViewModelProvider.Factory
    )
) {
    val viewPasswordUiState by viewModel.viewPasswordUiState.collectAsStateWithLifecycle()
    when (val uiState = viewPasswordUiState) {
        is ViewPasswordUiState.Loading -> LoadingScreen()
        is ViewPasswordUiState.Error -> ErrorScreen(
            errorMessage = uiState.errorMessage
                ?: stringResource(R.string.an_error_has_occurred),
            onRetry = {
                // TODO: refresh
            })

        is ViewPasswordUiState.Ready -> ViewPasswordReady(
            uiState = uiState,
            onBack = onBack,
            onDelete = viewModel::deletePassword,
            onEdit = { id ->
                onNavigateToEditPassword(id, viewModel.getActiveUserId())
            },
            sharedElementTransition = sharedElementTransition
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ViewPasswordReady(
    uiState: ViewPasswordUiState.Ready,
    onBack: () -> Unit,
    onDelete: (Int) -> Unit,
    onEdit: (Int) -> Unit,
    sharedElementTransition: SharedElementTransition,
    viewModel: ViewPasswordViewModel = viewModel(
        factory = BeehiveViewModelProvider.Factory
    )
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            with(sharedElementTransition.sharedTransitionScope) {
                PasswordTile(
                    name = uiState.name,
                    icon = uiState.icon,
                    onNavigateToViewPassword = {},
                    backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier
                        .sharedElement(
                            sharedElementTransition.sharedTransitionScope.rememberSharedContentState(
                                key = viewModel.getUri()
                            ),
                            animatedVisibilityScope = sharedElementTransition.animatedContentScope,
                        )
                        .padding(
                            PaddingValues(
                                start = MediumPadding,
                                end = MediumPadding,
                                top = MediumPadding
                            )
                        )
                )
            }
            Text(
                text = uiState.email,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(
                    LargePadding
                )
            )

            PasswordsGrid(
                passwords = uiState.passwords,
                onDelete = onDelete,
                onEdit = onEdit,
                sharedElementTransition = sharedElementTransition
            )
        }
    }
}