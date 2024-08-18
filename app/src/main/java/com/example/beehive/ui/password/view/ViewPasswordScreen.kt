package com.example.beehive.ui.password.view

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.navigation.SharedElementTransition
import com.example.beehive.ui.password.components.PasswordsGrid

@Composable
fun ViewPasswordScreen(
    onBack: () -> Unit,
    onNavigateToEditPassword: (Int) -> Unit,
    sharedElementTransition: SharedElementTransition
) {
    val viewModel: ViewPasswordViewModel = viewModel(
        factory = BeehiveViewModelProvider.Factory
    )

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        ViewPasswordContent(
            uiState = viewModel.uiState,
            onBack = onBack,
            onDelete = viewModel::deletePassword,
            onEdit = onNavigateToEditPassword,
            sharedElementTransition = sharedElementTransition,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ViewPasswordContent(
    uiState: ViewPasswordUiState,
    onBack: () -> Unit,
    onDelete: (Int) -> Unit,
    onEdit: (Int) -> Unit,
    sharedElementTransition: SharedElementTransition,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
        }
        Text(
            text = uiState.email,
            style = MaterialTheme.typography.headlineLarge,
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