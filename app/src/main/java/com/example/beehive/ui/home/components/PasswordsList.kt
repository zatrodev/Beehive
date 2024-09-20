package com.example.beehive.ui.home.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.beehive.domain.GetPasswordsWithIconsOfUserUseCase.PasswordWithIcon
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.navigation.SharedElementTransition

@OptIn(ExperimentalMaterialApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PasswordsList(
    page: Int,
    passwords: List<PasswordWithIcon>,
    refreshing: Boolean,
    refresh: () -> Unit,
    onNavigateToViewPassword: (String, Int) -> Unit,
    sharedElementTransition: SharedElementTransition,
) {
    val pullRefreshState = rememberPullRefreshState(refreshing, refresh)

    Box(
        modifier = Modifier
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(MediumPadding)
        ) {
            items(passwords) { password ->
                with(sharedElementTransition.sharedTransitionScope) {
                    PasswordTile(
                        name = password.self.name,
                        icon = password.icon,
                        packageName = password.self.uri,
                        onNavigateToViewPassword = { email ->
                            onNavigateToViewPassword(
                                email,
                                password.self.userId
                            )
                        },
                        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier
                            .sharedElement(
                                sharedElementTransition.sharedTransitionScope.rememberSharedContentState(
                                    key = password.self.uri
                                ),
                                animatedVisibilityScope = sharedElementTransition.animatedContentScope
                            )
                            .fillMaxWidth()
                            .padding(SmallPadding)
                    )
                }
            }
        }
        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}