package com.example.beehive.ui.password.edit

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.domain.GetInstalledAppsUseCase.InstalledApp
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.BeehiveButton
import com.example.beehive.ui.common.BeehiveTextButton
import com.example.beehive.ui.home.components.PasswordCard
import com.example.beehive.ui.navigation.SharedElementTransition
import com.example.beehive.ui.password.add.AddPasswordUiState
import com.example.beehive.ui.password.add.OptionType
import com.example.beehive.ui.password.components.FeatureNameTextField
import com.example.beehive.ui.password.components.LengthSlider
import com.example.beehive.ui.password.components.NameSearchDialog
import com.example.beehive.ui.password.components.OptionRow
import com.example.beehive.utils.generatePassword
import kotlinx.coroutines.launch


@Composable
fun EditPasswordScreen(
    onBack: () -> Unit,
    sharedElementTransition: SharedElementTransition,
    viewModel: EditPasswordViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    var isError by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        EditPasswordContent(
            installedApps = viewModel.uiState.installedApps,
            isError = isError,
            onClearError = { isError = false },
            uiState = viewModel.uiState,
            updateUiState = viewModel::updateUiState,
            onBack = onBack,
            onDoneEditingClick = {
                coroutineScope.launch {
                    if (viewModel.updatePassword())
                        onBack()
                    else
                        isError = true
                }
            },
            sharedElementTransition = sharedElementTransition,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditPasswordContent(
    installedApps: List<InstalledApp>,
    isError: Boolean,
    onClearError: () -> Unit,
    uiState: AddPasswordUiState,
    updateUiState: (String, String, String) -> Unit,
    onBack: () -> Unit,
    onDoneEditingClick: () -> Unit,
    sharedElementTransition: SharedElementTransition,
    modifier: Modifier = Modifier
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

    fun toggleShowPassword() {
        showPassword = !showPassword
    }

    fun onOptionChange(optionType: OptionType) {
        val tempStates = checkboxStates.toMutableMap()
        tempStates[optionType] = !tempStates[optionType]!!
        checkboxStates = tempStates

        if (!showPassword)
            toggleShowPassword()

        updateUiState(
            uiState.name,
            uiState.packageName,
            generatePassword(sliderPosition, checkboxStates)
        )
    }


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(0.3f))
        PasswordCard(
            username = uiState.name,
            password = uiState.password,
            showPassword = showPassword,
            sharedElementTransition = sharedElementTransition,
            modifier = Modifier
                .width(200.dp)
                .clickable(interactionSource = interactionSource, indication = null) {
                    toggleShowPassword()
                }
        )
        Spacer(modifier = Modifier.weight(0.3f))
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FeatureNameTextField(
                name = uiState.name,
                packageName = uiState.packageName,
                isError = isError,
                onNameChange = {
                    if (showPassword)
                        toggleShowPassword()

                    updateUiState(it, uiState.packageName, uiState.password)
                    onClearError()
                },
                modifier = Modifier.clickable(
                    enabled = true,
                    onClick = { showDialog = true }
                )
            )
            LengthSlider(
                length = sliderPosition,
                onLengthChange = {
                    if (!showPassword)
                        toggleShowPassword()

                    sliderPosition = it.toInt()
                    updateUiState(
                        uiState.name,
                        uiState.packageName,
                        generatePassword(sliderPosition, checkboxStates)
                    )
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
                text = stringResource(R.string.done_editing_button),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    onDoneEditingClick()
                }, modifier = Modifier.padding(SmallPadding)
            )
        }
    }

    if (showDialog) {
        NameSearchDialog(
            name = uiState.name,
            openDialog = showDialog,
            onNameChange = {
                updateUiState(it, uiState.packageName, uiState.password)
            },
            appCardOnClick = { name, packageName ->
                updateUiState(name, packageName, uiState.password)
                showDialog = false
            },
            disableError = onClearError,
            closeDialogBox = { showDialog = false },
            installedApps = installedApps
        )
    }
}