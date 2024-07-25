package com.example.beehive.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.data.passwords.Password
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.LargePadding
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

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
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
        modifier = modifier
            .navigationBarsPadding()

    ) {
        Spacer(modifier = Modifier.height(LargePadding))
        Text(
            text = "Beehive",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(
                LargePadding
            )
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SearchBar(
                query = query,
                onValueChanged = { onQueryChange(it) },
            )
            if (passwords.isEmpty()) {
                Spacer(modifier = Modifier.weight(0.6f))
                Text(
                    text = stringResource(R.string.no_password_description),
                    style = MaterialTheme.typography.displayMedium.copy(
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                )
            } else {
                PasswordsGrid(
                    passwords = passwords,
                    onDelete = onDeletePassword,
                    onEdit = navigateToEditPassword,
                    sharedElementTransition = sharedElementTransition,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            BeehiveButton(
                text = stringResource(R.string.add_password_button),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = Icons.Outlined.Add,
                onClick = onAddPasswordClick,
                modifier = Modifier.padding(SmallPadding)
            )
        }
    }
}

