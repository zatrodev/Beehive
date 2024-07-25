package com.example.beehive.ui.password.components

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.beehive.domain.GetInstalledAppsUseCase.InstalledApp
import com.example.beehive.ui.Dimensions.LargePadding
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.BeehiveTextButton
import com.example.beehive.ui.theme.BeehiveTheme

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameSearchDialog(
    name: String,
    openDialog: Boolean,
    onNameChange: (String) -> Unit,
    appCardOnClick: (String, String) -> Unit,
    disableError: () -> Unit,
    closeDialogBox: () -> Unit,
    installedApps: List<InstalledApp>
) {
    if (openDialog) {
        val interactionSource = remember { MutableInteractionSource() }
        BasicAlertDialog(onDismissRequest = closeDialogBox, modifier = Modifier.height(300.dp)) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.small,
            ) {
                Column {
                    BasicTextField(
                        value = name,
                        onValueChange = {
                            onNameChange(it)
                            disableError()
                        },
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                        singleLine = true,
                        decorationBox = @Composable {
                            TextFieldDefaults.DecorationBox(
                                value = name,
                                visualTransformation = VisualTransformation.None,
                                innerTextField = it,
                                singleLine = true,
                                enabled = true,
                                interactionSource = interactionSource,
                                contentPadding = PaddingValues(0.dp),
                                colors = TextFieldDefaults.colors().copy(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                                ),
                                leadingIcon = @Composable {
                                    Icon(
                                        imageVector = Icons.Outlined.Create,
                                        contentDescription = "Search",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Column(modifier = Modifier.padding(MediumPadding)) {
                        Text(
                            text = "Installed Apps",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        LazyColumn {
                            items(installedApps) { (app, packageName, icon) ->
                                InstalledAppCard(
                                    appName = app,
                                    appIcon = icon,
                                    onClick = {
                                        appCardOnClick(app, packageName)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            SmallPadding
                                        )
                                )
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(LargePadding))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        BeehiveTextButton(text = "Confirm", onClick = closeDialogBox)
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewNameSearchDialog() {
    BeehiveTheme {
        NameSearchDialog(
            name = "",
            onNameChange = {},
            openDialog = true,
            appCardOnClick = { _, _ -> },
            closeDialogBox = {},
            disableError = {},
            installedApps = emptyList()
        )
    }

}