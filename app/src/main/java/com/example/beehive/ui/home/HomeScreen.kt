package com.example.beehive.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.EmailTextFieldSize
import com.example.beehive.ui.Dimensions.IndicatorLineThickness
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.RoundedCornerShape
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.BeehiveButton
import com.example.beehive.ui.home.components.PasswordsList
import com.example.beehive.ui.home.components.SearchBar
import com.example.beehive.ui.home.components.UserNavigationBar
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigateToAddPassword: () -> Unit,
    onNavigateToViewPassword: (String, Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = BeehiveViewModelProvider.Factory)
) {
    val homeScreenUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    when (val uiState = homeScreenUiState) {
        is HomeScreenUiState.Loading -> HomeScreenLoading()
        is HomeScreenUiState.Error -> HomeScreenError(
            errorMessage = uiState.errorMessage ?: stringResource(R.string.an_error_has_occurred),
            onRetry = {})

        is HomeScreenUiState.InputUser -> HomeScreenInputUser(
            email = uiState.email,
            onEmailChange = viewModel::onEmailChange,
            onCreateUser = viewModel::onCreateUser
        )

        is HomeScreenUiState.Ready -> HomeScreenReady(
            uiState = uiState,
            onNavigateToAddPassword = onNavigateToAddPassword,
            onNavigateToViewPassword = onNavigateToViewPassword
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

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenInputUser(
    email: String,
    onEmailChange: (String) -> Unit,
    onCreateUser: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isError by remember { mutableStateOf(false) }

    Surface(modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.input_user_description),
            )
            TextField(
                value = email,
                onValueChange = onEmailChange,
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                modifier = modifier
                    .width(EmailTextFieldSize)
                    .padding(MediumPadding)
                    .clip(RoundedCornerShape(RoundedCornerShape))
                    .indicatorLine(
                        enabled = true,
                        isError = isError,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                        ),
                        interactionSource = MutableInteractionSource(),
                        unfocusedIndicatorLineThickness = IndicatorLineThickness,
                    ),

                )
            if (isError)
                Text(
                    text = stringResource(R.string.email_error_message),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.padding(SmallPadding)
                )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(MediumPadding)
                .navigationBarsPadding()
        ) {
            BeehiveButton(
                text = stringResource(R.string.continue_label),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    if (email.isEmpty())
                        isError = true
                    else
                        onCreateUser(email)
                },
            )
        }
    }
}

@Composable
private fun HomeScreenError(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = stringResource(R.string.an_error_has_occurred),
                modifier = Modifier.padding(SmallPadding)
            )
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
            )
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.retry_label))
            }
        }
    }
}

@Composable
fun HomeScreenReady(
    uiState: HomeScreenUiState.Ready,
    onNavigateToAddPassword: () -> Unit,
    onNavigateToViewPassword: (String, Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = BeehiveViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { uiState.users.size })

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            UserNavigationBar(
                users = uiState.users,
                onClick = { user, index ->
                    viewModel.onUserSelected(user)
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                onAddPasswordClick = onNavigateToAddPassword
            )
        }
    ) { innerPadding ->
        HomeContent(
            uiState = uiState,
            pagerState = pagerState,
            onNavigateToViewPassword = onNavigateToViewPassword,
            onQueryChange = viewModel::onQueryChange,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HomeContent(
    uiState: HomeScreenUiState.Ready,
    pagerState: PagerState,
    onNavigateToViewPassword: (String, Int) -> Unit,
    onQueryChange: (String) -> Unit,
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
            if (uiState.passwords.isEmpty()) {
                Spacer(modifier = Modifier.weight(0.6f))
                Text(
                    text = stringResource(R.string.no_password_description),
                    style = MaterialTheme.typography.displayMedium.copy(
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                )
            } else {
                // TODO: fix pages (enable swipe)
                HorizontalPager(state = pagerState) { page ->
                    PasswordsList(
                        passwords = uiState.passwords,
                        onNavigateToViewPassword = onNavigateToViewPassword,
                    )
                }

            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

