package com.example.beehive.ui.user

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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.EmailTextFieldSize
import com.example.beehive.ui.Dimensions.IndicatorLineThickness
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.RoundedCornerShape
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.BeehiveButton
import com.example.beehive.ui.common.BeehiveTextButton

@Composable
fun AddUserScreen(
    onBack: () -> Unit,
    viewModel: AddUserViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    Scaffold {
        AddUserContent(
            onBack = onBack,
            onCreateUser = { email ->
                viewModel.onCreateUser(email)
                onBack()
            },
            modifier = Modifier.padding(it)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserContent(
    onCreateUser: (String) -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    labelText: String = stringResource(R.string.add_user_desc),
) {
    val interactionSource = remember { MutableInteractionSource() }
    var email by remember {
        mutableStateOf("")
    }
    var isError by remember { mutableStateOf(false) }

    Surface(modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = labelText,
            )
            TextField(
                value = email,
                onValueChange = {
                    email = it
                },
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .width(EmailTextFieldSize)
                    .clip(RoundedCornerShape(RoundedCornerShape))
                    .padding(MediumPadding)
                    .indicatorLine(
                        enabled = true,
                        isError = isError,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                        ),
                        interactionSource = interactionSource,
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
                .padding(LargePadding)
                .navigationBarsPadding()
        ) {
            onBack?.let {
                BeehiveTextButton(
                    text = stringResource(R.string.back_button),
                    onClick = it
                )
            }
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