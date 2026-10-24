package com.lagradost.cloudstream4.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NuvioLibraryTab(
    val id: Int,
    val title: String,
    val count: Int? = null,
)

data class NuvioLibraryItem(
    val id: String,
    val title: String,
    val posterUrl: String?,
    val rating: String? = null,
    val subtitle: String? = null,
    val watchProgressPercent: Float? = null,
)

@Composable
fun NuvioLibraryScreen(
    currentTab: Int,
    tabs: List<NuvioLibraryTab>,
    onTabSelected: (Int) -> Unit,
    items: List<NuvioLibraryItem>,
    onItemClick: (NuvioLibraryItem) -> Unit,
    onSortClick: () -> Unit,
    onSyncClick: () -> Unit,
    syncApiName: String? = null,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NuvioColors.Background),
    ) {
        // Top Header Row: Title + Sync Source + Sort Action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "Library",
                    color = NuvioColors.TextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                )
                if (!syncApiName.isNullOrBlank()) {
                    Text(
                        text = "SYNCED WITH $syncApiName",
                        color = NuvioColors.Gold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    onClick = onSyncClick,
                    shape = CircleShape,
                    color = NuvioColors.Surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NuvioColors.Card),
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = "Sync Source",
                            tint = NuvioColors.Gold,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                Surface(
                    onClick = onSortClick,
                    shape = CircleShape,
                    color = NuvioColors.Surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NuvioColors.Card),
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Sort,
                            contentDescription = "Sort Library",
                            tint = NuvioColors.TextPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }

        // Horizontal Category Tabs
        if (tabs.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(tabs, key = { _, tab -> tab.id }) { index, tab ->
                    val isSelected = tab.id == currentTab || (currentTab == 0 && index == 0)
                    NuvioChip(
                        label = if (tab.count != null && tab.count > 0) "${tab.title} (${tab.count})" else tab.title,
                        selected = isSelected,
                        onClick = { onTabSelected(tab.id) },
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Content: Loading vs Empty vs Grid
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = NuvioColors.Gold,
                    strokeWidth = 2.5.dp,
                )
            }
        } else if (items.isEmpty()) {
            NuvioLibraryEmptyPrompt()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 110.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(items, key = { it.id }) { item ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onItemClick(item) },
                    ) {
                        NuvioPosterCard(
                            title = item.title,
                            posterUrl = item.posterUrl,
                            subtitle = item.subtitle,
                            rating = item.rating,
                        )

                        // Subtle watch progress bar overlay at the bottom of the card
                        if (item.watchProgressPercent != null && item.watchProgressPercent > 0f) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .background(NuvioColors.CardAlt),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = item.watchProgressPercent.coerceIn(0f, 1f))
                                        .height(3.dp)
                                        .background(NuvioColors.Gold),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NuvioLibraryEmptyPrompt(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(NuvioColors.Surface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    tint = NuvioColors.Gold,
                    modifier = Modifier.size(32.dp),
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Your Library is Empty",
                color = NuvioColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Bookmark movies and shows to keep track of what you want to watch and your progress.",
                color = NuvioColors.TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}
