package com.example.beehive.auth

import android.app.Activity
import android.app.assist.AssistStructure
import android.content.Intent
import android.os.Bundle
import android.service.autofill.Dataset
import android.view.autofill.AutofillId
import android.view.autofill.AutofillManager.EXTRA_ASSIST_STRUCTURE
import android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT
import android.view.autofill.AutofillValue
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.beehive.MainActivity
import com.example.beehive.auth.navigation.AuthNavHost
import com.example.beehive.data.container.AuthContainer
import com.example.beehive.data.container.AuthContainerImpl
import com.example.beehive.data.container.BeehiveContainer
import com.example.beehive.data.container.BeehiveContainerImpl
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.EXTRA_FROM_SERVICE
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.EXTRA_IS_CHOOSE
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.EXTRA_PASSWORD_ID
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.createPresentation
import com.example.beehive.service.autofill.parseStructure
import com.example.beehive.ui.theme.BeehiveTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    private fun returnToService(replyIntent: Intent) {
        setResult(Activity.RESULT_OK, replyIntent)
        finish()
    }

    private fun cancelAutofill() {
        setResult(RESULT_CANCELED)
        finish()
    }
}

@Suppress("DEPRECATION")
class ReplyIntentManager(
    private val intent: Intent,
    private val packageName: String,
    private val credentialRepository: CredentialRepository,
    private val returnToService: (Intent) -> Unit,
    private val cancelAutofill: () -> Unit,
) {
    private var usernameId: AutofillId? = null
    private var passwordId: AutofillId? = null
    private var focusedId: AutofillId? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var replyIntent = Intent()

    init {
        try {
            val structure =
                intent.getParcelableExtra<AssistStructure>(EXTRA_ASSIST_STRUCTURE)
                    ?: throw NullPointerException("Structure can't be null!")

            val (usernameId, passwordId, _, _, focusedId, _) = parseStructure(structure)
            if (usernameId == null && passwordId == null && focusedId == null)
                throw NullPointerException("No autofill-able text fields found!")

            this.usernameId = usernameId
            this.passwordId = passwordId
            this.focusedId = focusedId
        } catch (e: NullPointerException) {
            cancelAutofill()
        }
    }

    fun handleAutofill(navigateToChooseCredential: () -> Unit) {
        if (intent.getBooleanExtra(
                EXTRA_IS_CHOOSE, false
            )
        ) {
            navigateToChooseCredential()
        } else {
            val id = intent.getIntExtra(EXTRA_PASSWORD_ID, -1)

            if (id == -1) {
                cancelAutofill()
                return
            }

            coroutineScope.launch {
                setReply(id)
                sendReply()
            }
        }
    }

    suspend fun setReply(id: Int) {
        try {
            val credentialAndUser =
                credentialRepository.getCredentialAndUser(id).first()

            val responseDatasetBuilder = if (usernameId == null || passwordId == null) {
                Dataset.Builder()
                    .setValue(
                        focusedId!!,
                        AutofillValue.forText(credentialAndUser.credential.password),
                        createPresentation(
                            packageName,
                            credentialAndUser.user.email,
                            credentialAndUser.credential.username,
                        )
                    )
            } else Dataset.Builder()
                .setValue(
                    usernameId!!,
                    AutofillValue.forText(credentialAndUser.credential.username.ifBlank { credentialAndUser.user.email }),
                    createPresentation(
                        packageName,
                        credentialAndUser.user.email,
                        credentialAndUser.credential.username,
                    )
                )
                .setValue(
                    passwordId!!,
                    AutofillValue.forText(credentialAndUser.credential.password),
                    createPresentation(
                        packageName,
                        credentialAndUser.user.email,
                        credentialAndUser.credential.username,
                    )
                )

            replyIntent = Intent().apply {
                putExtra(EXTRA_AUTHENTICATION_RESULT, responseDatasetBuilder.build())
            }
        } catch (e: Exception) {
            cancelAutofill()
            return
        }
    }

    fun sendReply() {
        returnToService(replyIntent)
    }
}
