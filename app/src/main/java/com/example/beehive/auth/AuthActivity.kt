package com.example.beehive.auth

import android.app.Activity
import android.app.assist.AssistStructure
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.autofill.Dataset
import android.view.autofill.AutofillManager.EXTRA_ASSIST_STRUCTURE
import android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT
import android.view.autofill.AutofillValue
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.dataStore
import com.example.beehive.MainActivity
import com.example.beehive.data.BeehiveContainerImpl
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.EXTRA_FROM_SERVICE
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.EXTRA_PASSWORD_ID
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.createPresentation
import com.example.beehive.service.autofill.parsing.parseStructure
import com.example.beehive.ui.theme.BeehiveTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class AuthActivity : AppCompatActivity() {
    private lateinit var credentialRepository: CredentialRepository

    private val Context.dataStore by dataStore(
        fileName = "room-key",
        serializer = RoomKeySerializer(CryptoManager())
    )
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val promptManager by lazy {
        BiometricPromptManager(
            this,
            dataStore
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                            credentialRepository.getCredentialWithUser(id).map {
                                val responseDataset: Dataset = Dataset.Builder()
                                    .setValue(
                                        usernameId,
                                        AutofillValue.forText(it.credential.username.ifBlank { it.user.email }),
                                        createPresentation(
                                            packageName,
                                            it.credential.username,
                                            it.user.email,
                                            isFirstIteration = false
                                        )
                                    )
                                    .setValue(
                                        passwordId,
                                        AutofillValue.forText(it.credential.password),
                                        createPresentation(
                                            packageName,
                                            it.credential.username,
                                            it.user.email,
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