package com.example.beehive.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.beehive.ui.credential.add.AddCredentialScreen
import com.example.beehive.ui.credential.edit.EditCredentialScreen
import com.example.beehive.ui.home.HomeScreen
import com.example.beehive.ui.user.AddUserScreen
import kotlinx.serialization.Serializable


@Serializable
object Home

@Serializable
data class AddPassword(val userId: Int)

@Serializable
object AddUser

@Serializable
data class EditPassword(val id: Int, val userId: Int)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BeehiveNavHost(
    restartApp: () -> Unit = {},
    navController: NavHostController,
) {
    val durationMillis = 700
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Home,
        ) {
            composable<Home>(
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(durationMillis)
                    )
                },
                popEnterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(durationMillis)
                    )
                }
            ) {
                HomeScreen(
                    onNavigateToAddPassword = { userId ->
                        navController.navigate(
                            route = AddPassword(
                                userId = userId
                            )
                        )
                    },
                    onNavigateToEditPassword = { id, userId ->
                        navController.navigate(
                            route = EditPassword(
                                id = id,
                                userId = userId
                            )
                        )
                    },
                    onNavigateToAddUser = {
                        navController.navigate(route = AddUser)
                    },
                    restartApp = restartApp,
                    sharedElementTransition = SharedElementTransition(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable
                    )
                )
            }

            composable<AddPassword>(
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(durationMillis)
                    )
                },
                popExitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(durationMillis)
                    )
                }
            ) {
                AddCredentialScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<AddUser>(
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(durationMillis)
                    )
                },
                popExitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(durationMillis)
                    )
                }
            ) {
                AddUserScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<EditPassword>(
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(durationMillis)
                    )
                },
                popExitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(durationMillis)
                    )
                }
            ) {
                EditCredentialScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    sharedElementTransition = SharedElementTransition(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable
                    )
                )
            }
        }
    }
}

class SharedElementTransition @OptIn(ExperimentalSharedTransitionApi::class) constructor(
    val sharedTransitionScope: SharedTransitionScope,
    val animatedContentScope: AnimatedContentScope,
)