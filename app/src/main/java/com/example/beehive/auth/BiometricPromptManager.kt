package com.example.beehive.auth

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.datastore.core.DataStore
import com.example.beehive.utils.generatePassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class BiometricPromptManager(
    private val activity: AppCompatActivity,
    private val dataStore: DataStore<String>,
) {
    private val biometricManager = BiometricManager.from(activity)
    private val resultChannel = Channel<BiometricResult>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    companion object {
        val ALLOWED_AUTHENTICATORS =
            if (Build.VERSION.SDK_INT >= 30) BIOMETRIC_STRONG or DEVICE_CREDENTIAL else BIOMETRIC_STRONG
    }

    val promptResults = resultChannel.receiveAsFlow()

    fun authenticateWithBiometric(
        title: String,
    ) {
        when (biometricManager.canAuthenticate(ALLOWED_AUTHENTICATORS)) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                resultChannel.trySend(BiometricResult.HardwareUnavailable)
                return
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                resultChannel.trySend(BiometricResult.AuthenticationNotSet)
                return
            }

            else -> Unit
        }
        val promptInfo = PromptInfo.Builder()
            .setTitle(title)
            .setAllowedAuthenticators(ALLOWED_AUTHENTICATORS)

        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    resultChannel.trySend(BiometricResult.AuthenticationError(errString.toString()))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    setRoomPassphrase()
                    resultChannel.trySend(BiometricResult.AuthenticationSuccess)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    resultChannel.trySend(BiometricResult.AuthenticationFailed)
                }
            }
        )

        prompt.authenticate(
            promptInfo.build()
        )
    }

    private fun setRoomPassphrase() {
        coroutineScope.launch {
            dataStore.data.collectLatest { randomKey ->
                if (randomKey.isBlank())
                    CryptoManager.passphrase = dataStore.updateData {
                        generatePassword(16)
                    }.toByteArray()
                else
                    CryptoManager.passphrase = randomKey.toByteArray()
            }
        }
    }

    sealed interface BiometricResult {
        data object HardwareUnavailable : BiometricResult
        data class AuthenticationError(val error: String) : BiometricResult
        data object AuthenticationFailed : BiometricResult
        data object AuthenticationSuccess : BiometricResult
        data object AuthenticationNotSet : BiometricResult
    }
}