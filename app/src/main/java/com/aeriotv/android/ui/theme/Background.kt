package com.aeriotv.android.ui.theme

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

// Que's blue surfaces stay dark enough for normal-size white TV text.
val RoyalBlueTop = Color(0xFF2C5DE0)
val RoyalBlueBottom = Color(0xFF0737D6)
val RoyalBlueRail = Color(0xFF0731CF)
val RoyalBlueSurface = Color(0xFF1046E7)
val RoyalBlueRaised = Color(0xFF2858D7)
val RoyalBlueFocus = Color(0xFF20265F)
// Bound the brightest possible overlap of the decorative circles for contrast.
val RoyalBlueDecoratedTop = Color.White.copy(alpha = 0.09f).compositeOver(RoyalBlueTop)

@Composable
fun isQueTheme(): Boolean = LocalAppTheme.current == AppTheme.RoyalBlue &&
    LocalIsDarkTheme.current && !LocalTrueBlack.current

/** Blue glow from the upper left, with a saturated cobalt lower-right edge. */
@Composable
fun appBackgroundBrush(): Brush {
    val background = MaterialTheme.colorScheme.background
    return if (isQueTheme()) {
        Brush.linearGradient(listOf(RoyalBlueTop, background, RoyalBlueBottom))
    } else {
        Brush.verticalGradient(listOf(background, background))
    }
}

/** Shared backdrop for the home shell and setup screens; it never takes focus. */
@Composable
fun Modifier.appBackground(): Modifier {
    val que = isQueTheme()
    return background(appBackgroundBrush()).then(
        if (!que) Modifier else Modifier.drawBehind {
            val unit = minOf(size.width / 960f, size.height / 540f)
            val decoration = Color.White.copy(alpha = 0.035f)
            drawCircle(decoration, 108f * unit, Offset(size.width * 0.82f, 52f * unit))
            drawCircle(decoration, 59f * unit, Offset(size.width * 0.93f, 151f * unit))
            drawCircle(decoration, 38f * unit, Offset(size.width * 0.70f, 163f * unit))
        },
    )
}
