package com.example.beehive.ui.home

import android.database.sqlite.SQLiteException
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.ErrorScreen
import com.example.beehive.ui.common.LoadingScreen
import com.example.beehive.ui.home.components.PasswordTile
import com.example.beehive.ui.home.components.PasswordsGrid
import com.example.beehive.ui.home.components.SearchBar
import com.example.beehive.ui.home.components.UserNavigationBar
import com.example.beehive.ui.navigation.SharedElementTransition
import com.example.beehive.ui.user.AddUserContent
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigateToAddPassword: (Int) -> Unit,
    onNavigateToEditPassword: (Int, Int) -> Unit,
    onNavigateToAddUser: () -> Unit,
    restartApp: () -> Unit,
    sharedElementTransition: SharedElementTransition,
    viewModel: HomeViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    val homeScreenUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    when (val uiState = homeScreenUiState) {
        is HomeScreenUiState.Loading -> LoadingScreen()
        is HomeScreenUiState.Error -> ErrorScreen(
            errorMessage = uiState.errorMessage ?: "",
            onRetry = viewModel::refresh,
            onClose = if (uiState.errorType is SQLiteException) restartApp else null
        )

        is HomeScreenUiState.InputUser -> AddUserContent(
            email = uiState.email,
            onEmailChange = viewModel::onEmailChange,
            onCreateUser = viewModel::onCreateUser,
            labelText = stringResource(R.string.input_user_description),
        )

        is HomeScreenUiState.Ready -> HomeScreenReady(
            uiState = uiState,
            onNavigateToAddPassword = onNavigateToAddPassword,
            onNavigateToEditPassword = onNavigateToEditPassword,
            onNavigateToAddUser = onNavigateToAddUser,
            sharedElementTransition = sharedElementTransition
        )

    }
}

@Composable
fun HomeScreenReady(
    uiState: HomeScreenUiState.Ready,
    onNavigateToAddPassword: (Int) -> Unit,
    onNavigateToEditPassword: (Int, Int) -> Unit,
    onNavigateToAddUser: () -> Unit,
    sharedElementTransition: SharedElementTransition,
    viewModel: HomeViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { uiState.userPasswordMap.size })
    val users = uiState.userPasswordMap.keys.toList()

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onUserSelected(users[pagerState.currentPage])
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            UserNavigationBar(
                users = users,
                onClick = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                onAddPasswordClick = { onNavigateToAddPassword(uiState.selectedUser.id) },
                onAddUserClick = onNavigateToAddUser
            )
        }
    ) { innerPadding ->
        HomeContent(
            uiState = uiState,
            pagerState = pagerState,
            onDelete = viewModel::deletePassword,
            onEdit = { id ->
                onNavigateToEditPassword(id, uiState.selectedUser.id)
            },
            onQueryChange = viewModel::onQueryChange,
            refresh = viewModel::refresh,
            sharedElementTransition = sharedElementTransition,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    uiState: HomeScreenUiState.Ready,
    pagerState: PagerState,
    onDelete: (Int) -> Unit,
    onEdit: (Int) -> Unit,
    onQueryChange: (String) -> Unit,
    refresh: () -> Unit,
    sharedElementTransition: SharedElementTransition,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.navigationBarsPadding()
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
            HorizontalPager(
                state = pagerState,
            ) {
                val pullRefreshState = rememberPullRefreshState(uiState.isRefreshing, refresh)
                Box(
                    modifier = Modifier
                        .pullRefresh(pullRefreshState)
                        .fillMaxSize()
                        .padding(SmallPadding)
                ) {
                    val credentials = uiState.userPasswordMap[uiState.selectedUser]

                    if (credentials == null) {
                        ErrorScreen(stringResource(R.string.no_credentials_found), refresh)
                        return@Box
                    }

                    if (credentials.isEmpty())
                        Text(
                            text = stringResource(R.string.no_bees_found),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    else
                        LazyColumn {
                            items(credentials.toList()) { appPasswordPair ->
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
}

