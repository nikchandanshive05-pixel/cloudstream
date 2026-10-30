package com.lagradost.cloudstream4.compose

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Immutable
data class HomePageSection(
    val title: String,
    val subtitle: String? = null,
    val items: List<HomePageItem>,
)

@Immutable
data class HomePageItem(
    val title: String,
    val subtitle: String? = null,
    val rating: String? = null,
    val posterUrl: String? = null,
    val image: (@Composable () -> Unit)? = null,
    val position: Int = 0,
)

@Immutable
data class HeroItem(
    val title: String,
    val subtitle: String? = null,
    val posterUrl: String? = null,
    val image: (@Composable BoxScope.() -> Unit)? = null,
)

@Composable
fun NuvioHomeScreen(
    modifier: Modifier = Modifier,
    apiName: String? = null,
    pageItems: List<HomePageSection> = emptyList(),
    heroItems: List<HeroItem> = emptyList(),
    onApiClick: () -> Unit = {},
    onHeroClick: (HeroItem) -> Unit = {},
    onItemClick: (HomePageSection, Int) -> Unit = { _, _ -> },
    onExpandClick: (HomePageSection) -> Unit = {},
    isLoading: Boolean = false,
    error: String? = null,
    onRetry: () -> Unit = {},
    showRandom: Boolean = false,
    onRandomClick: () -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize().background(NuvioColors.Background)) {
        if (isLoading && pageItems.isEmpty() && heroItems.isEmpty()) {
            NuvioHomeLoading()
        } else if (error != null && pageItems.isEmpty()) {
            NuvioHomeError(error, onRetry)
        } else {
            NuvioHomeContent(
                apiName = apiName,
                pageItems = pageItems,
                heroItems = heroItems,
                onApiClick = onApiClick,
                onHeroClick = onHeroClick,
                onItemClick = onItemClick,
                onExpandClick = onExpandClick,
            )
        }
    }
}

@Composable
private fun NuvioHomeLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = NuvioColors.Gold,
            strokeWidth = 2.5.dp,
        )
    }
}

@Composable
private fun NuvioHomeError(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = error,
            color = NuvioColors.TextSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            onClick = onRetry,
            shape = RoundedCornerShape(24.dp),
            color = NuvioColors.Gold,
        ) {
            Text(
                text = "Retry Connection",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
            )
        }
    }
}

@Composable
private fun NuvioHomeContent(
    apiName: String?,
    pageItems: List<HomePageSection>,
    heroItems: List<HeroItem>,
    onApiClick: () -> Unit,
    onHeroClick: (HeroItem) -> Unit,
    onItemClick: (HomePageSection, Int) -> Unit,
    onExpandClick: (HomePageSection) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp),
    ) {
        // Section 1: Hero Banner (Full-bleed cinematic backdrop with logo, metadata pills, View Details button & dots)
        if (heroItems.isNotEmpty()) {
            item(key = "nuvio_hero_section") {
                NuvioHeroCinematicPager(
                    items = heroItems,
                    apiName = apiName,
                    onApiClick = onApiClick,
                    onHeroClick = onHeroClick,
                )
            }
        } else {
            // Fallback top provider bar if no hero items
            item(key = "nuvio_top_source_bar") {
                NuvioSourceBar(apiName = apiName, onApiClick = onApiClick)
            }
        }

        // Section 2: Regular dynamic homepage rows (Continue watching, trending, collections)
        itemsIndexed(pageItems, key = { _, s -> s.title }) { _, section ->
            val isContinueWatching = section.title.contains("continue", ignoreCase = true) ||
                    section.title.contains("resume", ignoreCase = true)

            if (isContinueWatching) {
                NuvioContinueWatchingRow(
                    section = section,
                    onItemClick = onItemClick,
                )
            } else {
                NuvioSectionRow(
                    section = section,
                    onItemClick = onItemClick,
                    onExpandClick = onExpandClick,
                )
            }
        }
    }
}

