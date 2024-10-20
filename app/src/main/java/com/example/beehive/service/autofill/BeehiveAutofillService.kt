package com.example.beehive.service.autofill

import android.app.PendingIntent
import android.app.assist.AssistStructure
import android.content.Intent
import android.content.IntentSender
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillContext
import android.service.autofill.FillRequest
import android.service.autofill.FillRequest.FLAG_MANUAL_REQUEST
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveInfo
import android.service.autofill.SaveRequest
import android.view.View
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.example.beehive.R
import com.example.beehive.auth.AuthActivity
import com.example.beehive.auth.SecretKeyManager
import com.example.beehive.data.container.AuthContainer
import com.example.beehive.data.container.AuthContainerImpl
import com.example.beehive.data.container.BeehiveContainer
import com.example.beehive.data.container.BeehiveContainerImpl
import com.example.beehive.data.credential.Credential
import com.example.beehive.data.credential.PasswordApp
import com.example.beehive.data.user.User
import com.example.beehive.utils.generatePassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
class BeehiveAutofillService : AutofillService() {
    private lateinit var beehiveContainer: BeehiveContainer
    private lateinit var authContainer: AuthContainer
    private lateinit var parser: Parser
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val DEFAULT_NEW_PASSWORD_LENGTH = 12

        const val EXTRA_PASSWORD_ID = "com.example.beehive.PasswordId"
        const val EXTRA_FROM_SERVICE = "com.example.beehive.FromService"
        const val EXTRA_IS_CHOOSE = "com.example.beehive.IsChoose"

        fun notUsedPresentation(packageName: String): RemoteViews {
            return RemoteViews(packageName, android.R.layout.simple_list_item_1)
        }

