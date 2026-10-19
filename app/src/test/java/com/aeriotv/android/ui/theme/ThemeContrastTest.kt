package com.aeriotv.android.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeContrastTest {
    private fun assertContrast(ink: Color, fill: Color, minimum: Float = 4.5f) {
        val actual = contrastRatio(ink, fill)
        assertTrue("$ink on $fill: $actual, expected >= $minimum", actual >= minimum - 0.001f)
    }

    @Test
    fun whiteAndBlackCustomAccentButtonsRemainReadableInBothModes() {
        for (dark in listOf(false, true)) {
            for (accent in listOf(Color.White, Color.Black, Color.Yellow, Color(0xFF777777))) {
                val palette = themePalette(AppTheme.RoyalBlue, customAccent = accent, isDark = dark)
                val scheme = palette.scheme
                assertContrast(scheme.onPrimary, scheme.primary)
                assertContrast(palette.textAccent, scheme.background)
                assertContrast(palette.textAccent, scheme.surface)
            }
        }
    }

    @Test
    fun allPresetsKeepTextReadableAcrossAppearanceAndContrastSettings() {
        for (theme in AppTheme.entries) for (dark in listOf(false, true)) {
            for (black in listOf(false, true)) for (tv in listOf(false, true)) {
                for (contrast in listOf(0f, 0.5f, 1f)) {
                    val palette = themePalette(theme, isDark = dark, trueBlack = black, isTv = tv, textContrast = contrast)
                    val c = palette.scheme
                    assertContrast(c.onPrimary, c.primary)
                    assertContrast(c.onPrimaryContainer, c.primaryContainer)
                    assertContrast(c.onSecondary, c.secondary)
                    assertContrast(c.onSecondaryContainer, c.secondaryContainer)
                    assertContrast(c.onError, c.error)
                    assertContrast(c.onErrorContainer, c.errorContainer)
                    assertContrast(c.onTertiaryContainer, c.tertiaryContainer)
                    assertContrast(c.inverseOnSurface, c.inverseSurface)
                    assertContrast(c.inversePrimary, c.inverseSurface)
                    for (surface in listOf(c.background, c.surface, c.surfaceVariant)) {
                        assertContrast(c.onBackground, surface)
                        assertContrast(c.onSurfaceVariant, surface)
                        assertContrast(c.tertiary, surface)
                        assertContrast(palette.textAccent, surface)
                    }
                    assertContrast(c.onSurface, c.surface)
                }
            }
        }
    }

    @Test
    fun royalBlueTextIsReadableAtEveryGradientStop() {
        val palette = themePalette(AppTheme.RoyalBlue)
        for (stop in listOf(RoyalBlueTop, palette.scheme.background, RoyalBlueBottom, RoyalBlueDecoratedTop)) {
            assertContrast(palette.scheme.onBackground, stop)
            assertContrast(palette.scheme.onSurfaceVariant, stop)
            assertContrast(palette.scheme.tertiary, stop)
            assertContrast(palette.textAccent, stop)
        }
    }

    @Test
    fun queFocusAndSelectedStatesRemainReadableWithAWhiteCustomAccent() {
        val scheme = themePalette(AppTheme.RoyalBlue, customAccent = Color.White).scheme
        for (fill in listOf(RoyalBlueSurface, RoyalBlueRaised, RoyalBlueRail, RoyalBlueFocus)) {
            assertContrast(Color.White, fill)
            assertContrast(guideCellInk(scheme, fill).title, fill)
            assertContrast(guideCellInk(scheme, fill).focus, fill, 3f)
        }
        assertContrast(RoyalBlueRail, Color.White)
        assertEquals(RoyalBlueRaised, scheme.primaryContainer)
        assertEquals(RoyalBlueSurface, scheme.secondaryContainer)
        assertTrue(scheme.onSurface != scheme.surface)
        assertTrue(scheme.onPrimary != scheme.primary)
    }

    @Test
    fun guideTextAndFocusRemainVisibleOnCategoryAndFocusFills() {
        val categories = listOf(Color.White, Color.Black, Color.Yellow, Color.Red, Color.Blue, Color.Green)
        for (theme in AppTheme.entries) for (dark in listOf(false, true)) {
            val scheme = themePalette(theme, isDark = dark).scheme
            for (category in categories) for (alpha in listOf(0.03f, 0.18f, 0.22f, 0.28f, 0.35f, 0.45f, 0.55f)) {
                val fill = category.copy(alpha = alpha).compositeOver(scheme.background)
                val ink = guideCellInk(scheme, fill)
                assertContrast(ink.title, fill)
                assertContrast(ink.secondary, fill)
                assertContrast(ink.time, fill)
                assertContrast(ink.focus, fill, 3f)
            }
        }
    }

    @Test
    fun whiteFocusedSurfacesAndOrangeBadgesUseDarkInk() {
        assertEquals(Color.Black, readableOn(Color.White))
        assertContrast(readableOn(Color(0xFFFFA502)), Color(0xFFFFA502))
        val light = themePalette(AppTheme.RoyalBlue, isDark = false).scheme
        assertContrast(light.onSurface, light.surfaceVariant)
    }

    @Test
    fun translucentTextIsMeasuredAfterCompositingAndGoodColoursArePreserved() {
        assertEquals(1f, contrastRatio(Color.White.copy(alpha = 0f), Color.Black), 0.001f)
        assertEquals(Color.White, ensureTextContrast(Color.White, Color.Black))
        assertContrast(ensureTextContrast(Color.White.copy(alpha = 0.1f), Color.Black), Color.Black)
    }
}
