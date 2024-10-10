package com.example.beehive.ui.credential.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.data.user.User
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.BeehiveButton
import com.example.beehive.ui.common.BeehiveTextButton
import com.example.beehive.ui.common.BeehiveTextField
import com.example.beehive.ui.common.PasswordTile
import com.example.beehive.ui.credential.components.ErrorText
import com.example.beehive.ui.credential.components.LengthSlider
import com.example.beehive.ui.credential.components.NameSearchDialog
import com.example.beehive.ui.credential.components.OptionRow
import com.example.beehive.ui.credential.components.PasswordDisplay
import com.example.beehive.ui.credential.components.UserDropdownMenu
import com.example.beehive.utils.generatePassword


@Composable
fun AddCredentialScreen(
    onNavigateToAddUser: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddCredentialViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    var showError by remember { mutableStateOf(false) }
    val uiState = viewModel.uiState

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        AddCredentialContent(
            uiState = uiState,
            showError = showError,
            onClearError = { showError = false },
            onNavigateToAddUser = onNavigateToAddUser,
            onNameChange = viewModel::updateName,
            onUsernameChange = viewModel::updateUsername,
            onPasswordChange = viewModel::updatePassword,
            onUserChange = viewModel::updateUser,
            onBack = onBack,
            onCreateClick = {
                if (uiState.name.isBlank() || uiState.user == null)
                    showError = true
                else {
                    viewModel.createCredential()
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
private fun AddCredentialContent(
    uiState: AddPasswordUiState,
    showError: Boolean,
    onClearError: () -> Unit,
    onNavigateToAddUser: () -> Unit,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onUserChange: (User) -> Unit,
    onBack: () -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier,
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

    Column(verticalArrangement = Arrangement.spacedBy(MediumPadding), modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(MediumPadding)
        ) {
            Text(
                text = stringResource(R.string.add_password_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SmallPadding)
            ) {
                UserDropdownMenu(
                    activeUser = uiState.user,
                    users = uiState.users,
                    onClick = onUserChange,
                    onNavigateToAddUser = onNavigateToAddUser,
                    isError = showError && uiState.user == null,
                )
                if (showError && uiState.user == null)
                    ErrorText(text = stringResource(R.string.user_error_message))
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        PasswordTile(
            name = uiState.name,
            icon = uiState.icon,
            backgroundColor = if (showError && uiState.name.isBlank()) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = if (showError && uiState.name.isBlank()) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer,
            onClick = { showDialog = true },
            modifier = Modifier.padding(horizontal = LargePadding)
        )
        if (showError && uiState.name.isBlank())
            ErrorText(text = stringResource(R.string.name_error_message))

        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .padding(horizontal = LargePadding)
        ) {
            BeehiveTextField(
                value = uiState.username,
                onValueChange = onUsernameChange,
                labelColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MediumPadding)
            )
        }
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .padding(horizontal = LargePadding)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.add_password_label),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = LargePadding, top = LargePadding)
                )
                PasswordDisplay(password = uiState.password, onPasswordChange = onPasswordChange)
                LengthSlider(
                    length = sliderPosition,
                    onLengthChange = {
                        sliderPosition = it.toInt()
                        onPasswordChange(generatePassword(sliderPosition, checkboxStates))
                    })
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(LargePadding),
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
            installedApps = uiState.mutableInstalledApps
        )
    }
}


enum class OptionType {
    LowerCase,
    UpperCase,
    Punctuations,
    Numbers
}