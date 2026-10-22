package com.lagradost.cloudstream4.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NuvioHeroBanner(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    image: @Composable BoxScope.() -> Unit,
    actions: @Composable (() -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))) {
        image()
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(
            0f to androidx.compose.ui.graphics.Color.Transparent,
            0.42f to androidx.compose.ui.graphics.Color(0x660D0D0D),
            1f to NuvioColors.Background,
        )))
        Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 26.sp,
                fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (!description.isNullOrBlank()) {
                Text(description, Modifier.padding(top = 6.dp), color = NuvioColors.TextSecondary,
                    fontSize = 13.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
            actions?.let { Column(Modifier.padding(top = 14.dp)) { it() } }
        }
    }
}
