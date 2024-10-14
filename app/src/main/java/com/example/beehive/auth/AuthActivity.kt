package com.example.beehive.auth

import android.app.Activity
import android.app.assist.AssistStructure
import android.content.Intent
import android.os.Bundle
import android.service.autofill.Dataset
import android.view.autofill.AutofillManager.EXTRA_ASSIST_STRUCTURE
import android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT
import android.view.autofill.AutofillValue
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.beehive.MainActivity
import com.example.beehive.data.AuthContainer
import com.example.beehive.data.AuthContainerImpl
import com.example.beehive.data.BeehiveContainerImpl
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.EXTRA_FROM_SERVICE
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.EXTRA_PASSWORD_ID
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.createPresentation
import com.example.beehive.service.autofill.parseStructure
import com.example.beehive.ui.theme.BeehiveTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class AuthActivity : AppCompatActivity() {
    private lateinit var authContainer: AuthContainer
    private lateinit var credentialRepository: CredentialRepository
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val promptManager by lazy {
        BiometricPromptManager(
            this,
            authContainer.dataStore
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authContainer = AuthContainerImpl(this.applicationContext)
        enableEdgeToEdge()
        setContent {
            BeehiveTheme {
                AuthScreen(
                    onNavigateToHome = {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    returnToService = {
                        credentialRepository =
                            BeehiveContainerImpl(this.applicationContext).credentialRepository

                        val structure =
                            intent.getParcelableExtra<AssistStructure>(EXTRA_ASSIST_STRUCTURE)
                        val id = intent.getIntExtra(EXTRA_PASSWORD_ID, -1)

                        if (structure == null || id == -1) {
                            cancelAutofill()
                            return@AuthScreen
                        }

                        val (usernameId, passwordId, _, _, _) = parseStructure(structure)
                        if (usernameId == null || passwordId == null) {
                            cancelAutofill()
                            return@AuthScreen
                        }

                        coroutineScope.launch(Dispatchers.IO) {
                            val credentialAndUser =
                                credentialRepository.getCredentialAndUser(id).first()
                            val responseDataset: Dataset = Dataset.Builder()
                                .setValue(
                                    usernameId,
                                    AutofillValue.forText(credentialAndUser.credential.username.ifBlank { credentialAndUser.user.email }),
                                    createPresentation(
                                        packageName,
                                        credentialAndUser.user.email,
                                        credentialAndUser.credential.username,
                                        isFirstIteration = false
                                    )
                                )
                                .setValue(
                                    passwordId,
                                    AutofillValue.forText(credentialAndUser.credential.password),
                                    createPresentation(
                                        packageName,
                                        credentialAndUser.user.email,
                                        credentialAndUser.credential.username,
                                        isFirstIteration = false
                                    )
                                )
                                .build()
                            val replyIntent = Intent().apply {
                                putExtra(EXTRA_AUTHENTICATION_RESULT, responseDataset)
                            }

                            setResult(Activity.RESULT_OK, replyIntent)
                            finish()
                        }
                    },
                    promptManager = promptManager,
                    isFromService = intent.getBooleanExtra(EXTRA_FROM_SERVICE, false)
                )
            }
        }
    }

    private fun cancelAutofill() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}