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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.data.user.User
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.AppTile
import com.example.beehive.ui.common.BeehiveButton
import com.example.beehive.ui.common.BeehiveTextButton
import com.example.beehive.ui.common.BeehiveTextField
import com.example.beehive.ui.credential.components.AppNameSearchDialog
import com.example.beehive.ui.credential.components.ErrorText
import com.example.beehive.ui.credential.components.LengthSlider
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        AddCredentialContent(
            uiState = uiState,
            showError = showError,
            clearError = { showError = false },
            onNavigateToAddUser = onNavigateToAddUser,
            updateAppName = viewModel::updateAppName,
            updateUsername = viewModel::updateUsername,
            updatePassword = viewModel::updatePassword,
            onUserChange = viewModel::updateUser,
            onBack = onBack,
            onCreateClick = {
                if (uiState.appName.isBlank() || uiState.user == null)
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
    clearError: () -> Unit,
    onNavigateToAddUser: () -> Unit,
    updateAppName: (String) -> Unit,
    updateUsername: (String) -> Unit,
    updatePassword: (String) -> Unit,
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
    var password by remember { mutableStateOf(uiState.password) }
    var username by remember { mutableStateOf(uiState.username) }
    var appName by remember { mutableStateOf(uiState.appName) }

    fun onOptionChange(optionType: OptionType) {
        val tempStates = checkboxStates.toMutableMap()
        tempStates[optionType] = !tempStates[optionType]!!
        checkboxStates = tempStates

        password = generatePassword(sliderPosition, checkboxStates)
        updatePassword(password)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(MediumPadding),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(MediumPadding)
        ) {
            Text(
                text = stringResource(R.string.add_password_title),
                style = MaterialTheme.typography.headlineSmall,
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
        AppTile(
            name = uiState.appName,
            icon = uiState.icon,
            backgroundColor = if (showError && uiState.appName.isBlank()) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = if (showError && uiState.appName.isBlank()) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer,
            onClick = { showDialog = true },
            modifier = Modifier.padding(horizontal = LargePadding)
        )
        if (showError && uiState.appName.isBlank())
            ErrorText(text = stringResource(R.string.name_error_message))

        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .padding(horizontal = LargePadding)
        ) {
            BeehiveTextField(
                value = username,
                onValueChange = {
                    username = it
                    updateUsername(username)
                    clearError()
                },
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
                PasswordDisplay(password = password, onPasswordChange = {
                    password = it
                    updatePassword(password)
                })
                LengthSlider(
                    length = sliderPosition,
                    onLengthChange = {
                        sliderPosition = it.toInt()
                        password = generatePassword(sliderPosition, checkboxStates)
                        updatePassword(password)
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
        AppNameSearchDialog(
            name = appName,
            openDialog = showDialog,
            onAppNameChange = {
                appName = it
                updateAppName(it)
            },
            appCardOnClick = {
                appName = it
                updateAppName(it)
                showDialog = false
            },
            disableError = clearError,
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