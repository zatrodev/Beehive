package com.example.beehive.ui.home.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.beehive.data.Password
import com.example.beehive.ui.Dimensions.IconSize
import com.example.beehive.ui.Dimensions.SmallPadding

//@Composable
//fun ScrollablePasswords(passwords: List<Password>, query: String, focusedOnSearch: Boolean) {
//    val listState = rememberLazyListState()
//    val firstVisibleItemIndex by remember {
//        derivedStateOf {
//            if (!listState.isScrollInProgress)
//                listState.firstVisibleItemIndex
//            else
//                null
//        }
//    }
//    val searchedPasswordIndex by remember(query) {
//        derivedStateOf { passwords.findFirstMatchingIndex(query) }
//    }
//    val relevantIndex = if (focusedOnSearch) searchedPasswordIndex else firstVisibleItemIndex
//
//    LazyRow(
//        state = listState,
//        contentPadding = PaddingValues(horizontal = 128.dp),
//        horizontalArrangement = Arrangement.spacedBy(28.dp),
//    ) {
//        items(passwords, key = {
//            it.id
//        }) { password ->
//            PasswordCard(
//                siteName = password.site,
//                url = password.url,
//                isHighlight = relevantIndex == passwords.indexOf(password)
//            )
//        }
//    }
//
//    LaunchedEffect(query) {
//        if (searchedPasswordIndex >= 0)
//            listState.animateScrollToItem(searchedPasswordIndex, 0)
//    }
//}

@Composable
fun PasswordsGrid(
    passwords: List<Password>
) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(SmallPadding)) {
        items(passwords) { password ->
            PasswordCard(
                site = password.site,
                password = password.password
            )
        }
    }
}