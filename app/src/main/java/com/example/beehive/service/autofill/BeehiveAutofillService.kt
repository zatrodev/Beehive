package com.example.beehive.service.autofill

import android.app.assist.AssistStructure
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
import com.example.beehive.data.BeehiveContainer
import com.example.beehive.data.BeehiveContainerImpl
import com.example.beehive.data.passwords.Password
import com.example.beehive.domain.GetPasswordsWithUserByUriUseCase
import com.example.beehive.service.autofill.parsing.parseStructure
import com.example.beehive.utils.generatePassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class BeehiveAutofillService : AutofillService() {
    private lateinit var getPasswordsWithUserByUriUseCase: GetPasswordsWithUserByUriUseCase
    private lateinit var beehiveContainer: BeehiveContainer
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    companion object {
        private const val DEFAULT_NEW_PASSWORD_LENGTH = 12
    }

    override fun onConnected() {
        super.onConnected()
        beehiveContainer = BeehiveContainerImpl(this.applicationContext)
        getPasswordsWithUserByUriUseCase = GetPasswordsWithUserByUriUseCase(
            beehiveContainer.passwordsRepository,
            beehiveContainer.usersRepository
        )
    }

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback,
    ) {
        /*
        TODO: account for more than two text fields (?)
              save request
        */
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        val (usernameId: AutofillId?, passwordId: AutofillId?, _, _, appUri: String) = parseStructure(
            structure
        )

        coroutineScope.launch {
            getPasswordsWithUserByUriUseCase(appUri).collectLatest { passwords ->
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
                                AutofillValue.forText(password.username.ifBlank { password.user.email }),
                                createPresentation(
                                    password.username,
                                    password.user.email,
                                    isFirstIteration = i == 0
                                )
                            )
                            .setValue(
                                passwordId,
                                AutofillValue.forText(password.password)
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

        val (_, _, username: String, password: String, _) = parseStructure(
            structure
        )

        if (username.isEmpty() || password.isEmpty()) {
            callback.onFailure("No text fields found")
            return
        }

        coroutineScope.launch {
            beehiveContainer.passwordsRepository.insertPassword(
                Password(
                    beehiveContainer.passwordsRepository.countPasswords() + 1,
                    username,
                    password
                )
            )

            callback.onSuccess()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
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

    private fun createPresentation(
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

data class ParsedStructure(
    var usernameId: AutofillId? = null,
    var passwordId: AutofillId? = null,
    var usernameValue: String = "",
    var passwordValue: String = "",
    var appUri: String = "",
)

