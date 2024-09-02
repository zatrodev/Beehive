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

@Serializable
data class ViewPassword(val packageName: String, val userId: Int)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BeehiveNavHost(
    navController: NavHostController
) {
    SharedTransitionLayout {
        NavHost(navController = navController, startDestination = Home) {
            composable<Home> {
                HomeScreen(
                    onNavigateToAddPassword = { userId ->
                        navController.navigate(
                            route = AddPassword(
                                userId = userId
                            )
                        )
                    },
                    onNavigateToViewPassword = { uri, userId ->
                        navController.navigate(
                            route = ViewPassword(
                                packageName = uri,
                                userId = userId
                            )
                        )
                    },
                    onNavigateToAddUser = {
                        navController.navigate(route = AddUser)
                    },
                    sharedElementTransition = SharedElementTransition(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable
                    )
                )
            }
            composable<AddPassword> {
                AddPasswordScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<AddUser> {
                AddUserScreen(
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
                    onNavigateToEditPassword = { id, userId ->
                        navController.navigate(
                            route = EditPassword(
                                id = id,
                                userId = userId
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