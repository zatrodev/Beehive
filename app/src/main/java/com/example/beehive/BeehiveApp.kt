package com.example.beehive

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.beehive.ui.navigation.BeehiveNavHost

@Composable
fun BeehiveApp(navController: NavHostController = rememberNavController()) {
    BeehiveNavHost(navController = navController)
}