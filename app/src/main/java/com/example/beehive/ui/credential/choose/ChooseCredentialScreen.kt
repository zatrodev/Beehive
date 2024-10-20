package com.example.beehive.ui.credential.choose

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.data.user.User
import com.example.beehive.service.autofill.ReplyIntentManager
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.BottomSheetIconSize
import com.example.beehive.ui.Dimensions.CheckIconSize
import com.example.beehive.ui.Dimensions.ExtraSmallPadding
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.PasswordCardHeight
import com.example.beehive.ui.Dimensions.RoundedCornerShape
import com.example.beehive.ui.Dimensions.ShadowElevation
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.common.ConditionalStyleText
import com.example.beehive.ui.common.LoadingScreen
import com.example.beehive.ui.common.PasswordTile
import com.example.beehive.utils.isDarkMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChooseCredentialScreen(
    replyIntentManager: ReplyIntentManager,
    viewModel: ChooseCredentialViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    var chosenCredential by remember {
        mutableStateOf<CredentialAndUser?>(null)
    }
    val credentials by viewModel.credentials.collectAsStateWithLifecycle()

    if (credentials.isEmpty()) {
        LoadingScreen()
    } else {
        Scaffold { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        chosenCredential = null
                    }
            ) {
                chosenCredential?.let {
                    BottomSheet(
                        onConfirm = {
                            coroutineScope.launch {
                                replyIntentManager.setReply(chosenCredential!!.credential.id)
                                replyIntentManager.sendReply()
                            }
                        },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .navigationBarsPadding()
                ) {
                    Text(
                        text = stringResource(R.string.choose_password_title),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(MediumPadding)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = MediumPadding)
                    ) {
                        if (credentials.isEmpty()) {
                            Text(
                                text = stringResource(R.string.no_bees_found),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            return@Column
                        }

                        LazyColumn {
                            items(credentials.groupBy {
                                it.credential.app
                            }.toList()) { credentialPair ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = MaterialTheme.shapes.extraLarge,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = SmallPadding)
                                ) {
                                    Column {
                                        PasswordTile(
                                            name = credentialPair.first.name,
                                            icon = credentialPair.first.icon,
                                            backgroundColor = Color.Transparent,
                                            modifier = Modifier.padding(
                                                start = SmallPadding,
                                                end = SmallPadding,
                                                top = SmallPadding,
                                            ),
                                        )
                                        FlowRow(
                                            maxItemsInEachRow = 2,
                                            modifier = Modifier.padding(
                                                start = SmallPadding,
                                                end = SmallPadding,
                                                bottom = SmallPadding,
                                            )
                                        ) {
                                            credentialPair.second.map {
                                                PasswordCard(
                                                    username = it.credential.username,
                                                    user = it.user,
                                                    selected = it.credential.id == chosenCredential?.credential?.id,
                                                    onClick = {
                                                        chosenCredential = it
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun BottomSheet(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp)
        ),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier
            .safeDrawingPadding()
            .fillMaxHeight(0.1f)
            .fillMaxWidth()

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable {
                        onConfirm()
                    }
                    .padding(MediumPadding)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_confirm),
                    contentDescription = "confirm",
                    modifier = Modifier.size(BottomSheetIconSize)
                )
                Text(
                    text = stringResource(R.string.confirm),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun PasswordCard(
    username: String,
    user: User,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val cardWidth: Dp =
        ((LocalConfiguration.current.screenWidthDp.dp - MediumPadding - SmallPadding * 4) / 2)

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) {
                onClick()
            }
            .width(cardWidth)
            .height(PasswordCardHeight)
            .padding(SmallPadding)
            .shadow(
                elevation = if (isDarkMode(LocalContext.current)) 0.dp else ShadowElevation,
                shape = RoundedCornerShape(RoundedCornerShape)
            )

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(MediumPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = user.email,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            ConditionalStyleText(
                text = username,
                fontSize = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ExtraSmallPadding)
        ) {

            Badge(
                containerColor = if (selected) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.surfaceContainerHighest,
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.inverseOnSurface,
                        modifier = Modifier.size(CheckIconSize)
                    )
                }
            }
        }
    }
}