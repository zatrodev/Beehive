package com.example.beehive.auth.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.beehive.auth.AuthScreen
import com.example.beehive.auth.BiometricPromptManager
import com.example.beehive.auth.ReplyIntentManager
import com.example.beehive.auth.choose.ChooseCredentialScreen
import kotlinx.serialization.Serializable

@Serializable
object Auth

@Serializable
object ChooseCredential


@Composable
fun AuthNavHost(
    onNavigateToHome: () -> Unit,
    replyIntentManager: ReplyIntentManager?,
    promptManager: BiometricPromptManager,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Auth
    ) {
        composable<Auth> {
            AuthScreen(
                navigateToHome = onNavigateToHome,
                navigateToChooseCredential = {
                    navController.navigate(ChooseCredential)
                },
                replyIntentManager = replyIntentManager,
                promptManager = promptManager,
            )
        }

        composable<ChooseCredential> {
            ChooseCredentialScreen(
                replyIntentManager = replyIntentManager!!
            )
        }
    }
}