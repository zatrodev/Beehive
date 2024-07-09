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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.common.PasswordButton
import com.example.beehive.ui.common.PasswordTextButton
import com.example.beehive.ui.password.components.LengthSlider
import com.example.beehive.ui.password.components.OptionRow
import com.example.beehive.ui.password.components.PasswordDisplay
import com.example.beehive.ui.password.components.SiteTextField
import kotlinx.coroutines.launch


@Composable
fun AddPasswordScreen(
    navigateBack: () -> Unit,
    viewModel: AddPasswordViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        AddPasswordContent(
            updateUiState = viewModel::updateUiState,
            onBack = navigateBack,
            onCreateClick = {
                coroutineScope.launch {
                    viewModel.createPassword()
                    navigateBack()
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddPasswordContent(
    updateUiState: (String, String) -> Unit,
    onBack: () -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var site by remember { mutableStateOf("") }
    var sliderPosition by remember { mutableIntStateOf(1) }
    var checkboxStates by remember {
        mutableStateOf(
            mapOf(*OptionType.entries.map { it to true }.toTypedArray())
        )
    }
    val password by remember(sliderPosition, checkboxStates) {
        derivedStateOf {
            generatePassword(sliderPosition, checkboxStates)
        }
    }

    fun onOptionChange(optionType: OptionType) {
        val tempStates = checkboxStates.toMutableMap()
        tempStates[optionType] = !tempStates[optionType]!!
        checkboxStates = tempStates
    }

    Column(modifier = modifier) {
        SiteTextField(
            site = site,
            onSiteChange = { site = it },
        )
        Column(
            modifier = Modifier.fillMaxHeight(0.8f),
            verticalArrangement = Arrangement.Center
        ) {
            PasswordDisplay(password = password)
            LengthSlider(
                length = sliderPosition,
                onLengthChange = {
                    sliderPosition = it.toInt()
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
                text = "Create Password",
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    updateUiState(site, password)
                    onCreateClick()
                })
        }
    }
}

private fun generatePassword(length: Int, options: Map<OptionType, Boolean>): String {
    val usableChars = mutableListOf<Char>()

    if (options[OptionType.LowerCase] == true) usableChars.addAll('a'..'z')
    if (options[OptionType.UpperCase] == true) usableChars.addAll('A'..'Z')
    if (options[OptionType.Punctuations] == true) usableChars.addAll(
        listOf(
            '!',
            '@',
            '#',
            '$',
            '%',
            '^',
            '&',
            '*',
            '(',
            ')',
            '_',
            '-'
        )
    )
    if (options[OptionType.Numbers] == true) usableChars.addAll('0'..'9')

    return (1..length)
        .map { usableChars.random() }
        .joinToString("")
}

private enum class OptionType {
    LowerCase,
    UpperCase,
    Punctuations,
    Numbers
}