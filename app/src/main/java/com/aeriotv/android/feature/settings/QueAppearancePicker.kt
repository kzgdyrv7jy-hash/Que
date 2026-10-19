package com.aeriotv.android.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeriotv.android.ui.theme.AppTheme
import com.aeriotv.android.ui.theme.AppearanceMode
import com.aeriotv.android.ui.theme.LocalIsDarkTheme
import com.aeriotv.android.ui.theme.RoyalBlueFocus
import com.aeriotv.android.ui.theme.RoyalBlueRaised
import com.aeriotv.android.ui.theme.RoyalBlueSurface

/** Lets the full-width TV pane reveal the category list again. */
internal val LocalQueAppearanceExit = staticCompositionLocalOf<(() -> Unit)?> { null }

/** The reference's real, selectable palette previews, with no decorative fake options. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun QueAppearancePicker(
    currentTheme: AppTheme,
    appearanceMode: AppearanceMode,
    onThemeSelected: (AppTheme) -> Unit,
    onModeSelected: (AppearanceMode) -> Unit,
    onAllSettings: () -> Unit,
) {
    val ink = MaterialTheme.colorScheme.onBackground
    Column {
        Text("Settings", fontSize = 26.sp, fontWeight = FontWeight.SemiBold, color = ink)
        Spacer(Modifier.height(25.dp))
        Text("Appearance", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = ink)
        Spacer(Modifier.height(13.dp))
        Text("Light or dark", fontSize = 16.sp, color = ink)
        Spacer(Modifier.height(9.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(AppearanceMode.Light, AppearanceMode.Dark, AppearanceMode.System).forEach { mode ->
                QueAppearancePill(
                    label = when (mode) {
                        AppearanceMode.Light -> "Light"
                        AppearanceMode.Dark -> "Dark"
                        AppearanceMode.System -> "Follow system"
                    },
                    selected = mode == appearanceMode,
                    onClick = { onModeSelected(mode) },
                )
            }
        }
        Spacer(Modifier.height(17.dp))
        QueThemeGroup("Classic TV guide", listOf(AppTheme.RoyalBlue), currentTheme, onThemeSelected)
        Spacer(Modifier.height(14.dp))
        QueThemeGroup(
            "Fresh light blues",
            listOf(AppTheme.SkyBlue, AppTheme.PowderBlue, AppTheme.Arctic, AppTheme.Ocean),
            currentTheme,
            onThemeSelected,
            lightPreview = true,
        )
        Spacer(Modifier.height(14.dp))
        QueThemeGroup("Vivid blues", listOf(AppTheme.Aerio, AppTheme.Midnight), currentTheme, onThemeSelected)
        Spacer(Modifier.height(14.dp))
        QueThemeGroup(
            "More colours",
            listOf(AppTheme.Sunset, AppTheme.Forest, AppTheme.Lavender, AppTheme.Monochrome, AppTheme.Light),
            currentTheme,
            onThemeSelected,
        )
        Spacer(Modifier.height(20.dp))
        QueAppearancePill("All settings", selected = false, role = Role.Button, onClick = onAllSettings)
    }
}

@Composable
private fun QueAppearancePill(label: String, selected: Boolean, role: Role = Role.RadioButton, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val dark = LocalIsDarkTheme.current
    val shape = RoundedCornerShape(50)
    val background = when {
        focused -> if (dark) Color.White else MaterialTheme.colorScheme.primary
        selected -> if (dark) RoyalBlueSurface else MaterialTheme.colorScheme.primaryContainer
        else -> if (dark) RoyalBlueRaised else MaterialTheme.colorScheme.surfaceVariant
    }
    val foreground = when {
        focused && dark -> Color(0xFF0731CF)
        focused -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onBackground
    }
    Text(
        text = label,
        color = foreground,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .semantics { this.selected = selected }
            .onFocusChanged { focused = it.isFocused }
            .background(background, shape)
            .clickable(role = role, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QueThemeGroup(
    title: String,
    themes: List<AppTheme>,
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    lightPreview: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 15.sp)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            themes.forEach { theme ->
                QueThemePreview(theme, currentTheme == theme, lightPreview) { onThemeSelected(theme) }
            }
        }
    }
}

@Composable
private fun QueThemePreview(theme: AppTheme, selected: Boolean, light: Boolean, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val classic = theme == AppTheme.RoyalBlue
    val shape = RoundedCornerShape(12.dp)
    val background = if (light) theme.lightAppBackground else if (classic) RoyalBlueSurface else theme.appBackground
    val card = if (light) theme.lightCardBackground else if (classic) RoyalBlueRaised else theme.cardBackground
    val ink = if (light) Color(0xFF122B3A) else Color.White
    val accent = if (light) theme.lightAccentPrimary else if (classic) RoyalBlueFocus else theme.accentPrimary
    val accentInk = if (light || classic) Color.White else Color(0xFF10202A)
    Column(
        modifier = Modifier
            .width(146.dp)
            .height(104.dp)
            .semantics { this.selected = selected }
            .onFocusChanged { focused = it.isFocused }
            .background(background, shape)
            .border(
                width = if (selected || focused) 2.dp else 1.dp,
                color = when {
                    focused || selected -> if (LocalIsDarkTheme.current) Color.White else MaterialTheme.colorScheme.primary
                    light -> Color.White.copy(alpha = 0.35f)
                    else -> Color.Transparent
                },
                shape = shape,
            )
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(theme.displayName, color = ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), maxLines = 1)
            if (selected) {
                Icon(Icons.Default.Check, "Selected", tint = ink, modifier = Modifier.size(15.dp).background(ink.copy(alpha = 0.15f), CircleShape))
            }
        }
        Text(
            "Channel 101",
            color = ink,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth().background(card, RoundedCornerShape(4.dp)).padding(horizontal = 7.dp, vertical = 5.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Text("Focus", color = accentInk, fontSize = 11.sp, modifier = Modifier.background(accent, RoundedCornerShape(50)).padding(horizontal = 7.dp, vertical = 3.dp))
            Text("Chip", color = ink, fontSize = 11.sp, modifier = Modifier.background(card, RoundedCornerShape(50)).padding(horizontal = 7.dp, vertical = 3.dp))
        }
    }
}
