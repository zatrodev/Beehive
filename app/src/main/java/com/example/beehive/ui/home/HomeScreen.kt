package com.example.beehive.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.data.Password
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.BeehiveButton
import com.example.beehive.ui.home.components.PasswordsGrid
import com.example.beehive.ui.home.components.SearchBar
import com.example.beehive.ui.navigation.SharedElementTransition
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigateToEditPassword: (Int) -> Unit,
    onNavigateToAddPassword: () -> Unit,
    sharedElementTransition: SharedElementTransition,
    viewModel: HomeViewModel = viewModel(factory = BeehiveViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        HomeContent(
            passwords = homeUiState.passwords,
            query = homeUiState.query,
            onQueryChange = viewModel::onQueryChange,
            onAddPasswordClick = onNavigateToAddPassword,
            onDeletePassword = {
                coroutineScope.launch {
                    viewModel.deletePassword(it)
                }
            },
            navigateToEditPassword = onNavigateToEditPassword,
            sharedElementTransition = sharedElementTransition,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HomeContent(
    passwords: List<Password>,
    query: String,
    onQueryChange: (String) -> Unit,
    onAddPasswordClick: () -> Unit,
    onDeletePassword: (Int) -> Unit,
    navigateToEditPassword: (Int) -> Unit,
    sharedElementTransition: SharedElementTransition,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .navigationBarsPadding()
    ) {
        SearchBar(
            query = query,
            onValueChanged = { onQueryChange(it) },
        )
        PasswordsGrid(
            passwords = passwords,
            onDelete = onDeletePassword,
            onEdit = navigateToEditPassword,
            sharedElementTransition = sharedElementTransition,
        )
        Spacer(modifier = Modifier.weight(1f))
        BeehiveButton(
            text = stringResource(R.string.add_password_button),
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            icon = Icons.Outlined.Add,
            onClick = onAddPasswordClick,
            modifier = Modifier.padding(SmallPadding)
        )
    }
}

