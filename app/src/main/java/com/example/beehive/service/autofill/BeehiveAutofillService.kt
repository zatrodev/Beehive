package com.example.beehive.service.autofill

import android.app.PendingIntent
import android.app.assist.AssistStructure
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillContext
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveInfo
import android.service.autofill.SaveRequest
import android.view.View
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.example.beehive.R
import com.example.beehive.auth.AuthActivity
import com.example.beehive.data.BeehiveContainer
import com.example.beehive.data.BeehiveContainerImpl
import com.example.beehive.data.credential.Credential
import com.example.beehive.data.credential.PasswordApp
import com.example.beehive.domain.GetCredentialsWithUserByUriUseCase
import com.example.beehive.service.autofill.parsing.parseStructure
import com.example.beehive.utils.generatePassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class BeehiveAutofillService : AutofillService() {
    private lateinit var getCredentialsWithUserByUriUseCase: GetCredentialsWithUserByUriUseCase
    private lateinit var beehiveContainer: BeehiveContainer
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    companion object {
        private const val DEFAULT_NEW_PASSWORD_LENGTH = 12

        const val EXTRA_PASSWORD_ID = "passwordId"
        const val EXTRA_FROM_SERVICE = "fromService"

        fun createPresentation(
            packageName: String,
            title: String = "",
            subtitle: String = "",
            isFirstIteration: Boolean = true,
        ): RemoteViews {
            val newPresentation =
                RemoteViews(packageName, R.xml.password_autofill)

            if (isFirstIteration)
                newPresentation.setViewVisibility(R.id.app_name, View.VISIBLE)

            if (title.isBlank()) {
                newPresentation.setTextViewText(R.id.line_a, subtitle)
                newPresentation.setTextViewText(R.id.line_b, "<no username>")
            } else {
                newPresentation.setTextViewText(R.id.line_a, title)
                newPresentation.setTextViewText(R.id.line_b, subtitle)
            }

            return newPresentation
        }
    }

    override fun onConnected() {
        super.onConnected()
        beehiveContainer = BeehiveContainerImpl(this.applicationContext)
        getCredentialsWithUserByUriUseCase = GetCredentialsWithUserByUriUseCase(
            beehiveContainer.credentialRepository,
            beehiveContainer.userRepository
        )
    }

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback,
    ) {
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        val (usernameId: AutofillId?, passwordId: AutofillId?, _, _, appUri: String) = parseStructure(
            structure
        )

        coroutineScope.launch {
            getCredentialsWithUserByUriUseCase(appUri).collectLatest { passwords ->
                if (passwords.isEmpty()) {
                    // sign up
                    val requestFillResponse = requestSignUp(usernameId, passwordId)
                    if (requestFillResponse != null) {
                        callback.onSuccess(requestFillResponse)
                    }

                    return@collectLatest
                }

                if (usernameId == null || passwordId == null) {
                    callback.onFailure("Unable to autofill.")
                    return@collectLatest
                }

                val fillResponseBuilder = FillResponse.Builder()
                passwords.forEachIndexed { i, password ->
                    fillResponseBuilder.addDataset(
                        Dataset.Builder()
                            .setValue(
                                usernameId,
                                null,
                                createPresentation(
                                    packageName,
                                    password.username,
                                    password.user.email,
                                    isFirstIteration = i == 0
                                )
                            )
                            .setAuthentication(
                                createIntentSender(password.id)
                            )
                            .build()
                    )
                }

                callback.onSuccess(fillResponseBuilder.build())
            }
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        val (_, _, username: String, password: String, packageName) = parseStructure(
            structure
        )

        if (username.isEmpty() || password.isEmpty()) {
            callback.onFailure("No text fields found")
            return
        }

        coroutineScope.launch {
            beehiveContainer.credentialRepository.insertCredential(
                Credential(
                    id = beehiveContainer.credentialRepository.getNextId() + 1,
                    username = username,
                    password = password,
                    userId = beehiveContainer.userRepository.getNextId(),
                    app = PasswordApp(
                        // TODO: edit name
                        name = "Instagram",
                        packageName = packageName
                    )
                )
            )

            callback.onSuccess()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun createIntentSender(passwordId: Int): IntentSender {
        val authIntent = Intent(this, AuthActivity::class.java).apply {
            putExtra(EXTRA_PASSWORD_ID, passwordId)
            putExtra(EXTRA_FROM_SERVICE, true)
        }
        val intentSender: IntentSender = PendingIntent.getActivity(
            this,
            passwordId,
            authIntent,
            PendingIntent.FLAG_MUTABLE
        ).intentSender

        return intentSender
    }

    private fun requestSignUp(usernameId: AutofillId?, passwordId: AutofillId?): FillResponse? {
        val newPassword = generatePassword(DEFAULT_NEW_PASSWORD_LENGTH)
        val presentation = createPresentation("Create password for this app? ($newPassword)")

        if (usernameId != null && passwordId != null) {
            return FillResponse.Builder().addDataset(
                Dataset.Builder()
                    .setValue(usernameId, AutofillValue.forText("some user"), presentation)
                    .setValue(passwordId, AutofillValue.forText(newPassword), presentation)
                    .build()
            )
                .setSaveInfo(
                    SaveInfo.Builder(
                        SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                        arrayOf(usernameId, passwordId)
                    ).build()
                )
                .build()
        }

        if (usernameId != null) {
            val clientState = Bundle().apply { putParcelable("usernameId", usernameId) }

            return FillResponse.Builder()
                .addDataset(
                    Dataset.Builder()
                        .setValue(usernameId, AutofillValue.forText("some user"), presentation)
                        .build()
                )
                .setClientState(clientState)
                .setSaveInfo(
                    SaveInfo.Builder(
                        SaveInfo.SAVE_DATA_TYPE_USERNAME,
                        arrayOf(usernameId)
                    ).build()
                )
                .build()
        }

        if (passwordId != null) {
            val clientState = Bundle().apply {
                putParcelable("passwordId", passwordId)
            }
            val parceledUsernameId: AutofillId = clientState.getParcelable("usernameId")!!

            return FillResponse.Builder()
                .setClientState(clientState)
                .setSaveInfo(
                    SaveInfo.Builder(
                        SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                        arrayOf(parceledUsernameId, passwordId)
                    )
                        .setFlags(SaveInfo.FLAG_SAVE_ON_ALL_VIEWS_INVISIBLE)
                        .build()
                )
                .build()
        }


        return null
    }


}

data class ParsedStructure(
    var usernameId: AutofillId? = null,
    var passwordId: AutofillId? = null,
    var usernameValue: String = "",
    var passwordValue: String = "",
    var appUri: String = "",
)

