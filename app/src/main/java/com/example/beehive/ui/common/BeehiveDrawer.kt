package com.example.beehive.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Badge
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.beehive.ui.Dimensions.MediumPadding
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.DrawerItemsManager.NavigationItem
import kotlinx.coroutines.launch

@Composable
fun BeehiveDrawer(
    drawerState: DrawerState,
    items: List<NavigationItem>,
    selectedIndex: Int = 0,
    content: @Composable () -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(selectedIndex)
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                items.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = item.label,
                        selected = index == selectedItemIndex,
                        onClick = {
                            selectedItemIndex = index
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            item.navigate()
                        },
                        icon = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                        badge = {
                            if (item.badgeCount != null)
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                ) {
                                    Text(
                                        text = item.badgeCount.toString(),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                        },
                        shape = MaterialTheme.shapes.extraLarge.copy(
                            topStart = CornerSize(0.dp),
                            bottomStart = CornerSize(0.dp)
                        ),
                        modifier = Modifier.padding(
                            top = SmallPadding,
                            bottom = SmallPadding,
                            end = MediumPadding
                        )
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        content()
    }
}
