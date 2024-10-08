package com.example.beehive.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.FabIconSize
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.DrawerItemsManager
import com.example.beehive.ui.common.BeehiveDrawer
import com.example.beehive.ui.common.BeehiveTopBar
import com.example.beehive.ui.common.ErrorScreen
import com.example.beehive.ui.common.LoadingScreen
import com.example.beehive.ui.home.components.PasswordTile
import com.example.beehive.ui.home.components.PasswordsGrid
import com.example.beehive.ui.home.components.SearchBar
import com.example.beehive.ui.navigation.SharedElementTransition
import com.example.beehive.ui.user.AddUserContent

@Composable
fun HomeScreen(
    onNavigateToAddPassword: () -> Unit,
    onNavigateToEditPassword: (Int, Int) -> Unit,
    restartApp: () -> Unit,
    sharedElementTransition: SharedElementTransition,
    viewModel: HomeViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is HomeScreenUiState.Loading -> LoadingScreen()
        is HomeScreenUiState.Error -> ErrorScreen(
            errorMessage = state.errorMessage,
            onRetry = restartApp,
        )

        is HomeScreenUiState.InputUser -> AddUserContent(
            email = state.email,
            onEmailChange = viewModel::onEmailChange,
            onCreateUser = viewModel::onCreateUser,
            labelText = stringResource(R.string.input_user_description),
        )

        is HomeScreenUiState.Ready -> HomeScreenReady(
            uiState = state,
            onNavigateToAddPassword = onNavigateToAddPassword,
            onNavigateToEditPassword = onNavigateToEditPassword,
            sharedElementTransition = sharedElementTransition
        )

        HomeScreenUiState.Tutorial -> Unit
    }
}

@Composable
private fun HomeScreenReady(
    uiState: HomeScreenUiState.Ready,
    onNavigateToAddPassword: () -> Unit,
    onNavigateToEditPassword: (Int, Int) -> Unit,
    sharedElementTransition: SharedElementTransition,
    viewModel: HomeViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    BeehiveDrawer(
        drawerState = drawerState,
        items = DrawerItemsManager.allItems
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                BeehiveTopBar(
                    title = stringResource(R.string.app_name),
                    drawerState = drawerState
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        onNavigateToAddPassword()
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .padding(SmallPadding)
                        .size(FabIconSize)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "add password",
                        modifier = Modifier.padding(MediumPadding)
                    )
                }
            },
        ) { innerPadding ->
            HomeContent(
                uiState = uiState,
                onDelete = viewModel::trashPassword,
                onEdit = { id, userId ->
                    onNavigateToEditPassword(id, userId)
                },
                onQueryChange = viewModel::onQueryChange,
                refresh = viewModel::refresh,
                sharedElementTransition = sharedElementTransition,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    uiState: HomeScreenUiState.Ready,
    onDelete: (Int) -> Unit,
    onEdit: (Int, Int) -> Unit,
    onQueryChange: (String) -> Unit,
    refresh: () -> Unit,
    sharedElementTransition: SharedElementTransition,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.navigationBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(LargePadding))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SearchBar(
                query = uiState.query,
                onValueChanged = { onQueryChange(it) },
            )
            val pullRefreshState = rememberPullRefreshState(uiState.isRefreshing, refresh)
            Box(
                modifier = Modifier
                    .pullRefresh(pullRefreshState)
                    .fillMaxSize()
                    .padding(SmallPadding)
            ) {
                if (uiState.appCredentialMap.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_bees_found),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    return@Box
                }

                LazyColumn {
                    items(uiState.appCredentialMap.toList()) { appPasswordPair ->
                        PasswordTile(
                            name = appPasswordPair.first.name,
                            icon = appPasswordPair.first.icon,
                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(horizontal = MediumPadding)
                        )
                        PasswordsGrid(
                            credentials = appPasswordPair.second,
                            onDelete = onDelete,
                            onEdit = onEdit,
                            sharedElementTransition = sharedElementTransition
                        )

                    }
                }
                PullRefreshIndicator(
                    refreshing = uiState.isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

