package com.lagradost.cloudstream4.compose

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class NuvioNavigationItem(
    val label: String,
    val selected: Boolean,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit,
)

@Composable
fun NuvioBottomBar(items: List<NuvioNavigationItem>, modifier: Modifier = Modifier) {
    NavigationBar(modifier = modifier, containerColor = NuvioColors.Surface, contentColor = NuvioColors.TextSecondary) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.selected,
                onClick = item.onClick,
                icon = item.icon,
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NuvioColors.Gold,
                    selectedTextColor = NuvioColors.Gold,
                    indicatorColor = NuvioColors.GoldFocus,
                    unselectedIconColor = NuvioColors.TextTertiary,
                    unselectedTextColor = NuvioColors.TextTertiary,
                ),
            )
        }
    }
}
