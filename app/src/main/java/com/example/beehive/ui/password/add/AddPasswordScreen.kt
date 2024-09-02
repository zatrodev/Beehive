package com.example.beehive.ui.password.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.BeehiveButton
import com.example.beehive.ui.common.BeehiveTextButton
import com.example.beehive.ui.common.BeehiveTextField
import com.example.beehive.ui.home.components.PasswordTile
import com.example.beehive.ui.password.components.LengthSlider
import com.example.beehive.ui.password.components.NameSearchDialog
import com.example.beehive.ui.password.components.OptionRow
import com.example.beehive.ui.password.components.PasswordDisplay
import com.example.beehive.utils.generatePassword


@Composable
fun AddPasswordScreen(
    onBack: () -> Unit,
    viewModel: AddPasswordViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    var showError by remember { mutableStateOf(false) }
    val uiState = viewModel.uiState

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        AddPasswordContent(
            uiState = uiState,
            isError = showError,
            onClearError = { showError = false },
            onNameChange = viewModel::updateName,
            onUsernameChange = viewModel::updateUsername,
            onPasswordChange = viewModel::updatePassword,
            onBack = onBack,
            onCreateClick = {
                if (uiState.name.isBlank())
                    showError = true
                else {
                    viewModel.onCreatePassword()
                    onBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .statusBarsPadding()
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddPasswordContent(
    uiState: AddPasswordUiState,
    isError: Boolean,
    onClearError: () -> Unit,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onBack: () -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableIntStateOf(1) }
    var checkboxStates by remember {
        mutableStateOf(
            mapOf(*OptionType.entries.map { it to true }.toTypedArray())
        )
    }

    fun onOptionChange(optionType: OptionType) {
        val tempStates = checkboxStates.toMutableMap()
        tempStates[optionType] = !tempStates[optionType]!!
        checkboxStates = tempStates

        onPasswordChange(generatePassword(sliderPosition, checkboxStates))
    }

    Column(modifier = modifier) {
        PasswordTile(
            name = uiState.name,
            icon = uiState.installedApps.find { it.packageName == uiState.packageName }?.icon,
            backgroundColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
            onClick = { showDialog = true },
            modifier = Modifier.padding(MediumPadding)
        )
        if (isError)
            Text(
                text = stringResource(R.string.name_error_message),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.padding(
                    start = MediumPadding
                )
            )
        BeehiveTextField(
            value = uiState.username,
            onValueChange = onUsernameChange,
            isError = isError,
            modifier = Modifier
                .width(280.dp)
                .padding(MediumPadding)
        )

        Column(
            modifier = Modifier.fillMaxHeight(0.75f),
            verticalArrangement = Arrangement.Center
        ) {
            PasswordDisplay(password = uiState.password)
            LengthSlider(
                length = sliderPosition,
                onLengthChange = {
                    sliderPosition = it.toInt()
                    onPasswordChange(generatePassword(sliderPosition, checkboxStates))
                })
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(LargePadding)) {
                    OptionRow(
                        checked = checkboxStates[OptionType.LowerCase]!!,
                        onCheckedChange = { onOptionChange(OptionType.LowerCase) },
                        text = stringResource(R.string.checkbox_lowercase_label),
                        isEnabled = checkboxStates.values.count { it } > 1 || !checkboxStates[OptionType.LowerCase]!!
                    )
                    OptionRow(
                        checked = (checkboxStates[OptionType.UpperCase]!!),
                        onCheckedChange = { onOptionChange(OptionType.UpperCase) },
                        text = stringResource(R.string.checkbox_uppercase_label),
                        isEnabled = checkboxStates.values.count { it } > 1 || !checkboxStates[OptionType.UpperCase]!!
                    )
                    OptionRow(
                        checked = (checkboxStates[OptionType.Punctuations]!!),
                        onCheckedChange = { onOptionChange(OptionType.Punctuations) },
                        text = stringResource(R.string.checkbox_punctuations_label),
                        isEnabled = checkboxStates.values.count { it } > 1 || !checkboxStates[OptionType.Punctuations]!!
                    )
                    OptionRow(
                        checked = (checkboxStates[OptionType.Numbers]!!),
                        onCheckedChange = { onOptionChange(OptionType.Numbers) },
                        text = stringResource(R.string.checkbox_numbers_label),
                        isEnabled = checkboxStates.values.count { it } > 1 || !checkboxStates[OptionType.Numbers]!!
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LargePadding),
            horizontalArrangement = Arrangement.End
        ) {
            BeehiveTextButton(text = stringResource(R.string.back_button), onClick = onBack)
            BeehiveButton(
                text = stringResource(R.string.create_password_button),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = onCreateClick,
                modifier = Modifier.padding(SmallPadding)
            )
        }
    }

    if (showDialog) {
        NameSearchDialog(
            name = uiState.name,
            openDialog = showDialog,
            onNameChange = onNameChange,
            appCardOnClick = { name ->
                onNameChange(name)
                showDialog = false
            },
            disableError = onClearError,
            closeDialogBox = { showDialog = false },
            installedApps = uiState.installedApps
        )
    }
}


enum class OptionType {
    LowerCase,
    UpperCase,
    Punctuations,
    Numbers
}