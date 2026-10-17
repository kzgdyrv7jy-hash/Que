package com.aeriotv.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val RoyalBlueTop = Color(0xFF0050EF)
val RoyalBlueBottom = Color(0xFF002C88)

/** The reference blue gradient; other presets and True Black retain solid fills. */
@Composable
fun appBackgroundBrush(): Brush {
    val background = MaterialTheme.colorScheme.background
    val blue = LocalAppTheme.current == AppTheme.RoyalBlue &&
        LocalIsDarkTheme.current && !LocalTrueBlack.current
    return Brush.verticalGradient(
        if (blue) listOf(RoyalBlueTop, background, RoyalBlueBottom)
        else listOf(background, background),
    )
}
