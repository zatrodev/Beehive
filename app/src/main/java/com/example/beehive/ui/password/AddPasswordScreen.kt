package com.example.beehive.ui.password

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.domain.GetInstalledAppsUseCase.InstalledApp
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.BeehiveButton
import com.example.beehive.ui.common.BeehiveTextButton
import com.example.beehive.ui.password.components.LengthSlider
import com.example.beehive.ui.password.components.NameSearchDialog
import com.example.beehive.ui.password.components.NameTextField
import com.example.beehive.ui.password.components.OptionRow
import com.example.beehive.ui.password.components.PasswordDisplay
import com.example.beehive.utils.generatePassword
import kotlinx.coroutines.launch


@Composable
fun AddPasswordScreen(
    navigateBack: () -> Unit,
    viewModel: AddPasswordViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    var showError by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        AddPasswordContent(
            installedApps = viewModel.uiState.installedApps,
            isError = showError,
            onClearError = { showError = false },
            uiState = viewModel.uiState,
            updateUiState = viewModel::updateUiState,
            onBack = navigateBack,
            onCreateClick = {
                coroutineScope.launch {
                    val isCreated = viewModel.createPassword()
                    if (isCreated)
                        navigateBack()
                    else
                        showError = true

                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddPasswordContent(
    installedApps: List<InstalledApp>,
    isError: Boolean,
    onClearError: () -> Unit,
    uiState: AddPasswordUiState,
    updateUiState: (String, String, String) -> Unit,
    onBack: () -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var openDialog by remember { mutableStateOf(false) }
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

        updateUiState(
            uiState.name,
            uiState.packageName,
            generatePassword(sliderPosition, checkboxStates)
        )
    }

    Column(modifier = modifier) {
        NameTextField(
            name = uiState.name,
            onNameChange = {
                updateUiState(it, uiState.packageName, uiState.password)
            },
            isError = isError,
            showSearchDialog = { openDialog = true }
        )
        Column(
            modifier = Modifier.fillMaxHeight(0.8f),
            verticalArrangement = Arrangement.Center
        ) {
            PasswordDisplay(password = uiState.password)
            LengthSlider(
                length = sliderPosition,
                onLengthChange = {
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
                text = stringResource(R.string.create_password_button),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = onCreateClick,
                modifier = Modifier.padding(SmallPadding)
            )
        }
    }

    if (openDialog) {
        NameSearchDialog(
            name = uiState.name,
            openDialog = openDialog,
            onNameChange = {
                updateUiState(it, uiState.packageName, uiState.password)
            },
            appCardOnClick = { name, packageName ->
                updateUiState(name, packageName, uiState.password)
                openDialog = false
            },
            disableError = onClearError,
            closeDialogBox = { openDialog = false },
            installedApps = installedApps
        )
    }
}


enum class OptionType {
    LowerCase,
    UpperCase,
    Punctuations,
    Numbers
}