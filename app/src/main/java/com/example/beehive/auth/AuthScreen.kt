package com.example.beehive.auth

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.beehive.R
import com.example.beehive.auth.BiometricPromptManager.BiometricResult
import com.example.beehive.service.autofill.ReplyIntentManager
import com.example.beehive.ui.Dimensions.FingerprintIconSize
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.home.components.ConfirmationDialog
import kotlinx.coroutines.delay


@Composable
fun AuthScreen(
    navigateToHome: () -> Unit,
    navigateToChooseCredential: () -> Unit,
    replyIntentManager: ReplyIntentManager?,
    promptManager: BiometricPromptManager,
) {
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                println(it)
            })
    var showSetBiometricDialog by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.splash_bg),
                contentScale = ContentScale.FillBounds
            )
            .navigationBarsPadding()
            .padding(bottom = 50.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val biometricResult by promptManager.promptResults.collectAsStateWithLifecycle(
                initialValue = null
            )
            LaunchedEffect(biometricResult) {
                when (biometricResult) {
                    is BiometricResult.AuthenticationNotSet -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                            showSetBiometricDialog = true
                    }

                    is BiometricResult.AuthenticationSuccess -> {
                        replyIntentManager?.let { manager ->
                            manager.handleAutofill(navigateToChooseCredential)
                            return@LaunchedEffect
                        }

                        navigateToHome()
                    }

                    else -> Unit
                }
            }

            Spacer(modifier = Modifier.height(250.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(SmallPadding)
            )
            Text(
                text = stringResource(R.string.subtitle),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    promptManager.authenticateWithBiometric("Login with Biometrics")
                },
                modifier = Modifier
                    .size(FingerprintIconSize)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_fingerprint),
                    contentDescription = "fingerprint",
                    tint = Color.White,
                )
            }
        }

        if (replyIntentManager != null)
            LaunchedEffect(Unit) {
                delay(250)
                promptManager.authenticateWithBiometric("Authenticate Autofill")
            }

        if (showSetBiometricDialog)
            ConfirmationDialog(
                title = stringResource(R.string.biometric_not_set_title),
                message = stringResource(R.string.biometric_not_set_message),
                onConfirm = {
                    if (Build.VERSION.SDK_INT >= 30) {
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                            )
                        }
                        launcher.launch(enrollIntent)
                    }

                    showSetBiometricDialog = false
                },
                onCancel = { showSetBiometricDialog = false })
    }
}
