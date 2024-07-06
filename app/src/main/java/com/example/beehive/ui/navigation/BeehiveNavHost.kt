package com.example.beehive.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.beehive.ui.home.HomeScreen
import com.example.beehive.ui.home.HomeViewModel
import com.example.beehive.ui.password.AddPasswordScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object AddPassword

@Composable
fun BeehiveNavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            val viewModel = HomeViewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAddPassword = {
                    navController.navigate(route = AddPassword)
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
    }
}