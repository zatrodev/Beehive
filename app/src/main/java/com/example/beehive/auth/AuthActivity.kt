package com.example.beehive.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.beehive.MainActivity
import com.example.beehive.ui.theme.BeehiveTheme

class AuthActivity : AppCompatActivity() {
    private val promptManager by lazy {
        BiometricPromptManager(
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeehiveTheme {
                AuthScreen(onNavigateToHome = {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, promptManager = promptManager)
            }
        }
    }
}