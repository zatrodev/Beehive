package com.example.beehive.ui.credential.edit

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.beehive.ui.common.ErrorScreen
import com.example.beehive.ui.common.LoadingScreen
import com.example.beehive.ui.common.PasswordCard
import com.example.beehive.ui.common.PasswordTile
import com.example.beehive.ui.credential.add.OptionType
import com.example.beehive.ui.credential.components.LengthSlider
import com.example.beehive.ui.credential.components.NameSearchDialog
import com.example.beehive.ui.credential.components.OptionRow
import com.example.beehive.ui.credential.components.PasswordDisplay
import com.example.beehive.ui.credential.components.UserDropdownMenu
import com.example.beehive.ui.navigation.SharedElementTransition
import com.example.beehive.utils.generatePassword


@Composable
fun EditCredentialScreen(
    onNavigateToAddUser: () -> Unit,
    onBack: () -> Unit,
    restartApp: () -> Unit,
    sharedElementTransition: SharedElementTransition,
    viewModel: EditCredentialViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is EditPasswordUiState.Loading -> LoadingScreen()
        is EditPasswordUiState.Error -> ErrorScreen(
            errorMessage = state.errorMessage,
            onRetry = restartApp
        )

        is EditPasswordUiState.Ready -> EditCredentialScreenReady(
            uiState = state,
            onNavigateToAddUser = onNavigateToAddUser,
            onBack = onBack,
            sharedElementTransition = sharedElementTransition,
            viewModel = viewModel,
        )
    }


}

@Composable
private fun EditCredentialScreenReady(
    uiState: EditPasswordUiState.Ready,
    onNavigateToAddUser: () -> Unit,
    onBack: () -> Unit,
    sharedElementTransition: SharedElementTransition,
    viewModel: EditCredentialViewModel,
) {
    var showError by remember { mutableStateOf(false) }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        EditCredentialContent(
            uiState = uiState,
            showError = showError,
            onNavigateToAddUser = onNavigateToAddUser,
            onAppNameChange = viewModel::updateAppName,
            onUserChange = viewModel::updateUser,
            clearError = { showError = false },
            onBack = onBack,
            onDoneEditingClick = {
                if (uiState.appName.isBlank())
                    showError = true
                else {
                    viewModel.updatePassword()
                    onBack()
                }
            },
            updateUsername = viewModel::updateUsername,
            updatePassword = viewModel::updatePassword,
            sharedElementTransition = sharedElementTransition,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun EditCredentialContent(
    uiState: EditPasswordUiState.Ready,
    showError: Boolean,
    clearError: () -> Unit,
    onNavigateToAddUser: () -> Unit,
    onAppNameChange: (String) -> Unit,
    onUserChange: (User) -> Unit,
    onBack: () -> Unit,
    onDoneEditingClick: () -> Unit,
    updateUsername: (String) -> Unit,
    updatePassword: (String) -> Unit,
    sharedElementTransition: SharedElementTransition,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    var showDialog by remember { mutableStateOf(false) }
    var showPassword by remember {
        mutableStateOf(false)
    }
    var sliderPosition by remember { mutableIntStateOf(1) }
    var checkboxStates by remember {
        mutableStateOf(
            mapOf(*OptionType.entries.map { it to true }.toTypedArray())
        )
    }
    var password by remember { mutableStateOf(uiState.password) }
    var username by remember { mutableStateOf(uiState.username) }

    fun toggleShowPassword() {
        showPassword = !showPassword
    }

    fun onOptionChange(optionType: OptionType) {
        val tempStates = checkboxStates.toMutableMap()
        tempStates[optionType] = !tempStates[optionType]!!
        checkboxStates = tempStates

        if (!showPassword)
            toggleShowPassword()

        password = generatePassword(sliderPosition, checkboxStates)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MediumPadding)
    ) {
        Spacer(modifier = Modifier.weight(1.5f))
        PasswordCard(
            title = uiState.user.email,
            subtitle = username,
            password = password,
            userId = uiState.user.id,
            showPassword = showPassword,
            sharedElementTransition = sharedElementTransition,
            modifier = Modifier
                .width(200.dp)
                .clickable(interactionSource = interactionSource, indication = null) {
                    toggleShowPassword()
                }
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LargePadding)
        ) {
            with(sharedElementTransition.sharedTransitionScope) {
                PasswordTile(
                    name = uiState.appName,
                    icon = uiState.icon,
                    backgroundColor = if (showError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = if (showError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .sharedElement(
                            sharedElementTransition.sharedTransitionScope.rememberSharedContentState(
                                key = uiState.packageName
                            ),
                            animatedVisibilityScope = sharedElementTransition.animatedContentScope,
                        )
                )
            }
            UserDropdownMenu(
                activeUser = uiState.user,
                users = uiState.users,
                onClick = { user ->
                    if (showPassword)
                        toggleShowPassword()

                    onUserChange(user)
                },
                onNavigateToAddUser = onNavigateToAddUser
            )
        }
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .padding(start = LargePadding, end = LargePadding)
        ) {
            BeehiveTextField(
                value = username,
                onValueChange = {
                    if (showPassword)
                        toggleShowPassword()

                    username = it
                    clearError()
                },
                labelColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MediumPadding)
            )
        }
        Surface(
            modifier = Modifier
                .padding(start = LargePadding, end = LargePadding),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column {
                Text(
                    text = stringResource(R.string.add_password_label),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(MediumPadding)
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    PasswordDisplay(
                        password = password,
                        onPasswordChange = {
                            password = it
                        }
                    )
                    LengthSlider(
                        length = sliderPosition,
                        onLengthChange = {
                            if (!showPassword)
                                toggleShowPassword()

                            sliderPosition = it.toInt()
                            password = generatePassword(sliderPosition, checkboxStates)
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
                text = stringResource(R.string.done_editing_button),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    updateUsername(username)
                    updatePassword(password)
                    onDoneEditingClick()
                },
                modifier = Modifier.padding(SmallPadding)
            )
        }
    }

    if (showDialog) {
        NameSearchDialog(
            name = uiState.appName,
            openDialog = showDialog,
            onNameChange = onAppNameChange,
            appCardOnClick = { name ->
                onAppNameChange(name)
                showDialog = false
            },
            disableError = clearError,
            closeDialogBox = { showDialog = false },
            installedApps = uiState.mutableInstalledApps
        )
    }
}