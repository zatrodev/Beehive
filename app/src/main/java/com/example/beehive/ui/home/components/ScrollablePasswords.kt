package com.example.beehive.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.example.beehive.data.Password
import com.example.beehive.utils.findFirstMatchingIndex

@Composable
fun ScrollablePasswords(passwords: List<Password>, query: String, focusedOnSearch: Boolean) {
    val listState = rememberLazyListState()
    val firstVisibleItemIndex by remember {
        derivedStateOf {
            if (!listState.isScrollInProgress)
                listState.firstVisibleItemIndex
            else
                null
        }
    }
    val searchedPasswordIndex by remember(query) {
        derivedStateOf { passwords.findFirstMatchingIndex(query) }
    }
    val relevantIndex = if (focusedOnSearch) searchedPasswordIndex else firstVisibleItemIndex

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 128.dp),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        items(passwords, key = {
            it.id
        }) { password ->
            PasswordCard(
                siteName = password.site,
                url = password.url,
                isHighlight = relevantIndex == passwords.indexOf(password)
            )
        }
    }

    LaunchedEffect(query) {
        if (searchedPasswordIndex >= 0)
            listState.animateScrollToItem(searchedPasswordIndex, 0)
    }
}

@Composable
fun PasswordsGrid(
    passwords: List<Password>
) {

}