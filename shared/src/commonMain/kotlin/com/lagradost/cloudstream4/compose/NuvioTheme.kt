package com.lagradost.cloudstream4.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Nuvio's dark palette, scoped to the redesigned UI. */
object NuvioColors {
    val Background = Color(0xFF0D0D0D)
    val Surface = Color(0xFF1A1A1A)
    val Card = Color(0xFF242424)
    val CardAlt = Color(0xFF1F1F1F)
    val Gold = Color(0xFFE8A91C)
    val GoldHighlight = Color(0xFFFFD45C)
    val GoldFocus = Color(0xFF3D2D1A)
    val Blue = Color(0xFF3185F5)
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFAAB2BE)
    val TextTertiary = Color(0xFF687381)
}

enum class NuvioAccent { Gold, Blue }

@Composable
fun NuvioTheme(
    accent: NuvioAccent = NuvioAccent.Gold,
    content: @Composable () -> Unit,
) {
    val primary = when (accent) {
        NuvioAccent.Gold -> NuvioColors.Gold
        NuvioAccent.Blue -> NuvioColors.Blue
    }
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = primary,
            onPrimary = Color(0xFF111111),
            secondary = primary,
            background = NuvioColors.Background,
            onBackground = NuvioColors.TextPrimary,
            surface = NuvioColors.Surface,
            onSurface = NuvioColors.TextPrimary,
            surfaceVariant = NuvioColors.Card,
            onSurfaceVariant = NuvioColors.TextSecondary,
        ),
        content = content,
    )
}
