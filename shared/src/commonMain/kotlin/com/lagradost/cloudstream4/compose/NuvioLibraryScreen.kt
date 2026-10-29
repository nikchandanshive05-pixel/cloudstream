package com.lagradost.cloudstream4.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Immutable
data class NuvioLibraryTab(
    val id: Int,
    val title: String,
    val count: Int? = null,
)

@Immutable
data class NuvioLibraryItem(
    val id: String,
    val title: String,
    val posterUrl: String?,
    val rating: String? = null,
    val subtitle: String? = null,
    val watchProgressPercent: Float? = null,
    val isWatched: Boolean = false,
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
        // Top Header Row: Big bold "Library" + Action Icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "Library",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                )
                if (!syncApiName.isNullOrBlank()) {
                    Text(
                        text = "SYNCED WITH ${syncApiName.uppercase()}",
                        color = NuvioColors.Gold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    onClick = onSyncClick,
                    shape = CircleShape,
                    color = Color(0xFF1E2024),
                    modifier = Modifier.size(38.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = "Sync",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                Surface(
                    onClick = onSortClick,
                    shape = CircleShape,
                    color = Color(0xFF1E2024),
                    modifier = Modifier.size(38.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }

        // Row of Rounded Capsule Category Selectors ([ Saved ] [ Cloud ])
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.15f),
                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.35f)),
            ) {
                Text(
                    text = "Saved",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1C1D21),
            ) {
                Text(
                    text = "Cloud",
                    color = Color(0xFF8E95A5),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                )
            }
        }

        // Horizontal Category Tabs (Watching, Completed, Plan to watch, etc.)
        if (tabs.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(tabs, key = { _, tab -> tab.id }) { index, tab ->
                    val isSelected = tab.id == currentTab || (currentTab == 0 && index == 0)
                    Surface(
                        onClick = { onTabSelected(tab.id) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) NuvioColors.Gold else Color(0xFF1C1D21),
                    ) {
                        val title = if (tab.count != null && tab.count > 0) "${tab.title} (${tab.count})" else tab.title
                        Text(
                            text = title,
                            color = if (isSelected) Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }
        }

        // Filter Pills Row ([ All types v ] [ Recently added v ])
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                onClick = onSortClick,
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1A1C20),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "All types",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF8E95A5),
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Surface(
                onClick = onSortClick,
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1A1C20),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "Recently added",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF8E95A5),
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // Content: Loading vs Empty vs 3-Column Poster Grid
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
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(items, key = { it.id }) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemClick(item) },
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2f / 3f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1C1D21))
                        ) {
                            val poster = item.posterUrl
                            if (!poster.isNullOrBlank()) {
                                AsyncImage(
                                    model = poster,
                                    contentDescription = item.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }

                            // Watched Checkmark Badge (top-right white circle)
                            if (item.isWatched) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Watched",
                                        tint = Color.Black,
                                        modifier = Modifier.size(12.dp),
                                    )
                                }
                            }

                            // Rating Badge (bottom-left)
                            val rating = item.rating
                            if (!rating.isNullOrBlank()) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.75f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Text(
                                        text = "★",
                                        color = NuvioColors.Gold,
                                        fontSize = 9.sp,
                                    )
                                    Text(
                                        text = rating,
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }

                            // Watch progress bar overlay at the bottom
                            if (item.watchProgressPercent != null && item.watchProgressPercent > 0f) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .background(Color(0x55000000)),
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

                        Text(
                            text = item.title,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        if (!item.subtitle.isNullOrBlank()) {
                            Text(
                                text = item.subtitle,
                                color = Color(0xFF8E95A5),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
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
                    .background(Color(0xFF1E2024)),
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
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Bookmark movies and shows to keep track of what you want to watch and your progress.",
                color = Color(0xFF8E95A5),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}
