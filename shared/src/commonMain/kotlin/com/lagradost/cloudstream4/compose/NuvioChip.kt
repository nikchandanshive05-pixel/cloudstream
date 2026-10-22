package com.lagradost.cloudstream4.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NuvioChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val accent = MaterialTheme.colorScheme.primary
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        color = if (selected) accent.copy(alpha = 0.16f) else NuvioColors.Surface,
        contentColor = if (selected) accent else NuvioColors.TextSecondary,
        border = BorderStroke(1.dp, if (selected) accent else NuvioColors.Card),
    ) {
        Text(label, Modifier.padding(horizontal = 15.dp, vertical = 9.dp),
            fontSize = 13.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium)
    }
}
