package com.lagradost.cloudstream4.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (isLoading) {
            NuvioHomeLoading()
        } else if (error != null) {
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
                showRandom = showRandom,
                onRandomClick = onRandomClick,
            )
        }
    }
}

@Composable
private fun NuvioHomeLoading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        NuvioPosterCard(
            title = "Loading...",
            modifier = Modifier.padding(16.dp).width(125.dp).aspectRatio(2f / 3f),
            image = { Box(Modifier.fillMaxSize().background(NuvioColors.Card)) },
        )
    }
}

@Composable
private fun NuvioHomeError(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = error,
            color = NuvioColors.TextSecondary,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
        androidx.compose.material3.Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = NuvioColors.Gold,
                contentColor = NuvioColors.Background,
            ),
        ) {
            Text("Retry", fontWeight = FontWeight.SemiBold)
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
    showRandom: Boolean,
    onRandomClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                NuvioProviderChip(
                    label = apiName ?: "Select Source",
                    onClick = onApiClick,
                )
            }

            if (heroItems.isNotEmpty()) {
                item {
                    NuvioHeroCarousel(
                        items = heroItems,
                        onClick = onHeroClick,
                    )
                }
            }

            itemsIndexed(pageItems, key = { _, s -> s.title }) { _, section ->
                NuvioContentRow(
                    items = section.items,
                    key = { it.title + it.position },
                    title = section.title,
                    subtitle = section.subtitle,
                    itemContent = { item ->
                        Box(
                            modifier = Modifier
                                .width(125.dp)
                                .fillMaxHeight()
                                .clickable { onItemClick(section, item.position) }
                        ) {
                            NuvioPosterCard(
                                title = item.title,
                                posterUrl = item.posterUrl,
                                subtitle = item.subtitle,
                                rating = item.rating,
                                image = item.image,
                            )
                        }
                    },
                    onExpandClick = { onExpandClick(section) },
                )
            }
        }

        if (showRandom) {
            FloatingActionButton(
                onClick = onRandomClick,
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                containerColor = NuvioColors.Gold,
                contentColor = NuvioColors.Background,
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Random",
                )
            }
        }
    }
}

@Composable
private fun NuvioProviderChip(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = NuvioColors.Surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, NuvioColors.Gold.copy(alpha = 0.5f)),
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Text(
                text = "SOURCE",
                color = NuvioColors.Gold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                color = NuvioColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun NuvioHeroCarousel(
    items: List<HeroItem>,
    modifier: Modifier = Modifier,
    onClick: (HeroItem) -> Unit,
) {
    NuvioContentRow(
        items = items,
        title = "Continue Watching",
        subtitle = null,
        itemContent = { hero ->
            NuvioHeroCard(
                hero = hero,
                modifier = Modifier.width(280.dp).aspectRatio(16f / 9f),
                onClick = { onClick(hero) },
            )
        },
    )
}

@Composable
private fun NuvioHeroCard(
    hero: HeroItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxSize()
            .clickable { onClick() },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (hero.image != null) {
                hero.image.invoke(this)
            } else if (!hero.posterUrl.isNullOrBlank()) {
                AsyncImage(
                    model = hero.posterUrl,
                    contentDescription = hero.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Box(Modifier.fillMaxSize().background(NuvioColors.Card))
            }
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to NuvioColors.Background.copy(alpha = 0.8f),
                        1f to NuvioColors.Background,
                    )
                ),
        )
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                text = hero.title,
                color = NuvioColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!hero.subtitle.isNullOrBlank()) {
                Text(
                    text = hero.subtitle,
                    color = NuvioColors.TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

data class HomePageSection(
    val title: String,
    val subtitle: String? = null,
    val items: List<HomePageItem>,
)

data class HomePageItem(
    val title: String,
    val subtitle: String? = null,
    val rating: String? = null,
    val posterUrl: String? = null,
    val image: (@Composable () -> Unit)? = null,
    val position: Int = 0,
)

data class HeroItem(
    val title: String,
    val subtitle: String? = null,
    val posterUrl: String? = null,
    val image: (@Composable BoxScope.() -> Unit)? = null,
)
