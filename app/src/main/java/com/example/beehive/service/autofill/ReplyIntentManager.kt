package com.example.beehive.service.autofill

import android.app.assist.AssistStructure
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.os.PersistableBundle
import android.service.autofill.Dataset
import android.view.autofill.AutofillId
import android.view.autofill.AutofillManager.EXTRA_ASSIST_STRUCTURE
import android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT
import android.view.autofill.AutofillValue
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.EXTRA_IS_CHOOSE
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.EXTRA_PASSWORD_ID
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.notUsedPresentation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class ReplyIntentManager(
    private val intent: Intent,
    private val clipboardManager: ClipboardManager,
    private val packageName: String,
    private val credentialRepository: CredentialRepository,
    private val returnToService: (Intent?) -> Unit,
    private val cancelAutofill: () -> Unit,
) {
    private var usernameId: AutofillId? = null
    private var passwordId: AutofillId? = null
    private var focusedId: AutofillId? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var replyIntent: Intent? = null

    init {
        try {
            val structure =
                intent.getParcelableExtra<AssistStructure>(EXTRA_ASSIST_STRUCTURE)

            if (structure != null) {
                val parser = Parser(structure)
                val (usernameId, passwordId, _, _, focusedId, _) = parser.autofillData

                this.usernameId = usernameId
                this.passwordId = passwordId
                this.focusedId = focusedId

                if (usernameId == null && passwordId == null && focusedId == null)
                    throw NullPointerException("No autofill-able text fields found!")
            }
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

            if (usernameId == null && passwordId == null && focusedId == null) {
                val clipData = ClipData.newPlainText(
                    "",
                    credentialAndUser.credential.password
                )
                clipData.apply {
                    description.extras = PersistableBundle().apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
                        } else {
                            putBoolean("android.content.extra.IS_SENSITIVE", true)
                        }
                    }
                }
                clipboardManager.setPrimaryClip(clipData)
            } else {
                val responseDatasetBuilder = if (usernameId == null || passwordId == null) {
                    Dataset.Builder()
                        .setValue(
                            focusedId!!,
                            AutofillValue.forText(credentialAndUser.credential.password),
                            notUsedPresentation(packageName)
                        )
                } else Dataset.Builder()
                    .setValue(
                        usernameId!!,
                        AutofillValue.forText(credentialAndUser.credential.username.ifBlank { credentialAndUser.user.email }),
                        notUsedPresentation(packageName)
                    )
                    .setValue(
                        passwordId!!,
                        AutofillValue.forText(credentialAndUser.credential.password),
                        notUsedPresentation(packageName)
                    )

                replyIntent = Intent().apply {
                    putExtra(EXTRA_AUTHENTICATION_RESULT, responseDatasetBuilder.build())
                }
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
