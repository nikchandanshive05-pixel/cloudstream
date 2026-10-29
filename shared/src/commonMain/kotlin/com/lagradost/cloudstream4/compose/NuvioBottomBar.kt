package com.lagradost.cloudstream4.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class NuvioNavigationItem(
    val id: Int,
    val label: String,
    val selected: Boolean,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

/**
 * Encapsulated high-level bottom bar that maps directly to the standard 5 main CloudStream destinations
 * without requiring the consumer module (:app) to declare direct dependencies on compose icons.
 */
@Composable
fun NuvioMainBottomBar(
    selectedId: Int,
    homeId: Int,
    searchId: Int,
    libraryId: Int,
    downloadsId: Int,
    settingsId: Int,
    homeLabel: String = "Home",
    searchLabel: String = "Search",
    libraryLabel: String = "Library",
    downloadsLabel: String = "Downloads",
    settingsLabel: String = "Settings",
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = remember(selectedId, homeId, searchId, libraryId, downloadsId, settingsId, homeLabel, searchLabel, libraryLabel, downloadsLabel, settingsLabel) {
        listOf(
            NuvioNavigationItem(
                id = homeId,
                label = homeLabel,
                selected = selectedId == homeId,
                icon = Icons.Default.Home,
                onClick = { onItemSelected(homeId) }
            ),
            NuvioNavigationItem(
                id = searchId,
                label = searchLabel,
                selected = selectedId == searchId,
                icon = Icons.Default.Search,
                onClick = { onItemSelected(searchId) }
            ),
            NuvioNavigationItem(
                id = libraryId,
                label = libraryLabel,
                selected = selectedId == libraryId,
                icon = Icons.Default.VideoLibrary,
                onClick = { onItemSelected(libraryId) }
            ),
            NuvioNavigationItem(
                id = downloadsId,
                label = downloadsLabel,
                selected = selectedId == downloadsId,
                icon = Icons.Default.Download,
                onClick = { onItemSelected(downloadsId) }
            ),
            NuvioNavigationItem(
                id = settingsId,
                label = settingsLabel,
                selected = selectedId == settingsId,
                icon = Icons.Default.Settings,
                onClick = { onItemSelected(settingsId) }
            ),
        )
    }
    NuvioFloatingBottomBar(items = items, modifier = modifier)
}

/**
 * Floating glass capsule navigation bar replicating Nuvio Mobile.
 * Semi-translucent dark pill with subtle rim lighting and gold active state.
 */
@Composable
fun NuvioFloatingBottomBar(
    items: List<NuvioNavigationItem>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF0181A1F),
                            Color(0xF80E1013),
                        )
                    )
                )
                .border(
                    width = 0.8.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x35FFFFFF),
                            Color(0x10FFFFFF),
                        )
                    ),
                    shape = RoundedCornerShape(50.dp)
                )
                .padding(horizontal = 6.dp, vertical = 5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEach { item ->
                    val interactionSource = remember { MutableInteractionSource() }
                    val contentColor by animateColorAsState(
                        targetValue = if (item.selected) NuvioColors.Gold else Color(0xFF888F9E),
                        animationSpec = tween(durationMillis = 200),
                        label = "nav_item_color"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(40.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = item.onClick
                            )
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = contentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = item.label,
                                color = contentColor,
                                fontSize = 10.sp,
                                fontWeight = if (item.selected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
