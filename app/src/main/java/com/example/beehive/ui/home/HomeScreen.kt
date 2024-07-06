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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.beehive.ui.common.PasswordButton
import com.example.beehive.ui.home.components.ScrollablePasswords
import com.example.beehive.ui.home.components.SearchBar
import com.example.beehive.ui.theme.BeehiveTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAddPassword: () -> Unit
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val passwords by viewModel.passwords.collectAsStateWithLifecycle()
    var focusedOnSearch = false

    fun onFocusChanged(isFocused: Boolean) {
        focusedOnSearch = isFocused
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .navigationBarsPadding()
        ) {
            SearchBar(
                query = query,
                onValueChanged = viewModel::onQueryChange,
                onFocusChanged = ::onFocusChanged
            )
            ScrollablePasswords(
                passwords,
                query = query,
                focusedOnSearch
            )
            Spacer(modifier = Modifier.weight(1f))
            PasswordButton(
                text = "Add Password",
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                icon = Icons.Outlined.Add,
                onClick = onNavigateToAddPassword
            )
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    BeehiveTheme {
        val viewModel = HomeViewModel()
        HomeScreen(
            viewModel = viewModel,
            onNavigateToAddPassword = {}
        )
    }
}
