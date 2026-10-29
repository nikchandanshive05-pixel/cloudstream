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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
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
    if (isLoading || detail == null) {
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

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(NuvioColors.Background),
    ) {
        val isWideScreen = maxWidth > 720.dp
        val contentMaxWidth = if (isWideScreen) 860.dp else maxWidth

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 64.dp),
        ) {
            // Section 1: Hero Backdrop & Top Floating Controls
            item(key = "detail_hero") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isWideScreen) 440.dp else 360.dp),
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
                            .height(90.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
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
                                        Color(0x990D0D0D),
                                        NuvioColors.Background,
                                    )
                                )
                            )
                    )

                    // Floating Back Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            onClick = onBackClick,
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.45f),
                            modifier = Modifier.size(40.dp),
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
                    }
                }
            }

            // Section 2: Header Metadata, Title, Genres, Overview & Actions (Centered Container)
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
                        // Title or Graphic Logo
                        if (!detail.logoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = detail.logoUrl,
                                contentDescription = detail.title,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .heightIn(max = 72.dp)
                                    .fillMaxWidth(0.7f),
                            )
                        } else {
                            Text(
                                text = detail.title,
                                fontSize = if (isWideScreen) 32.sp else 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = NuvioColors.TextPrimary,
                                letterSpacing = (-0.5).sp,
                            )
                        }

                        // Metadata Badges Row: Year • Runtime • [Content Rating] • [IMDb ⭐ 7.5]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            if (!detail.year.isNullOrBlank()) {
                                Text(
                                    text = detail.year,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NuvioColors.TextSecondary,
                                )
                            }

                            if (!detail.duration.isNullOrBlank()) {
                                Text(
                                    text = detail.duration,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NuvioColors.TextSecondary,
                                )
                            }

                            if (!detail.contentRating.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.Transparent,
                                    border = BorderStroke(1.dp, Color(0x55FFFFFF)),
                                    modifier = Modifier.padding(horizontal = 2.dp),
                                ) {
                                    Text(
                                        text = detail.contentRating,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NuvioColors.TextPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    )
                                }
                            }

                            if (!detail.rating.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = NuvioColors.GoldHighlight,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    ) {
                                        Text(
                                            text = "IMDb",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF111111),
                                        )
                                        Text(
                                            text = detail.rating,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF111111),
                                        )
                                    }
                                }
                            }
                        }

                        // Genres Line: Action • Science Fiction
                        if (detail.genres.isNotEmpty()) {
                            Text(
                                text = detail.genres.joinToString(" • "),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = NuvioColors.TextSecondary,
                            )
                        }

                        // Director Line: Director: Sam Raimi
                        if (!detail.director.isNullOrBlank()) {
                            Text(
                                text = "Director: ${detail.director}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = NuvioColors.TextSecondary,
                            )
                        }

                        // Synopsis / Plot with smooth expand
                        if (!detail.synopsis.isNullOrBlank()) {
                            var isExpanded by remember { mutableStateOf(false) }
                            Text(
                                text = detail.synopsis,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp,
                                color = NuvioColors.TextSecondary,
                                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .clickable { isExpanded = !isExpanded }
                                    .animateContentSize(),
                            )
                        }

                        // Primary Action Row: Pill Button [ ▶ Play / Resume ] + Circle [ ... ]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 6.dp),
                        ) {
                            Surface(
                                onClick = onPlayClick,
                                shape = RoundedCornerShape(26.dp),
                                color = Color.White,
                                modifier = Modifier
                                    .height(48.dp)
                                    .weight(1f, fill = false)
                                    .widthIn(min = 160.dp, max = 280.dp),
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 22.dp),
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Icon(
                                            Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(20.dp),
                                        )
                                        Text(
                                            text = actionState.playButtonText,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                        )
                                    }

                                    // Subtle progress line at bottom of pill if resuming
                                    if (actionState.progressPercent != null && actionState.progressPercent > 0f) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .fillMaxWidth()
                                                .height(3.dp)
                                                .background(Color(0xFFE0E0E0)),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(actionState.progressPercent.coerceIn(0f, 1f))
                                                    .height(3.dp)
                                                    .background(NuvioColors.Gold),
                                            )
                                        }
                                    }
                                }
                            }

                            // Circular [ ... ] Options Button
                            Surface(
                                onClick = onOptionsClick,
                                shape = CircleShape,
                                color = NuvioColors.Surface,
                                border = BorderStroke(1.dp, NuvioColors.Card),
                                modifier = Modifier.size(48.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.MoreHoriz,
                                        contentDescription = "Options",
                                        tint = NuvioColors.TextPrimary,
                                        modifier = Modifier.size(22.dp),
                                    )
                                }
                            }

                            // Optional Circular Download Button
                            if (actionState.canDownload) {
                                Surface(
                                    onClick = onDownloadClick,
                                    shape = CircleShape,
                                    color = NuvioColors.Surface,
                                    border = BorderStroke(1.dp, NuvioColors.Card),
                                    modifier = Modifier.size(48.dp),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Download,
                                            contentDescription = "Download",
                                            tint = NuvioColors.TextPrimary,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Cast Rail (Circular Headshots)
            if (cast.isNotEmpty()) {
                item(key = "detail_cast_section") {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(max = contentMaxWidth)
                                .fillMaxWidth()
                                .padding(top = 28.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Text(
                                text = "Cast",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = NuvioColors.TextPrimary,
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                items(cast, key = { it.name }) { member ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(76.dp),
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = NuvioColors.Surface,
                                            border = BorderStroke(1.dp, NuvioColors.Card),
                                            modifier = Modifier.size(64.dp),
                                        ) {
                                            if (!member.imageUrl.isNullOrBlank()) {
                                                AsyncImage(
                                                    model = member.imageUrl,
                                                    contentDescription = member.name,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize(),
                                                )
                                            } else {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = member.name.take(1).uppercase(),
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = NuvioColors.Gold,
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(8.dp))

                                        Text(
                                            text = member.name,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NuvioColors.TextPrimary,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )

                                        if (!member.role.isNullOrBlank()) {
                                            Text(
                                                text = member.role,
                                                fontSize = 10.5.sp,
                                                color = NuvioColors.TextSecondary,
                                                textAlign = TextAlign.Center,
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

            // Section 4: TV Series Episodes
            if (episodes.isNotEmpty()) {
                item(key = "detail_episodes_section") {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(max = contentMaxWidth)
                                .fillMaxWidth()
                                .padding(top = 28.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Text(
                                text = "Episodes",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = NuvioColors.TextPrimary,
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )

                            // Season selector chips
                            if (seasons.size > 1) {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 20.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    itemsIndexed(seasons, key = { _, s -> s.seasonNumber }) { _, season ->
                                        NuvioChip(
                                            label = season.name,
                                            selected = season.seasonNumber == selectedSeason,
                                            onClick = { onSeasonSelected(season.seasonNumber) },
                                        )
                                    }
                                }
                            }

                            // Episode Cards
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                episodes.forEach { ep ->
                                    Surface(
                                        onClick = { onEpisodeClick(ep) },
                                        shape = RoundedCornerShape(12.dp),
                                        color = NuvioColors.Surface,
                                        border = BorderStroke(1.dp, NuvioColors.Card),
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            // 16:9 Thumbnail
                                            Box(
                                                modifier = Modifier
                                                    .size(width = 110.dp, height = 66.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(NuvioColors.Card),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                if (!ep.posterUrl.isNullOrBlank()) {
                                                    AsyncImage(
                                                        model = ep.posterUrl,
                                                        contentDescription = ep.name,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize(),
                                                    )
                                                }
                                                Icon(
                                                    Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    tint = Color.White.copy(alpha = 0.85f),
                                                    modifier = Modifier.size(26.dp),
                                                )
                                            }

                                            // Text info
                                            Column(modifier = Modifier.weight(1f)) {
                                                val epLabel = if (ep.episodeNumber != null) "E${ep.episodeNumber} • ${ep.name}" else ep.name
                                                Text(
                                                    text = epLabel,
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = NuvioColors.TextPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                                if (!ep.duration.isNullOrBlank()) {
                                                    Text(
                                                        text = ep.duration,
                                                        fontSize = 11.5.sp,
                                                        color = NuvioColors.TextSecondary,
                                                    )
                                                }
                                                if (!ep.description.isNullOrBlank()) {
                                                    Text(
                                                        text = ep.description,
                                                        fontSize = 11.5.sp,
                                                        color = NuvioColors.TextTertiary,
                                                        maxLines = 2,
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
            }

            // Section 5: Movie Details Table (Status, Release, Runtime, Certification, Country, Language)
            item(key = "detail_info_table") {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = contentMaxWidth)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(
                            text = "Movie Details",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = NuvioColors.TextPrimary,
                        )

                        val detailsList = listOfNotNull(
                            detail.status?.let { "Status" to it },
                            detail.year?.let { "Release Info" to it },
                            detail.duration?.let { "Runtime" to it },
                            detail.contentRating?.let { "Certification" to it },
                            detail.country?.let { "Origin Country" to it },
                            detail.language?.let { "Original Language" to it },
                            detail.provider?.let { "Provider" to it },
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NuvioColors.Surface)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            detailsList.forEachIndexed { idx, (label, value) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 11.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        color = NuvioColors.TextSecondary,
                                    )
                                    Text(
                                        text = value,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = NuvioColors.TextPrimary,
                                    )
                                }
                                if (idx < detailsList.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(0.5.dp)
                                            .background(Color(0x1AFFFFFF))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 6: More Like This (Recommendations Rail)
            if (recommendations.isNotEmpty()) {
                item(key = "detail_recommendations_section") {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(max = contentMaxWidth)
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "More Like This",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NuvioColors.TextPrimary,
                                )
                                Text(
                                    text = "Powered by TMDB",
                                    fontSize = 11.sp,
                                    color = NuvioColors.TextTertiary,
                                )
                            }

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(recommendations, key = { it.id }) { rec ->
                                    Box(
                                        modifier = Modifier
                                            .width(130.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { onRecommendationClick(rec) }
                                    ) {
                                        NuvioPosterCard(
                                            title = rec.title,
                                            posterUrl = rec.posterUrl,
                                            rating = rec.rating,
                                            subtitle = rec.year,
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
}
