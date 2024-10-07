package com.example.beehive.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.beehive.ui.navigation.DeletedCredentials
import com.example.beehive.ui.navigation.Home


object DrawerItemsManager {
    data class NavigationItem(
        val label: @Composable () -> Unit,
        val selectedIcon: @Composable () -> Unit,
        val unselectedIcon: @Composable () -> Unit,
        val navigate: () -> Unit,
        var badgeCount: Int? = null,
    )

    private val items = mutableListOf<NavigationItem>()

    val allItems: List<NavigationItem>
        get() = items

    fun setDrawerItems(navController: NavController) {
        items.addAll(
            listOf(
                NavigationItem(
                    label = { Text(text = "Home") },
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
                    label = { Text(text = "Super Duper Secrets") },
                    selectedIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "super duper secrets"
                        )
                    },
                    unselectedIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "super duper secrets"
                        )
                    },
                    navigate = {
                        // TODO
                    }
                ),
                NavigationItem(
                    label = { Text(text = "Deleted") },
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
                    label = { Text(text = "Settings") },
                    selectedIcon = {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = "settings")
                    },
                    unselectedIcon = {
                        Icon(imageVector = Icons.Outlined.Settings, contentDescription = "settings")
                    },
                    navigate = {
                        // TODO
                    }
                )
            )
        )
    }

    fun setBadgeCount(index: Int, count: Int) {
        items[index].badgeCount = count
    }
}