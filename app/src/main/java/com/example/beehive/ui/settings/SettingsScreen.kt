package com.example.beehive.ui.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beehive.R
import com.example.beehive.ui.BeehiveViewModelProvider
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.TopBarElevation
import com.example.beehive.ui.common.LoadingScreen
import kotlin.math.absoluteValue

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = BeehiveViewModelProvider.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is SettingsUiState.Loading -> LoadingScreen()
        is SettingsUiState.Ready -> SettingsScreenReady(
            retentionPeriod = state.retentionPeriod,
            onUpdateRetentionPeriod = viewModel::updateRetentionPeriod,
            onBack = onBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenReady(
    retentionPeriod: Int,
    onUpdateRetentionPeriod: (Int) -> Unit,
    onBack: () -> Unit,
) {
    var showInputSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(
                shadowElevation = TopBarElevation
            ) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            onBack()
                        }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "close")
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.settings_title),
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    },
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            SettingsItem(
                title = stringResource(R.string.retention_title),
                description = stringResource(R.string.retention_desc),
                modifier = Modifier.clickable {
                    showInputSheet = true
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = retentionPeriod.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "arrow right",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = MediumPadding)

            )
        }

        if (showInputSheet)
            InputSheet(
                retentionPeriod = retentionPeriod,
                onSave = onUpdateRetentionPeriod,
                onDismiss = { showInputSheet = false }
            )
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(MediumPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )

            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputSheet(
    retentionPeriod: Int,
    onSave: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val maxDays = 30

    val pageCount = 1000 * maxDays
    val pagerState = rememberPagerState(
        initialPage = retentionPeriod - 1,
        pageCount = {
            pageCount
        }
    )

    val threePagesPerViewport = object : PageSize {
        override fun Density.calculateMainAxisPageSize(availableSpace: Int, pageSpacing: Int): Int {
            return (availableSpace - 2 * pageSpacing) / 3
        }
    }
    val fling = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(10)
    )

    ModalBottomSheet(onDismissRequest = onDismiss, modifier = Modifier.fillMaxHeight(0.4f)) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row {
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "cancel")
                }
                Text(
                    text = "${pagerState.currentPage % maxDays + 1} day/s",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(MediumPadding)
                )
                IconButton(onClick = {
                    onSave(pagerState.currentPage % maxDays + 1)
                    onDismiss()
                }) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "confirm")
                }
            }

            VerticalPager(
                state = pagerState,
                pageSize = threePagesPerViewport,
                snapPosition = SnapPosition.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                flingBehavior = fling
            ) { absolutePageIndex ->
                val pageOffset =
                    pagerState.getOffsetDistanceInPages(absolutePageIndex).absoluteValue
                Text(
                    text = ((absolutePageIndex % maxDays) + 1).toString(),
                    fontSize = 35.sp * (lerp(1f, 0.75f, pageOffset)),
                    color = if (pagerState.currentPage == absolutePageIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}