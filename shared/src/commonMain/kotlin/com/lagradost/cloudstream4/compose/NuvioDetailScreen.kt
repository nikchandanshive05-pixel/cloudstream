package com.lagradost.cloudstream4.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Immutable
data class NuvioDetailData(
    val title: String,
    val logoUrl: String? = null,
    val backdropUrl: String? = null,
    val posterUrl: String? = null,
    val year: String? = null,
    val duration: String? = null,
    val rating: String? = null,
    val contentRating: String? = null,
    val genres: List<String> = emptyList(),
    val director: String? = null,
    val synopsis: String? = null,
    val status: String? = null,
    val country: String? = null,
    val language: String? = null,
    val provider: String? = null,
)

@Immutable
data class NuvioCastMember(
    val name: String,
    val role: String? = null,
    val imageUrl: String? = null,
)

@Immutable
data class NuvioEpisodeItem(
    val id: String,
    val episodeNumber: Int? = null,
    val seasonNumber: Int? = null,
    val name: String,
    val description: String? = null,
    val posterUrl: String? = null,
    val duration: String? = null,
    val rating: String? = null,
    val isDownloaded: Boolean = false,
)

@Immutable
data class NuvioSeasonTab(
    val seasonNumber: Int,
    val name: String,
)

@Immutable
data class NuvioRecommendationItem(
    val id: String,
    val title: String,
    val posterUrl: String?,
    val rating: String? = null,
    val year: String? = null,
    val type: String? = null,
)

@Immutable
data class NuvioActionState(
    val isResume: Boolean = false,
    val playButtonText: String = "Play Movie",
    val resumeTimeText: String? = null,
    val progressPercent: Float? = null,
    val isBookmarked: Boolean = false,
    val canDownload: Boolean = false,
)