        fun createPresentation(
            packageName: String,
            title: String,
            subtitle: String = "",
            appIcon: Drawable? = null,
        ): RemoteViews {
            val newPresentation = RemoteViews(packageName, R.layout.autofill_suggestion_item)

            if (appIcon != null) {
                newPresentation.setImageViewBitmap(R.id.app_icon, appIcon.toBitmap())
                newPresentation.setViewVisibility(R.id.app_icon, View.VISIBLE)
            }

            newPresentation.setTextViewText(R.id.title, title)

            if (subtitle.isNotBlank()) {
                newPresentation.setTextViewText(R.id.subtitle, subtitle)
                newPresentation.setViewVisibility(R.id.subtitle, View.VISIBLE)
            }

            return newPresentation
        }
    }

    override fun onConnected() {
        super.onConnected()
        authContainer = AuthContainerImpl(this.applicationContext)
    }

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback,
    ) {
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        parser = Parser(structure)
        coroutineScope.launch {
            SecretKeyManager.setPassphrase(authContainer.dataStore)
            beehiveContainer = BeehiveContainerImpl(applicationContext)

            val credentials =
                beehiveContainer.credentialRepository.getCredentialsByApp(parser.parsedStructure.appUri)
                    .first()

            if (credentials.isEmpty()) {
                if (request.flags and FLAG_MANUAL_REQUEST == FLAG_MANUAL_REQUEST) {
                    parser.parsedStructure.focusedFieldId?.let {
                        val fillResponseBuilder = FillResponse.Builder()
                        fillResponseBuilder.addChoosePasswordOption(parser.parsedStructure.focusedFieldId!!)

                        callback.onSuccess(fillResponseBuilder.build())
                        return@launch
                    }
                } else {
                    beehiveContainer.userRepository.getAllUsers().first().first()
                        .let { defaultUser ->
                            val requestFillResponseBuilder =
                                requestSignUp(
                                    request.clientState,
                                    parser.parsedStructure.usernameId,
                                    parser.parsedStructure.passwordId,
                                    defaultUser
                                )

                            requestFillResponseBuilder?.let { builder ->
                                val autofillId =
                                    arrayOf(
                                        parser.parsedStructure.usernameId,
                                        parser.parsedStructure.passwordId
                                    ).firstOrNull { id -> id != null }

                                autofillId?.let {
                                    builder.addChoosePasswordOption(it)
                                }

                                callback.onSuccess(builder.build())
                            }

                            return@launch
                        }
                }
            }

            if (parser.parsedStructure.passwordId == null) {
                callback.onFailure("Unable to autofill.")
                return@launch
            }

            val fillResponseBuilder = FillResponse.Builder()
            credentials.forEach { credential ->
                fillResponseBuilder.addDataset(
                    Dataset.Builder()
                        .setValue(
                            parser.parsedStructure.usernameId!!,
                            null,
                            createPresentation(
                                packageName,
                                credential.user.email,
                                credential.credential.username,
                            )
                        )
                        .setAuthentication(
                            createIntentSender(id = credential.credential.id)
                        )
                        .build()
                )
            }

            fillResponseBuilder.addChoosePasswordOption(parser.parsedStructure.passwordId!!)
            callback.onSuccess(fillResponseBuilder.build())

        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        var parsedStructure = Parser.ParsedStructure()

        val clientState = request.clientState
        val usernameId: AutofillId? = clientState?.getParcelable("usernameId")
        val passwordId: AutofillId? = clientState?.getParcelable("passwordId")

        if (usernameId != null && passwordId != null) {
            parsedStructure.usernameValue =
                Parser.findNodeByAutofillId(
                    context[0].structure,
                    usernameId
                )?.autofillValue.toString()

            parsedStructure.usernameValue =
                Parser.findNodeByAutofillId(
                    context[1].structure,
                    usernameId
                )?.autofillValue.toString()
        } else {
            parsedStructure = Parser(structure).parsedStructure
            if (parsedStructure.usernameValue.isEmpty() || parsedStructure.passwordValue.isEmpty()) {
                callback.onFailure("No text fields found")
                return
            }
        }

        coroutineScope.launch {
            beehiveContainer.credentialRepository.insertCredential(
                Credential(
                    id = beehiveContainer.credentialRepository.getNextId() + 1,
                    username = parsedStructure.usernameValue,
                    password = parsedStructure.passwordValue,
                    userId = beehiveContainer.userRepository.getNextId(),
                    app = PasswordApp(
                        name = parsedStructure.appUri,
                        packageName = parsedStructure.appUri
                    )
                )
            )

            callback.onSuccess()
        }
    }

    private fun createIntentSender(
        id: Int? = null,
    ): IntentSender {
        val authIntent = Intent(this, AuthActivity::class.java).apply {
            if (id == null)
                putExtra(EXTRA_IS_CHOOSE, true)
            else {
                putExtra(EXTRA_PASSWORD_ID, id)
                putExtra(EXTRA_FROM_SERVICE, true)
            }
        }

        val intentSender: IntentSender = PendingIntent.getActivity(
            this,
            id ?: -1,
            authIntent,
            PendingIntent.FLAG_MUTABLE
        ).intentSender

        return intentSender
    }

    private fun FillResponse.Builder.addChoosePasswordOption(
        autofillId: AutofillId,
    ) {
        this.addDataset(
            Dataset.Builder()
                .setValue(
                    autofillId,
                    null,
                    createPresentation(
                        packageName,
                        "Choose a saved password",
                        appIcon = AppCompatResources.getDrawable(
                            applicationContext,
                            R.mipmap.ic_launcher_round
                        )
                    )
                )
                .setAuthentication(
                    createIntentSender()
                )
                .build()
        )
    }

    private fun requestSignUp(
        clientState: Bundle?,
        usernameId: AutofillId?,
        passwordId: AutofillId?,
        user: User,
    ): FillResponse.Builder? {
        val newPassword = generatePassword(DEFAULT_NEW_PASSWORD_LENGTH)
        val presentation =
            createPresentation(
                packageName,
                "Create password for this app? ($newPassword)",
                appIcon = AppCompatResources.getDrawable(
                    applicationContext,
                    R.drawable.ic_launcher_foreground
                )
            )

        val usernameValue = AutofillValue.forText(user.email)
        val passwordValue = AutofillValue.forText(newPassword)

        if (usernameId != null && passwordId != null) {
            return FillResponse.Builder().addDataset(
                Dataset.Builder()
                    .setValue(usernameId, usernameValue, presentation)
                    .setValue(passwordId, passwordValue, presentation)
                    .build()
            )
                .setSaveInfo(
                    SaveInfo.Builder(
                        SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                        arrayOf(usernameId, passwordId)
                    ).build()
                )
        }

        if (usernameId != null) {
            val newClientState = Bundle()
            newClientState.putParcelable("usernameId", usernameId)
            newClientState.putParcelable("passwordValue", passwordValue)

            return FillResponse.Builder()
                .addDataset(
                    Dataset.Builder()
                        .setValue(usernameId, usernameValue, presentation)
                        .build()
                )
                .setClientState(newClientState)
                .setSaveInfo(
                    SaveInfo.Builder(
                        SaveInfo.SAVE_DATA_TYPE_USERNAME,
                        arrayOf(usernameId)
                    ).build()
                )
        }

        if (passwordId != null) {
            clientState?.putParcelable("passwordId", passwordId)
            val parceledUsernameId: AutofillId =
                clientState?.getParcelable("usernameId") ?: return null
            val parceledPasswordValue: AutofillValue? = clientState.getParcelable("passwordValue")

            return FillResponse.Builder()
                .addDataset(
                    Dataset.Builder()
                        .setValue(
                            passwordId,
                            AutofillValue.forText(newPassword),
                            createPresentation(
                                packageName,
                                parceledPasswordValue?.textValue.toString()
                            )
                        )
                        .build()
                )
                .setClientState(clientState)
                .setSaveInfo(
                    SaveInfo.Builder(
                        SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                        arrayOf(parceledUsernameId, passwordId)
                    )
                        .setFlags(SaveInfo.FLAG_SAVE_ON_ALL_VIEWS_INVISIBLE)
                        .build()
                )
        }


        return null
    }

//    fun parseStructure(
//        structure: AssistStructure,
//    ): ParsedStructure {
//        return structure.windows
//            .mapNotNull { window -> window.rootViewNode }
//            .fold(ParsedStructure()) { parsed, node -> getAutofillableFields(node, parsed) }
//    }
//

//
//    private fun getAutofillableFields(
//        viewNode: ViewNode,
//        parsedStructure: ParsedStructure = ParsedStructure(),
//    ): ParsedStructure {
//        if (parsedStructure.usernameId != null && parsedStructure.passwordId != null)
//            return parsedStructure
//
//        parsedStructure.usernameId = parsedStructure.usernameId ?: identifyEmailField(viewNode)
//        parsedStructure.usernameValue = parsedStructure.usernameValue.ifEmpty {
//            parsedStructure.usernameId?.let { viewNode.autofillValue?.toString() } ?: ""
//        }
//
//        parsedStructure.passwordId = parsedStructure.passwordId ?: identifyPasswordField(viewNode)
//        parsedStructure.passwordValue = parsedStructure.passwordValue.ifEmpty {
//            parsedStructure.passwordId?.let { viewNode.autofillValue?.toString() } ?: ""
//        }
//
//        parsedStructure.focusedFieldId =
//            parsedStructure.focusedFieldId ?: identifyFocusedField(viewNode)
//
//        parsedStructure.appUri = viewNode.idPackage ?: parsedStructure.appUri
//
//        for (i in 0 until viewNode.childCount)
//            getAutofillableFields(viewNode.getChildAt(i), parsedStructure)
//
//        return parsedStructure
//    }
//
//    private fun identifyFocusedField(
//        viewNode: ViewNode,
//    ): AutofillId? {
//        if (!viewNode.isFocused)
//            return null
//
//        val className = viewNode.className ?: return null
//        if (!className.contains("EditText"))
//            return null
//
//        return viewNode.autofillId
//    }
//
//    private fun identifyEmailField(
//        viewNode: ViewNode,
//    ): AutofillId? {
//        val className = viewNode.className ?: return null
//        if (!className.contains("EditText")) return null
//
//        if (viewNode.autofillHints?.contains(AUTOFILL_HINT_EMAIL_ADDRESS) == true || viewNode.autofillHints?.contains(
//                AUTOFILL_HINT_USERNAME
//            ) == true
//        )
//            return viewNode.autofillId
//
//
//        if (viewNode.text?.contains(
//                "email",
//                ignoreCase = true
//            ) == true || viewNode.hint?.contains("email", ignoreCase = true) == true
//        ) return viewNode.autofillId
//
//        if (viewNode.idEntry?.isNotBlank() == true)
//            return null
//
//        if (viewNode.inputType and (InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) != 0)
//            return viewNode.autofillId
//
//        return null
//    }
//
//    private fun identifyPasswordField(
//        viewNode: ViewNode,
//    ): AutofillId? {
//        val className = viewNode.className ?: return null
//        if (!className.contains("EditText")) return null
//
//        if (viewNode.autofillHints?.contains(AUTOFILL_HINT_PASSWORD) == true)
//            return viewNode.autofillId
//
//        if (viewNode.inputType and InputType.TYPE_TEXT_VARIATION_PASSWORD != 0)
//            return viewNode.autofillId
//
//        if (viewNode.text?.contains("password", ignoreCase = true) == true ||
//            viewNode.hint?.contains("password", ignoreCase = true) == true
//        ) {
//            return viewNode.autofillId
//        }
//
//        return null
//    }
}
