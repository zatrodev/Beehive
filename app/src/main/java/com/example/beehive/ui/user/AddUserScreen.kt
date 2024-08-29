package com.example.beehive.ui.user

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.EmailTextFieldSize
import com.example.beehive.ui.Dimensions.IndicatorLineThickness
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.RoundedCornerShape
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.BeehiveButton
import com.example.beehive.ui.common.BeehiveTextButton

@Composable
fun AddUserScreen(
    onBack: () -> Unit,
    viewModel: AddUserViewModel = viewModel(factory = BeehiveViewModelProvider.Factory)
) {
    val email by viewModel.email.collectAsStateWithLifecycle()

    Scaffold {
        AddUserContent(
            email = email,
            onBack = onBack,
            onEmailChange = viewModel::onEmailChange,
            onCreateUser = viewModel::onCreateUser,
            modifier = Modifier.padding(it)
        )
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserContent(
    email: String,
    onBack: () -> Unit,
    onEmailChange: (String) -> Unit,
    onCreateUser: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isError by remember { mutableStateOf(false) }

    Surface(modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.add_user_desc),
            )
            TextField(
                value = email,
                onValueChange = onEmailChange,
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                modifier = modifier
                    .width(EmailTextFieldSize)
                    .padding(SmallPadding)
                    .clip(RoundedCornerShape(RoundedCornerShape))
                    .indicatorLine(
                        enabled = true,
                        isError = isError,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                        ),
                        interactionSource = MutableInteractionSource(),
                        unfocusedIndicatorLineThickness = IndicatorLineThickness,
                    ),

                )
            if (isError)
                Text(
                    text = stringResource(R.string.email_error_message),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.padding(SmallPadding)
                )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(MediumPadding)
                .navigationBarsPadding()
        ) {
            BeehiveTextButton(text = stringResource(R.string.back_button), onClick = onBack)
            BeehiveButton(
                text = stringResource(R.string.add_user_button),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    if (email.isEmpty())
                        isError = true
                    else
                        onCreateUser(email)
                },
                modifier = Modifier.padding(SmallPadding)
            )
        }
    }
}