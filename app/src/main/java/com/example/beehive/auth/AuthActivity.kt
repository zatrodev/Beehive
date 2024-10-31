package com.example.beehive.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.beehive.MainActivity
import com.example.beehive.auth.navigation.AuthNavHost
import com.example.beehive.data.container.AuthContainer
import com.example.beehive.data.container.AuthContainerImpl
import com.example.beehive.data.container.BeehiveContainer
import com.example.beehive.data.container.BeehiveContainerImpl
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.EXTRA_FROM_SERVICE
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.EXTRA_IS_CHOOSE
import com.example.beehive.service.autofill.ReplyIntentManager
import com.example.beehive.ui.theme.BeehiveTheme

class AuthActivity : AppCompatActivity() {
    private lateinit var authContainer: AuthContainer
    private lateinit var beehiveContainer: BeehiveContainer

    private var replyIntentManager: ReplyIntentManager? = null
    private val promptManager by lazy {
        BiometricPromptManager(
            this,
            authContainer.dataStore
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authContainer = AuthContainerImpl(applicationContext)
        beehiveContainer = BeehiveContainerImpl(applicationContext)

        setContent {
            BeehiveTheme {
                if (intent.getBooleanExtra(EXTRA_FROM_SERVICE, false) || intent.getBooleanExtra(
                        EXTRA_IS_CHOOSE,
                        false
                    )
                ) {
                    replyIntentManager = ReplyIntentManager(
                        intent,
                        applicationContext,
                        packageName,
                        beehiveContainer.credentialRepository,
                        ::returnToService,
                        ::cancelAutofill
                    )
                }

                AuthNavHost(
                    onNavigateToHome = {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    replyIntentManager = replyIntentManager,
                    promptManager = promptManager,
                )
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelAutofill()
    }

    private fun returnToService(replyIntent: Intent?) {
        setResult(Activity.RESULT_OK, replyIntent)
        finish()
    }

    private fun cancelAutofill() {
        setResult(RESULT_CANCELED)
        finish()
    }
}