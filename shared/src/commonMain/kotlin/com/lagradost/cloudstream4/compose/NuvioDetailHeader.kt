package com.lagradost.cloudstream4.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NuvioDetailHeader(
    title: String,
    metadata: List<String>,
    description: String,
    tags: List<String>,
    modifier: Modifier = Modifier,
    playButtonText: String = "Play",
    downloadButtonText: String = "Download",
    isBookmarked: Boolean = false,
    onPlayClick: (() -> Unit)? = null,
    onDownloadClick: (() -> Unit)? = null,
    onBookmarkClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
) {
    var expanded by remember(title) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        // Title
        Text(
            text = title,
            color = NuvioColors.TextPrimary,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        // Metadata badges (Match score, Year, Rating, Quality tags)
        if (metadata.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                metadata.forEachIndexed { index, item ->
                    val isFirst = index == 0
                    val isQualityBadge = item.equals("HD", ignoreCase = true) ||
                            item.equals("4K", ignoreCase = true) ||
                            item.contains("HDR", ignoreCase = true) ||
                            item.contains("Atmos", ignoreCase = true)

                    Surface(
                        color = when {
                            isFirst -> Color(0x33E8A91C)
                            isQualityBadge -> Color(0x22FFFFFF)
                            else -> NuvioColors.Card
                        },
                        border = when {
                            isFirst -> BorderStroke(0.7.dp, NuvioColors.Gold.copy(alpha = 0.6f))
                            isQualityBadge -> BorderStroke(0.6.dp, Color(0x55FFFFFF))
                            else -> null
                        },
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        Text(
                            text = item,
                            color = if (isFirst) NuvioColors.Gold else Color(0xFFE0E0E0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        // Netflix / Nuvio Dual-Stacked Full-Width CTAs
        if (onPlayClick != null) {
            Spacer(Modifier.height(14.dp))
            // Primary Play Button (White pill, black bold text)
            Button(
                onClick = onPlayClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = playButtonText,
                        color = Color.Black,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        if (onDownloadClick != null) {
            Spacer(Modifier.height(8.dp))
            // Secondary Download Button (Dark translucent pill)
            Button(
                onClick = onDownloadClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF262626),
                    contentColor = Color.White,
                ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = downloadButtonText,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        // Synopsis
        if (description.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = description,
                color = NuvioColors.TextSecondary,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
            )
        }

        // Netflix Hawkins Action Shelf (My List, Rate, Share)
        if (onBookmarkClick != null || onShareClick != null) {
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onBookmarkClick != null) {
                    DetailActionItem(
                        icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        label = if (isBookmarked) "In List" else "My List",
                        tint = if (isBookmarked) NuvioColors.Gold else Color.White,
                        onClick = onBookmarkClick,
                    )
                }
                DetailActionItem(
                    icon = Icons.Default.ThumbUp,
                    label = "Rate",
                    tint = Color.White,
                    onClick = { onBookmarkClick?.invoke() },
                )
                if (onShareClick != null) {
                    DetailActionItem(
                        icon = Icons.Default.Share,
                        label = "Share",
                        tint = Color.White,
                        onClick = onShareClick,
                    )
                }
            }
        }

        // Genre Tags
        if (tags.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                tags.forEach { tag ->
                    Surface(
                        color = NuvioColors.Surface,
                        border = BorderStroke(0.6.dp, NuvioColors.Card),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Text(
                            text = tag,
                            color = NuvioColors.TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailActionItem(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}