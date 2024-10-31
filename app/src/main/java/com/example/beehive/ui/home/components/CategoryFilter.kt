package com.example.beehive.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.beehive.R
import com.example.beehive.ui.Dimensions.SmallPadding
import com.example.beehive.ui.home.GroupingOption

@Composable
fun CategoryFilter(
    groupingOption: GroupingOption,
    onGroupingOptionChange: (GroupingOption) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SmallPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilterChip(
            selected = groupingOption == GroupingOption.ByApp,
            onClick = {
                onGroupingOptionChange(GroupingOption.ByApp)
            },
            label = { Text(stringResource(R.string.by_app)) },
            colors = FilterChipDefaults.filterChipColors().copy(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = groupingOption == GroupingOption.ByApp,
                selected = groupingOption == GroupingOption.ByApp,
                selectedBorderColor = MaterialTheme.colorScheme.primary,
                disabledBorderColor = Color.Transparent
            ),
        )
        FilterChip(
            selected = groupingOption == GroupingOption.ByUser,
            onClick = {
                onGroupingOptionChange(GroupingOption.ByUser)
            },
            label = { Text(stringResource(R.string.by_user)) },
            colors = FilterChipDefaults.filterChipColors().copy(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.primary,
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = groupingOption == GroupingOption.ByUser,
                selected = groupingOption == GroupingOption.ByUser,
                selectedBorderColor = MaterialTheme.colorScheme.primary,
                disabledBorderColor = Color.Transparent
            )
        )
    }
}