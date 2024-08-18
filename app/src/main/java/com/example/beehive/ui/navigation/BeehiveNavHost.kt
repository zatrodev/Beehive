package com.example.beehive.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.beehive.ui.home.HomeScreen
import com.example.beehive.ui.password.add.AddPasswordScreen
import com.example.beehive.ui.password.edit.EditPasswordScreen
import com.example.beehive.ui.password.view.ViewPasswordScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object AddPassword

@Serializable
data class EditPassword(val id: Int)

@Serializable
data class ViewPassword(val uri: String, val userId: Int)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BeehiveNavHost(
    navController: NavHostController
) {
    SharedTransitionLayout {
        NavHost(navController = navController, startDestination = Home) {
            composable<Home> {
                HomeScreen(
                    onNavigateToAddPassword = {
                        navController.navigate(route = AddPassword)
                    },
                    onNavigateToViewPassword = { uri, userId ->
                        navController.navigate(
                            route = ViewPassword(
                                uri = uri,
                                userId = userId
                            )
                        )
                    }
                )
            }
            composable<AddPassword> {
                AddPasswordScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable<EditPassword> {
                EditPasswordScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    sharedElementTransition = SharedElementTransition(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable
                    )
                )
            }

            composable<ViewPassword> {
                ViewPasswordScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onNavigateToEditPassword = {
                        navController.navigate(
                            route = EditPassword(
                                id = it,
                            )
                        )
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