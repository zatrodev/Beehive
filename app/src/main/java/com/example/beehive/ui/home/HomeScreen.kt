package com.example.beehive.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.BeehiveButton
import com.example.beehive.ui.home.components.PasswordsGrid
import com.example.beehive.ui.home.components.SearchBar
import com.example.beehive.ui.home.components.UserNavigationBar
import com.example.beehive.ui.navigation.SharedElementTransition
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigateToEditPassword: (Int) -> Unit,
    onNavigateToAddPassword: () -> Unit,
    sharedElementTransition: SharedElementTransition,
    viewModel: HomeViewModel = viewModel(factory = BeehiveViewModelProvider.Factory)
) {
    val homeScreenUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    when (val uiState = homeScreenUiState) {
        is HomeScreenUiState.Loading -> HomeScreenLoading()
        is HomeScreenUiState.Error -> HomeScreenError(onRetry = {})
        is HomeScreenUiState.Ready -> HomeScreenReady(
            uiState = uiState,
            onNavigateToEditPassword = onNavigateToEditPassword,
            onNavigateToAddPassword = onNavigateToAddPassword,
            sharedElementTransition = sharedElementTransition
        )
    }
}

@Composable
private fun HomeScreenLoading(modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxSize()) {
        Box {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun HomeScreenError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = stringResource(id = R.string.an_error_has_occurred),
                modifier = Modifier.padding(SmallPadding)
            )
            Button(onClick = onRetry) {
                Text(text = stringResource(id = R.string.retry_label))
            }
        }
    }
}

@Composable
fun HomeScreenReady(
    uiState: HomeScreenUiState.Ready,
    onNavigateToEditPassword: (Int) -> Unit,
    onNavigateToAddPassword: () -> Unit,
    sharedElementTransition: SharedElementTransition,
    viewModel: HomeViewModel = viewModel(factory = BeehiveViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { uiState.users.size })

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            UserNavigationBar(users = uiState.users,
                onClick = { user, index ->
                    viewModel.onUserSelected(user)
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                })
        }
    ) { innerPadding ->
        HomeContent(
            uiState = uiState,
            pagerState = pagerState,
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
    uiState: HomeScreenUiState.Ready,
    pagerState: PagerState,
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
                query = uiState.query,
                onValueChanged = { onQueryChange(it) },
            )
            if (uiState.featuredPasswords.isEmpty()) {
                Spacer(modifier = Modifier.weight(0.6f))
                Text(
                    text = stringResource(R.string.no_password_description),
                    style = MaterialTheme.typography.displayMedium.copy(
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                )
            } else {
                HorizontalPager(state = pagerState) { page ->
                    PasswordsGrid(
                        passwords = uiState.featuredPasswords,
                        onDelete = onDeletePassword,
                        onEdit = navigateToEditPassword,
                        sharedElementTransition = sharedElementTransition,
                    )
                }

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

