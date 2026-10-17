package com.aeriotv.android.ui.theme

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalAppTheme = staticCompositionLocalOf { AppTheme.RoyalBlue }

/** True when Appearance > True Black Background is on (GH #105). */
val LocalTrueBlack = staticCompositionLocalOf { false }

/** OLED true-black palette: page backgrounds pure black, cards / sheets a very
 *  dark gray so elevation still reads. Accent and text colors are unchanged. */
val TrueBlackBackground = Color(0xFF000000)
val TrueBlackSurface = Color(0xFF0A0A0A)

/** Dark-rendition card surface for [theme], honoring True Black. For chrome
 *  that forces the dark palette over video (player menus, sheets). */
fun darkCardBackground(theme: AppTheme, trueBlack: Boolean): Color =
    if (trueBlack) TrueBlackSurface else theme.cardBackground

/** Dark-rendition page background for [theme], honoring True Black. */
fun darkAppBackground(theme: AppTheme, trueBlack: Boolean): Color =
    if (trueBlack) TrueBlackBackground else theme.appBackground

@Composable
fun AerioTVTheme(
    appTheme: AppTheme = AppTheme.RoyalBlue,
    customAccent: Color? = null,
    // Theme hue and light/dark appearance remain independent preferences.
    appearanceMode: AppearanceMode = AppearanceMode.Dark,
    // Appearance > True Black Background (GH #105). Affects the dark rendition
    // only; Light mode keeps its own surfaces.
    trueBlack: Boolean = false,
    content: @Composable () -> Unit,
) {
    // Only System follows the OS; Dark and Light are explicit overrides.
    val isDark = when (appearanceMode) {
        AppearanceMode.Dark -> true
        AppearanceMode.Light -> false
        AppearanceMode.System -> isSystemInDarkTheme()
    }

    val isTv = (LocalConfiguration.current.uiMode and Configuration.UI_MODE_TYPE_MASK) ==
        Configuration.UI_MODE_TYPE_TELEVISION
    val palette = themePalette(
        appTheme = appTheme,
        customAccent = customAccent,
        isDark = isDark,
        trueBlack = trueBlack,
        isTv = isTv,
        textContrast = LocalTextContrast.current,
    )

    // Match the system-bar icon contrast to the resolved mode so the status /
    // navigation bar glyphs stay legible: dark icons over a light background,
    // light icons over a dark one. Player chrome hides the bars entirely, so
    // this never fights the always-dark video scrims.
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as? Activity)?.window
        if (window != null) {
            SideEffect {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !isDark
                controller.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(
        LocalAppTheme provides appTheme,
        LocalIsDarkTheme provides isDark,
        LocalTrueBlack provides trueBlack,
        LocalBaseTextColors provides palette.baseTextColors,
        LocalTextAccent provides palette.textAccent,
    ) {
        MaterialTheme(
            colorScheme = palette.scheme,
            typography = aerioTypography(isTv),
            content = content,
        )
    }
}