@Composable
fun NuvioDetailScreen(
    detail: NuvioDetailData?,
    actionState: NuvioActionState,
    cast: List<NuvioCastMember>,
    episodes: List<NuvioEpisodeItem>,
    seasons: List<NuvioSeasonTab>,
    selectedSeason: Int,
    onSeasonSelected: (Int) -> Unit,
    recommendations: List<NuvioRecommendationItem>,
    onPlayClick: () -> Unit,
    onOptionsClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onEpisodeClick: (NuvioEpisodeItem) -> Unit,
    onRecommendationClick: (NuvioRecommendationItem) -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    if (isLoading && detail == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(NuvioColors.Background),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = NuvioColors.Gold, strokeWidth = 2.5.dp)
        }
        return
    }

    if (detail == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(NuvioColors.Background),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Unable to load media details", color = Color.White, fontSize = 16.sp)
                Surface(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(20.dp),
                    color = NuvioColors.Gold,
                ) {
                    Text("Go Back", color = Color.Black, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                }
            }
        }
        return
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(NuvioColors.Background),
    ) {
        val isWideScreen = maxWidth > 720.dp
        val contentMaxWidth = if (isWideScreen) 860.dp else maxWidth

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
        ) {
            // Section 1: Hero Backdrop & Top Floating Controls
            item(key = "detail_hero") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isWideScreen) 460.dp else 400.dp),
                ) {
                    val backdropModel = detail.backdropUrl ?: detail.posterUrl
                    if (!backdropModel.isNullOrBlank()) {
                        AsyncImage(
                            model = backdropModel,
                            contentDescription = detail.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    // Top Scrim for back button visibility
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                                )
                            )
                    )

                    // Bottom Atmospheric Gradient Scrim into deep #0D0D0D
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0x660D0D0D),
                                        Color(0xCC0D0D0D),
                                        NuvioColors.Background,
                                    )
                                )
                            )
                    )

                    // Floating Top Navigation Row (Back, Options/Bookmarks)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            onClick = onBackClick,
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.55f),
                            modifier = Modifier.size(42.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                onClick = onOptionsClick,
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.55f),
                                modifier = Modifier.size(42.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (actionState.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (actionState.isBookmarked) NuvioColors.Gold else Color.White,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Header Metadata, Title, Overview & Play Actions
            item(key = "detail_header_content") {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = contentMaxWidth)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        // Title
                        Text(
                            text = detail.title,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp,
                        )

                        // Metadata Badges (Rating, Year, Duration, Content Rating)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (!detail.rating.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0x22E8A91C),
                                    border = BorderStroke(0.6.dp, NuvioColors.Gold.copy(alpha = 0.6f)),
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
                                            text = detail.rating,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }

                            if (!detail.contentRating.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0x33FFFFFF),
                                    border = BorderStroke(0.6.dp, Color(0x44FFFFFF)),
                                ) {
                                    Text(
                                        text = detail.contentRating,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    )
                                }
                            }

                            if (!detail.duration.isNullOrBlank()) {
                                Text(
                                    text = detail.duration,
                                    color = Color(0xFFC7CBD3),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }

                            if (!detail.year.isNullOrBlank()) {
                                Text(
                                    text = "•",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                )
                                Text(
                                    text = detail.year,
                                    color = Color(0xFFC7CBD3),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }

                        // Genres pills
                        if (detail.genres.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(detail.genres) { genre ->
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color(0xFF1E2024),
                                        border = BorderStroke(0.6.dp, Color(0x33FFFFFF)),
                                    ) {
                                        Text(
                                            text = genre,
                                            color = Color(0xFFC7CBD3),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        )
                                    }
                                }
                            }
                        }

                        // Play / Resume White Pill Action Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Surface(
                                onClick = onPlayClick,
                                shape = RoundedCornerShape(40.dp),
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                shadowElevation = 4.dp,
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = actionState.playButtonText,
                                        color = Color.Black,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }

                            if (actionState.canDownload) {
                                Surface(
                                    onClick = onDownloadClick,
                                    shape = CircleShape,
                                    color = Color(0xFF1E2024),
                                    modifier = Modifier.size(46.dp),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Download,
                                            contentDescription = "Download",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                }
                            }
                        }

                        // Synopsis / Plot
                        if (!detail.synopsis.isNullOrBlank()) {
                            var isExpanded by remember { mutableStateOf(false) }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                                    .clickable { isExpanded = !isExpanded },
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    text = detail.synopsis,
                                    color = Color(0xFFC7CBD3),
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    maxLines = if (isExpanded) 20 else 3,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = if (isExpanded) "Show Less" else "More",
                                    color = NuvioColors.Gold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }

            // Section 3: Cast Members (Circular Avatars)
            if (cast.isNotEmpty()) {
                item(key = "detail_cast") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "Cast",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            items(cast) { member ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(72.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF22242A))
                                    ) {
                                        if (!member.imageUrl.isNullOrBlank()) {
                                            AsyncImage(
                                                model = member.imageUrl,
                                                contentDescription = member.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize(),
                                            )
                                        }
                                    }

                                    Text(
                                        text = member.name,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 4: Seasons & Episodes (for series)
            if (episodes.isNotEmpty()) {
                item(key = "detail_episodes_header") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "Episodes",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        // Season Pills
                        if (seasons.size > 1) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(seasons) { season ->
                                    val isSelected = season.seasonNumber == selectedSeason
                                    Surface(
                                        onClick = { onSeasonSelected(season.seasonNumber) },
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isSelected) NuvioColors.Gold else Color(0xFF1E2024),
                                    ) {
                                        Text(
                                            text = season.name,
                                            color = if (isSelected) Color.Black else Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Episode Cards
                items(episodes, key = { it.id }) { ep ->
                    NuvioEpisodeCard(
                        episode = ep,
                        onClick = { onEpisodeClick(ep) },
                    )
                }
            }

            // Section 5: Recommendations Row
            if (recommendations.isNotEmpty()) {
                item(key = "detail_recommendations") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "More Like This",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(recommendations) { rec ->
                                Column(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .clickable { onRecommendationClick(rec) },
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(2f / 3f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF1E2024))
                                    ) {
                                        if (!rec.posterUrl.isNullOrBlank()) {
                                            AsyncImage(
                                                model = rec.posterUrl,
                                                contentDescription = rec.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize(),
                                            )
                                        }
                                    }

                                    Text(
                                        text = rec.title,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
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
    }
}

@Composable
private fun NuvioEpisodeCard(
    episode: NuvioEpisodeItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Episode 16:9 thumbnail
        Box(
            modifier = Modifier
                .width(120.dp)
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1E2024))
        ) {
            val poster = episode.posterUrl
            if (!poster.isNullOrBlank()) {
                AsyncImage(
                    model = poster,
                    contentDescription = episode.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Episode",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        // Title and description
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            val epNum = episode.episodeNumber?.let { "EP $it • " } ?: ""
            val duration = episode.duration?.let { " • $it" } ?: ""
            Text(
                text = "$epNum${episode.name}",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!episode.description.isNullOrBlank()) {
                Text(
                    text = episode.description,
                    color = Color(0xFF8E95A5),
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (duration.isNotBlank()) {
                Text(
                    text = duration.trimStart(' ', '•'),
                    color = NuvioColors.Gold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
