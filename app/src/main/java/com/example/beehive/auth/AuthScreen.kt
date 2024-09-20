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
import com.example.beehive.ui.Dimensions.SmallPadding


@Composable
fun AuthScreen(
    onNavigateToHome: () -> Unit,
    promptManager: BiometricPromptManager,
) {
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
            val launcher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = {
                        println(it)
                    })


            LaunchedEffect(biometricResult) {
                if (biometricResult is BiometricResult.AuthenticationNotSet) {
                    if (Build.VERSION.SDK_INT >= 30) {
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                            )
                        }

                        launcher.launch(enrollIntent)
                    }
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
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_fingerprint),
                    contentDescription = "fingerprint",
                    tint = Color.White,
                )
            }

            biometricResult?.takeIf { it is BiometricResult.AuthenticationSuccess }?.let {
                onNavigateToHome()
            }
        }

//            Surface(
//                color = Color.Transparent,
//                shape = MaterialTheme.shapes.small,
//                border = BorderStroke(2.dp, Color.Black),
//                modifier = Modifier
//                    .clickable {
//                        promptManager.authenticateWithBiometric("Login with Biometrics")
//                    }
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier.padding(MediumPadding)
//                ) {
//                    Text(
//                        text = stringResource(R.string.fingerprint_title),
//                        style = MaterialTheme.typography.bodySmall.copy(
//                            fontSize = 2.25.em
//                        ),
//                        lineHeight = 0.5.em
//                    )
//                }
//            }


    }
}