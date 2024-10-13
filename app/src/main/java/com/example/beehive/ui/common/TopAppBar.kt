package com.example.beehive.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextOverflow
import com.example.beehive.ui.Dimensions.TopBarElevation
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BeehiveTopBar(
    title: String,
    drawerState: DrawerState,
    actions: @Composable () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    Surface(
        shadowElevation = TopBarElevation,
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "menu")
                }
            },
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    overflow = TextOverflow.Ellipsis
                )
            },
            actions = {
                actions()
            },
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        )
    }
}