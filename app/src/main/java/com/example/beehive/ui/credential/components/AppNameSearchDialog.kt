package com.example.beehive.ui.credential.components

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.beehive.data.app.AppInfo
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.BeehiveTextButton
import com.example.beehive.ui.theme.BeehiveTheme

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNameSearchDialog(
    name: String,
    openDialog: Boolean,
    onAppNameChange: (String) -> Unit,
    appCardOnClick: (String) -> Unit,
    disableError: () -> Unit,
    closeDialogBox: () -> Unit,
    installedApps: List<AppInfo>,
) {
    if (openDialog) {
        val interactionSource = remember { MutableInteractionSource() }
        BasicAlertDialog(onDismissRequest = closeDialogBox) {
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
                            onAppNameChange(it)
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
                    Column(
                        modifier = Modifier
                            .padding(
                                start = MediumPadding,
                                top = MediumPadding,
                                end = MediumPadding
                            )
                    ) {
                        if (installedApps.isEmpty()) {
                            Text(
                                buildAnnotatedString {
                                    append("Creating custom password for ")
                                    withStyle(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(name)
                                    }
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(
                                text = "Installed Apps",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            LazyColumn {
                                items(installedApps) { (_, name, icon) ->
                                    InstalledAppCard(
                                        appName = name,
                                        appIcon = icon,
                                        onClick = {
                                            appCardOnClick(name)
                                            disableError()
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


                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        BeehiveTextButton(
                            text = "Confirm",
                            onClick = closeDialogBox,
                            modifier = Modifier.padding(
                                SmallPadding
                            )
                        )
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
        AppNameSearchDialog(
            name = "",
            onAppNameChange = {},
            openDialog = true,
            appCardOnClick = { _ -> },
            closeDialogBox = {},
            disableError = {},
            installedApps = emptyList()
        )
    }

}