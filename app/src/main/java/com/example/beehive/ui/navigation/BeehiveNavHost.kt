package com.example.beehive.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.beehive.ui.home.HomeScreen
import com.example.beehive.ui.password.AddPasswordScreen
import com.example.beehive.ui.password.EditPasswordScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object AddPassword

@Serializable
data class EditPassword(val id: Int)

@Composable
fun BeehiveNavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                onNavigateToAddPassword = {
                    navController.navigate(route = AddPassword)
                },
                onNavigateToEditPassword = {
                    navController.navigate(
                        route = EditPassword(
                            id = it,
                        )
                    )
                }
            )
        }
        composable<AddPassword> {
            AddPasswordScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<EditPassword> {
            EditPasswordScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}