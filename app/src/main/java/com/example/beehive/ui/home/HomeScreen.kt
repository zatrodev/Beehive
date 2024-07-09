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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.data.Password
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.common.PasswordButton
import com.example.beehive.ui.home.components.PasswordsGrid
import com.example.beehive.ui.home.components.SearchBar

@Composable
fun HomeScreen(
    onNavigateToAddPassword: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = BeehiveViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        HomeContent(
            passwords = homeUiState.passwords,
            onAddPasswordClick = onNavigateToAddPassword,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HomeContent(
    passwords: List<Password>,
    onAddPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var focusedOnSearch = false

    fun onFocusChanged(isFocused: Boolean) {
        focusedOnSearch = isFocused
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .navigationBarsPadding()
    ) {
        SearchBar(
            query = query,
            onValueChanged = { query = it },
            onFocusChanged = ::onFocusChanged
        )
        PasswordsGrid(passwords = passwords)
        Spacer(modifier = Modifier.weight(1f))
        PasswordButton(
            text = "Add Password",
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            icon = Icons.Outlined.Add,
            onClick = onAddPasswordClick
        )
    }
}