@Composable
private fun NuvioSourceBar(
    apiName: String?,
    onApiClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 48.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            onClick = onApiClick,
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1E2024),
            border = BorderStroke(0.7.dp, Color(0x33E8A91C)),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "SOURCE",
                    color = NuvioColors.Gold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "•",
                    color = Color.Gray,
                    fontSize = 11.sp,
                )
                Text(
                    text = apiName ?: "Select Provider",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Switch Source",
                    tint = NuvioColors.Gold,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun NuvioHeroCinematicPager(
    items: List<HeroItem>,
    apiName: String?,
    onApiClick: () -> Unit,
    onHeroClick: (HeroItem) -> Unit,
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var dragAccumulator by remember { mutableFloatStateOf(0f) }
    val safeIndex = if (items.isNotEmpty()) currentIndex.coerceIn(0, items.size - 1) else 0
    val currentItem = items.getOrNull(safeIndex) ?: return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(490.dp)
            .pointerInput(items.size) {
                if (items.size > 1) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            dragAccumulator += dragAmount
                        },
                        onDragEnd = {
                            val threshold = 50f
                            if (dragAccumulator < -threshold) {
                                // Swiped left -> next
                                currentIndex = (currentIndex + 1) % items.size
                            } else if (dragAccumulator > threshold) {
                                // Swiped right -> prev
                                currentIndex = (currentIndex - 1 + items.size) % items.size
                            }
                            dragAccumulator = 0f
                        },
                        onDragCancel = {
                            dragAccumulator = 0f
                        }
                    )
                }
            },
    ) {
        // High-res Backdrop with Smooth Crossfade
        Crossfade(
            targetState = currentItem,
            animationSpec = tween(durationMillis = 400),
            modifier = Modifier.fillMaxSize(),
            label = "hero_crossfade"
        ) { heroItem ->
            val poster = heroItem.posterUrl
            if (!poster.isNullOrBlank()) {
                AsyncImage(
                    model = poster,
                    contentDescription = heroItem.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF16171B))
                )
            }
        }

        // Top Subtle Scrim with Source Chip (padded below system status bar)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.88f), Color.Transparent)
                    )
                )
                .padding(horizontal = 16.dp)
                .padding(top = 44.dp, bottom = 12.dp)
        ) {
            Surface(
                onClick = onApiClick,
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.55f),
                border = BorderStroke(0.8.dp, NuvioColors.Gold.copy(alpha = 0.6f)),
                modifier = Modifier.align(Alignment.TopStart),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "SOURCE",
                        color = NuvioColors.Gold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "•",
                        color = Color.Gray,
                        fontSize = 10.sp,
                    )
                    Text(
                        text = apiName ?: "Select",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        // Bottom Atmospheric Blend Scrim into #0D0D0D
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.45f to Color(0x660D0D0D),
                        0.75f to Color(0xCC0D0D0D),
                        1f to NuvioColors.Background,
                    )
                )
        )

        // Bottom Overlay Content: Title, Metadata Chips, [ View Details ] Pill, Pager Dots
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Title
            Text(
                text = currentItem.title,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            // Metadata row (Series • Comedy • 40m)
            val subtitle = currentItem.subtitle
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = Color(0xFFC7CBD3),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Quality / Status badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0x33FFFFFF),
                    border = BorderStroke(0.6.dp, Color(0x44FFFFFF)),
                ) {
                    Text(
                        text = "HD",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0x22E8A91C),
                    border = BorderStroke(0.6.dp, NuvioColors.Gold.copy(alpha = 0.5f)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = "IMDb",
                            color = NuvioColors.Gold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = "8.8",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // White rounded pill [ View Details ]
            Surface(
                onClick = { onHeroClick(currentItem) },
                shape = RoundedCornerShape(40.dp),
                color = Color.White,
                shadowElevation = 4.dp,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 34.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "View Details",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Pager dots indicator (● ○ ○ ○)
            if (items.size > 1) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    items.take(8).forEachIndexed { idx, _ ->
                        val isSelected = idx == safeIndex
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(if (isSelected) 8.dp else 6.dp)
                                .background(if (isSelected) Color.White else Color(0x55FFFFFF))
                                .clickable { currentIndex = idx }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NuvioContinueWatchingRow(
    section: HomePageSection,
    onItemClick: (HomePageSection, Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = section.title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(section.items) { item ->
                NuvioContinueWatchingCard(
                    item = item,
                    onClick = { onItemClick(section, item.position) },
                )
            }
        }
    }
}

@Composable
private fun NuvioContinueWatchingCard(
    item: HomePageItem,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(260.dp)
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1A1C20))
            .clickable(onClick = onClick)
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

        // Gradient scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to Color(0x66000000),
                        1f to Color(0xF00D0D0D),
                    )
                )
        )

        // Top right "1h 14m left" or duration badge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.65f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = item.subtitle ?: "Resume",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        // Bottom title & progress bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0x44FFFFFF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .fillMaxHeight()
                        .background(NuvioColors.Gold)
                )
            }
        }
    }
}

@Composable
private fun NuvioSectionRow(
    section: HomePageSection,
    onItemClick: (HomePageSection, Int) -> Unit,
    onExpandClick: (HomePageSection) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = section.title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Expand",
                tint = Color(0xFF8E95A5),
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onExpandClick(section) }
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(section.items) { item ->
                NuvioPosterItemCard(
                    item = item,
                    onClick = { onItemClick(section, item.position) },
                )
            }
        }
    }
}

@Composable
private fun NuvioPosterItemCard(
    item: HomePageItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(124.dp)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E2025))
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

            // Score badge if available
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
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = NuvioColors.Gold,
                        modifier = Modifier.size(10.dp),
                    )
                    Text(
                        text = rating,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Text(
            text = item.title,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
