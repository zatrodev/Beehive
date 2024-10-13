package com.example.beehive.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.beehive.R
import com.example.beehive.ui.navigation.DeletedCredentials
import com.example.beehive.ui.navigation.Home
import com.example.beehive.ui.navigation.Settings


object DrawerItemsManager {
    data class NavigationItem(
        val label: @Composable () -> Unit,
        val selectedIcon: @Composable () -> Unit,
        val unselectedIcon: @Composable () -> Unit,
        val navigate: () -> Unit,
        var badgeCount: Int? = null,
    )

    const val DELETED_INDEX = 1
    private val items = mutableListOf<NavigationItem>()

    val allItems: List<NavigationItem>
        get() = items

    fun setDrawerItems(navController: NavController) {
        if (items.isNotEmpty()) {
            items.clear()
        }

        items.addAll(
            listOf(
                NavigationItem(
                    label = {
                        Text(
                            text = stringResource(R.string.home_title),
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "home"
                        )
                    },
                    unselectedIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = "home"
                        )
                    },
                    navigate = {
                        navController.navigate(
                            route = Home
                        )
                    }
                ),
                NavigationItem(
                    label = {
                        Text(
                            text = stringResource(R.string.deleted_title),
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    selectedIcon = {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "deleted")
                    },
                    unselectedIcon = {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "deleted")
                    },
                    navigate = {
                        navController.navigate(
                            route = DeletedCredentials
                        )
                    }
                ),
                NavigationItem(
                    label = {
                        Text(
                            text = stringResource(R.string.settings_title),
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    selectedIcon = {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = "settings")
                    },
                    unselectedIcon = {
                        Icon(imageVector = Icons.Outlined.Settings, contentDescription = "settings")
                    },
                    navigate = {
                        navController.navigate(
                            route = Settings
                        )
                    }
                )
            )
        )
    }

    fun setBadgeCount(index: Int, count: Int) {
        items[index].badgeCount = count
    }
}