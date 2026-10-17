package com.aeriotv.android.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/** Resolved palette shared by the UI and contrast regression tests. */
data class ThemePalette(val scheme: ColorScheme, val baseTextColors: BaseTextColors, val textAccent: Color)

fun themePalette(
    appTheme: AppTheme,
    customAccent: Color? = null,
    isDark: Boolean = true,
    trueBlack: Boolean = false,
    isTv: Boolean = false,
    textContrast: Float = 0f,
): ThemePalette {
    // iOS ThemeManager.useCustomAccent parity: when the user enables a custom
    // accent in Appearance, replace the preset's primary with their hex. Falls
    // back to the preset's own accent when [customAccent] is null. In light mode
    // the preset uses its darkened light accent (the bright dark-mode accents
    // wash out on white); a user-chosen custom accent still wins in both modes.
    val baseAccent = if (isDark) appTheme.accentPrimary else appTheme.lightAccentPrimary
    val effectivePrimary = customAccent ?: baseAccent

    // Surface + ink selection per mode. The dark branch keeps the ORIGINAL
    // values verbatim; the light branch reads the theme's light rendition.
    val appBackground = if (isDark) darkAppBackground(appTheme, trueBlack) else appTheme.lightAppBackground
    val cardBackground = if (isDark) darkCardBackground(appTheme, trueBlack) else appTheme.lightCardBackground
    val textPrimary = if (isDark) TextPrimary else TextPrimaryLight

    // Android TV needs a 10-foot type scale + slightly brighter dim text. iOS
    // keeps a separate ~1.5x tvOS Typography and pushes secondary text opacity
    // to 0.75 (vs 0.65 on phone) for legibility at distance (Colors.swift).
    // Mirrors iOS Colors.swift textSecondary = accent.opacity(0.65 phone / 0.75
    // tvOS). Every secondary copy (subtitles, card descriptions, info-banner
    // body, hint text) renders accent-tinted instead of plain white, which is
    // what gives the iOS Welcome / Add Playlist / Configure screens their soft
    // branded feel. Mapped through Material3 onSurfaceVariant so every call site
    // (`MaterialTheme.colorScheme.onSurfaceVariant`) picks it up.
    //
    // On a WHITE surface an accent-at-0.65 composited over white turns into a
    // washed pastel, so in light mode we both pull the tint toward the dark ink
    // AND raise the alpha to earn real contrast (the accent is already the
    // darkened light-mode accent here).
    val baseTextSecondary = if (isDark) {
        effectivePrimary.copy(alpha = if (isTv) 0.75f else 0.65f)
    } else {
        lerp(effectivePrimary, textPrimary, 0.20f).copy(alpha = 0.90f)
    }
    // Visual-parity polish: iOS has a SECOND, dimmer rung under textSecondary.
    // Colors.swift textTertiary = accent.opacity(0.45 tvOS / 0.28 phone), used
    // for channel numbers, time ranges, and hints so they recede behind the
    // title/description hierarchy. Carried on Material3's otherwise-unused
    // `tertiary` slot; reach it via `MaterialTheme.colorScheme.tertiary`.
    val baseTextTertiary = if (isDark) {
        effectivePrimary.copy(alpha = if (isTv) 0.45f else 0.28f)
    } else {
        lerp(effectivePrimary, textPrimary, 0.15f).copy(alpha = 0.65f)
    }

    // Text Contrast (Appearance > Text Contrast, see TextContrast.kt): blends
    // the TEXT tokens toward plain white (dark) / black (light). 0 = the
    // original values above, untouched. primary stays as-is (it also paints
    // fills and rings); accent TEXT goes through colorScheme.textAccent.
    val baseTextColors = BaseTextColors(
        secondary = baseTextSecondary,
        tertiary = baseTextTertiary,
        accent = effectivePrimary,
    )
    val textSecondary = contrastBlend(baseTextSecondary, textContrast, isDark)
    val textTertiary = contrastBlend(baseTextTertiary, textContrast, isDark)
    val textAccent = contrastBlend(effectivePrimary, textContrast, isDark)

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = effectivePrimary,
            onPrimary = appBackground,
            secondary = appTheme.accentSecondary,
            onSecondary = textPrimary,
            tertiary = textTertiary,
            onTertiary = appBackground,
            background = appBackground,
            onBackground = textPrimary,
            surface = cardBackground,
            onSurface = textPrimary,
            surfaceVariant = cardBackground,
            onSurfaceVariant = textSecondary,
            error = StatusLive,
            onError = TextPrimary,
        )
    } else {
        lightColorScheme(
            primary = effectivePrimary,
            // On a mid-tone/dark light-accent fill, near-white reads best.
            onPrimary = Color.White,
            secondary = appTheme.lightAccentSecondary,
            onSecondary = Color.White,
            tertiary = textTertiary,
            onTertiary = Color.White,
            background = appBackground,
            onBackground = textPrimary,
            surface = cardBackground,
            onSurface = textPrimary,
            surfaceVariant = cardBackground,
            onSurfaceVariant = textSecondary,
            error = StatusLive,
            onError = Color.White,
        )
    }

    val gradientStops = if (appTheme == AppTheme.RoyalBlue && isDark && !trueBlack) {
        listOf(RoyalBlueTop, RoyalBlueBottom)
    } else emptyList()
    return ThemePalette(
        scheme = colorScheme.withReadableText(gradientStops),
        baseTextColors = baseTextColors,
        textAccent = ensureTextContrast(textAccent, listOf(appBackground, cardBackground) + gradientStops),
    )
}
