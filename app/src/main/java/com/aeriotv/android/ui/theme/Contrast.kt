package com.aeriotv.android.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

/** WCAG contrast after compositing translucent foregrounds onto the surface. */
fun contrastRatio(foreground: Color, background: Color): Float {
    val a = foreground.compositeOver(background).luminance()
    val b = background.luminance()
    return (maxOf(a, b) + 0.05f) / (minOf(a, b) + 0.05f)
}

/** Choose the more legible ink for a filled button, badge, or focus platter. */
fun readableOn(background: Color): Color =
    if (contrastRatio(Color.White, background) >= contrastRatio(Color.Black, background)) {
        Color.White
    } else {
        Color.Black
    }

/** Preserve a colour where possible, lifting its contrast only when needed. */
fun ensureTextContrast(
    foreground: Color,
    backgrounds: List<Color>,
    minimum: Float = 4.5f,
): Color {
    require(backgrounds.isNotEmpty())
    fun score(color: Color) = backgrounds.minOf { contrastRatio(color, it) }
    if (score(foreground) >= minimum) return foreground
    val target = listOf(Color.White, Color.Black).maxBy { score(it) }
    // The app's surfaces share a luminance range. For an arbitrary caller
    // with incompatible surfaces, return the best ink rather than loop.
    if (score(target) < minimum) return target
    val start = foreground.compositeOver(backgrounds.first())
    var low = 0f
    var high = 1f
    repeat(20) {
        val mid = (low + high) / 2f
        if (score(lerp(start, target, mid)) >= minimum) high = mid else low = mid
    }
    return lerp(start, target, high)
}

fun ensureTextContrast(foreground: Color, background: Color, minimum: Float = 4.5f): Color =
    ensureTextContrast(foreground, listOf(background), minimum)

/** Apply to the actual scheme used by Compose, including custom accents. */
fun ColorScheme.withReadableText(extraBackgrounds: List<Color> = emptyList()): ColorScheme {
    val surfaces = listOf(background, surface, surfaceVariant) + extraBackgrounds
    val legibleTertiary = ensureTextContrast(tertiary, surfaces)
    val primaryPanel = primary.copy(alpha = 0.18f).compositeOver(surface)
    val secondaryPanel = secondary.copy(alpha = 0.18f).compositeOver(surface)
    return copy(
        onPrimary = readableOn(primary),
        primaryContainer = primaryPanel,
        onPrimaryContainer = ensureTextContrast(primary, primaryPanel),
        onSecondary = readableOn(secondary),
        secondaryContainer = secondaryPanel,
        onSecondaryContainer = ensureTextContrast(secondary, secondaryPanel),
        tertiary = legibleTertiary,
        onTertiary = readableOn(legibleTertiary),
        onBackground = ensureTextContrast(onBackground, surfaces),
        onSurface = ensureTextContrast(onSurface, surface),
        onSurfaceVariant = ensureTextContrast(onSurfaceVariant, surfaces),
        onError = readableOn(error),
        surfaceTint = primary,
        surfaceDim = background,
        surfaceBright = surface,
        surfaceContainerLowest = background,
        surfaceContainerLow = surface,
        surfaceContainer = surface,
        surfaceContainerHigh = surface,
        surfaceContainerHighest = surface,
    )
}

data class GuideCellInk(val title: Color, val secondary: Color, val time: Color, val focus: Color)

/** Called once per distinct guide fill; text layout caches stay reusable. */
fun guideCellInk(scheme: ColorScheme, opaqueFill: Color): GuideCellInk = GuideCellInk(
    title = ensureTextContrast(scheme.onSurface, opaqueFill),
    secondary = ensureTextContrast(scheme.onSurfaceVariant, opaqueFill),
    time = ensureTextContrast(scheme.tertiary, opaqueFill),
    focus = ensureTextContrast(scheme.primary, opaqueFill, minimum = 3f),
)
