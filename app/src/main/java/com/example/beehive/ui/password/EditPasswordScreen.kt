package com.example.beehive.ui.password

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.common.PasswordButton
import com.example.beehive.ui.common.PasswordTextButton
import com.example.beehive.ui.home.components.PasswordCard
import com.example.beehive.ui.password.components.FeatureSiteTextField
import com.example.beehive.ui.password.components.LengthSlider
import com.example.beehive.ui.password.components.OptionRow
import com.example.beehive.ui.theme.BeehiveTheme
import kotlinx.coroutines.launch


// TODO: implement shared layout transition

@Composable
fun EditPasswordScreen(
    navigateBack: () -> Unit,
    viewModel: EditPasswordViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        EditPasswordContent(
            passwordUiState = viewModel.passwordUiState,
            updateUiState = viewModel::updateUiState,
            onBack = navigateBack,
            onDoneEditingClick = {
                coroutineScope.launch {
                    viewModel.updatePassword()
                    navigateBack()
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditPasswordContent(
    passwordUiState: PasswordUiState,
    updateUiState: (String, String) -> Unit,
    onBack: () -> Unit,
    onDoneEditingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
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
            passwordUiState.site,
            generatePassword(sliderPosition, checkboxStates)
        )
    }


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(0.3f))
        PasswordCard(
            site = passwordUiState.site,
            password = passwordUiState.password,
            showPassword = showPassword,
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
            FeatureSiteTextField(
                site = passwordUiState.site,
                onSiteChange = {
                    if (showPassword)
                        toggleShowPassword()

                    updateUiState(it, passwordUiState.password)
                },
            )
            LengthSlider(
                length = sliderPosition,
                onLengthChange = {
                    if (!showPassword)
                        toggleShowPassword()

                    sliderPosition = it.toInt()
                    updateUiState(
                        passwordUiState.site,
                        generatePassword(sliderPosition, checkboxStates)
                    )
                })
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(MediumPadding)) {
                    OptionRow(
                        checked = checkboxStates[OptionType.LowerCase]!!,
                        onCheckedChange = { onOptionChange(OptionType.LowerCase) },
                        text = "Lower Case",
                        isEnabled = checkboxStates.values.count { it } > 1 || !checkboxStates[OptionType.LowerCase]!!
                    )
                    OptionRow(
                        checked = (checkboxStates[OptionType.UpperCase]!!),
                        onCheckedChange = { onOptionChange(OptionType.UpperCase) },
                        text = "Upper Case",
                        isEnabled = checkboxStates.values.count { it } > 1 || !checkboxStates[OptionType.UpperCase]!!
                    )
                    OptionRow(
                        checked = (checkboxStates[OptionType.Punctuations]!!),
                        onCheckedChange = { onOptionChange(OptionType.Punctuations) },
                        text = "Punctuations",
                        isEnabled = checkboxStates.values.count { it } > 1 || !checkboxStates[OptionType.Punctuations]!!
                    )
                    OptionRow(
                        checked = (checkboxStates[OptionType.Numbers]!!),
                        onCheckedChange = { onOptionChange(OptionType.Numbers) },
                        text = "Numbers",
                        isEnabled = checkboxStates.values.count { it } > 1 || !checkboxStates[OptionType.Numbers]!!
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MediumPadding),
            horizontalArrangement = Arrangement.End
        ) {
            PasswordTextButton(text = "Back", onClick = onBack)
            PasswordButton(
                text = "Done",
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    onDoneEditingClick()
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditPasswordScreenPreview() {
    BeehiveTheme {
        EditPasswordContent(
            passwordUiState = PasswordUiState(
                site = "Facebook",
                password = "ahbwhdawhdwd"
            ),
            updateUiState = { _, _ -> },
            onBack = { },
            onDoneEditingClick = { },
        )
    }
}