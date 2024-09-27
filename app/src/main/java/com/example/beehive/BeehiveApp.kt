package com.example.beehive

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.beehive.ui.navigation.BeehiveNavHost

@Composable
fun BeehiveApp(
    restartApp: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
) {
    BeehiveNavHost(restartApp = restartApp, navController = navController)
}