package com.lagradost.cloudstream4.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NuvioPosterCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    rating: String? = null,
    image: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(12.dp)).background(NuvioColors.Card)
        ) {
            image()
            if (!rating.isNullOrBlank()) {
                Text(
                    text = rating,
                    modifier = Modifier.align(Alignment.TopEnd).padding(7.dp)
                        .clip(RoundedCornerShape(5.dp)).background(NuvioColors.GoldHighlight)
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    color = Color(0xFF111111), fontSize = 11.sp, fontWeight = FontWeight.Bold,
                )
            }
        }
        Text(
            text = title,
            modifier = Modifier.padding(top = 7.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 13.sp, fontWeight = FontWeight.Medium,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
        )
        if (!subtitle.isNullOrBlank()) {
            Text(subtitle, color = NuvioColors.TextSecondary, fontSize = 11.sp, maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        }
    }
}
