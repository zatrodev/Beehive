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
import com.example.beehive.domain.GetInstalledAppsUseCase
import com.example.beehive.utils.generatePassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class BeehiveAutofillService : AutofillService() {
    private lateinit var beehiveContainer: BeehiveContainer
    private lateinit var authContainer: AuthContainer
    private lateinit var getInstalledAppsUseCase: GetInstalledAppsUseCase
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    companion object {
        private const val DEFAULT_NEW_PASSWORD_LENGTH = 12

        const val EXTRA_PASSWORD_ID = "passwordId"
        const val EXTRA_FROM_SERVICE = "fromService"
        const val EXTRA_IS_CHOOSE = "fromSearch"

        fun createPresentation(
            packageName: String,
            title: String,
            subtitle: String = "",
            appIcon: Drawable? = null,
        ): RemoteViews {
            val newPresentation = RemoteViews(packageName, R.layout.autofill_item)

            newPresentation.setImageViewBitmap(R.id.app_icon, appIcon?.toBitmap())
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
        getInstalledAppsUseCase = GetInstalledAppsUseCase(packageManager)
        authContainer = AuthContainerImpl(this.applicationContext)
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

        coroutineScope.launch(Dispatchers.IO) {
            SecretKeyManager.setPassphrase(authContainer.dataStore)
            beehiveContainer = BeehiveContainerImpl(applicationContext)

            val credentials =
                beehiveContainer.credentialRepository.getCredentialsByApp(appUri).first()

            if (credentials.isEmpty()) {
                // sign up
                beehiveContainer.userRepository.getAllUsers().first().first().let { defaultUser ->
                    val requestFillResponse =
                        requestSignUp(
                            request.clientState,
                            usernameId,
                            passwordId,
                            defaultUser
                        )
                    if (requestFillResponse != null) {
                        callback.onSuccess(requestFillResponse)
                    }

                    return@launch
                }
            }

            if (usernameId == null || passwordId == null) {
                callback.onFailure("Unable to autofill.")
                return@launch
            }

            val fillResponseBuilder = FillResponse.Builder()
            credentials.forEach { credential ->
                credential.apply {
                    credential.credential.app.icon = getInstalledAppsUseCase().find {
                        it.packageName == credential.credential.app.packageName
                    }?.icon
                }

                fillResponseBuilder.addDataset(
                    Dataset.Builder()
                        .setValue(
                            usernameId,
                            null,
                            createPresentation(
                                packageName,
                                credential.user.email,
                                credential.credential.username,
                                credential.credential.app.icon,
                            )
                        )
                        .setAuthentication(
                            createIntentSender(credential.credential.id)
                        )
                        .build()
                )
            }

            fillResponseBuilder.addDataset(
                Dataset.Builder()
                    .setValue(
                        usernameId,
                        null,
                        createPresentation(
                            packageName,
                            "Choose a password",
                            appIcon = AppCompatResources.getDrawable(
                                applicationContext,
                                R.drawable.ic_launcher_foreground
                            )
                        )
                    )
                    .setAuthentication(
                        createIntentSender()
                    )
                    .build()
            )
            callback.onSuccess(fillResponseBuilder.build())

        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        var parsedStructure = ParsedStructure()

        val clientState = request.clientState
        val usernameId: AutofillId? = clientState?.getParcelable("usernameId")
        val passwordId: AutofillId? = clientState?.getParcelable("passwordId")

        if (usernameId != null && passwordId != null) {
            parsedStructure.usernameValue = context.firstNotNullOfOrNull { fillContext ->
                getValueFromStructure(fillContext.structure, usernameId)
            } ?: return

            parsedStructure.passwordValue = context.firstNotNullOfOrNull { fillContext ->
                getValueFromStructure(fillContext.structure, passwordId)
            } ?: return
        } else {
            parsedStructure = parseStructure(structure)
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
                        name = parsedStructure.appName,
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

    private fun createIntentSender(passwordId: Int? = null): IntentSender {
        val authIntent = Intent(this, AuthActivity::class.java).apply {
            if (passwordId == null)
                putExtra(EXTRA_IS_CHOOSE, true)
            else {
                putExtra(EXTRA_PASSWORD_ID, passwordId)
                putExtra(EXTRA_FROM_SERVICE, true)
            }
        }
        val intentSender: IntentSender = PendingIntent.getActivity(
            this,
            passwordId ?: -1,
            authIntent,
            PendingIntent.FLAG_MUTABLE
        ).intentSender

        return intentSender
    }

    private fun requestSignUp(
        clientState: Bundle?,
        usernameId: AutofillId?,
        passwordId: AutofillId?,
        user: User,
    ): FillResponse? {
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
                .build()
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
                .build()
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
                .build()
        }


        return null
    }
}
