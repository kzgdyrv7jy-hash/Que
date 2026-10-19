package com.aeriotv.android.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberSmartRecord
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.ViewWeek
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeriotv.android.ui.theme.RoyalBlueRail
import com.aeriotv.android.ui.theme.RoyalBlueRaised
import com.aeriotv.android.ui.theme.RoyalBlueSurface
import com.aeriotv.android.ui.tv.TvGatedSpinner
import com.aeriotv.android.feature.livetv.LiveTVViewMode

internal val QueTvRailWidth = 104.dp

/** Visual rail only: the existing TV shell owns selection timing and launch focus. */
@Composable
internal fun QueTvRail(
    tabs: List<AppTab>,
    selected: AppTab,
    onSelect: (AppTab) -> Unit,
    focusRequester: FocusRequester,
    pillRequesters: Map<AppTab, FocusRequester>,
    tabEntryFocus: State<FocusRequester?>?,
    liveTvMode: LiveTVViewMode,
    liveTvListFocusRequester: FocusRequester?,
    guideFocusRequester: FocusRequester?,
    toolbarFocusRequester: FocusRequester?,
    onLiveTvModeSelect: (LiveTVViewMode) -> Unit,
    onLiveTvModeFocused: (LiveTVViewMode) -> Unit,
    onNavigationFocusChanged: (Boolean) -> Unit,
    onTabFocused: (AppTab) -> Unit,
) {
    val order = listOf(AppTab.LiveTV, AppTab.Favorites, AppTab.Movies, AppTab.TVShows,
        AppTab.OnDemand, AppTab.DVR, AppTab.Settings)
    val entries = tabs.sortedBy { order.indexOf(it) }.flatMap { tab ->
        if (tab == AppTab.LiveTV) listOf(QueRailEntry(tab, LiveTVViewMode.List), QueRailEntry(tab, LiveTVViewMode.Guide))
        else listOf(QueRailEntry(tab))
    }
    BoxWithConstraints(
        modifier = Modifier
            .width(QueTvRailWidth)
            .fillMaxHeight()
            .background(RoyalBlueRail),
    ) {
        // Keep every capability-filtered tab visible on both 720p and 1080p TVs.
        val tileHeight = ((maxHeight - 68.dp - 4.dp * (entries.size - 1).coerceAtLeast(0)) /
            entries.size.coerceAtLeast(1)).coerceIn(42.dp, 72.dp)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 14.dp, bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "AerioTV",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 18.dp),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .focusProperties {
                        onEnter = {
                            val entry = selected.takeIf { it in tabs } ?: tabs.firstOrNull()
                            entry?.let { pillRequesters[it]?.requestFocus() }
                        }
                        onExit = {
                            if (requestedFocusDirection == FocusDirection.Right ||
                                requestedFocusDirection == FocusDirection.Down
                            ) {
                                val target = tabEntryFocus?.value
                                if (target != null && runCatching { target.requestFocus() }.getOrDefault(false)) {
                                    cancelFocusChange()
                                }
                            }
                        }
                    }
                    .onFocusChanged { onNavigationFocusChanged(it.hasFocus) }
                    .focusGroup(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                entries.forEach { entry ->
                    val tab = entry.tab
                    val requester = when (entry.liveTvMode) {
                        LiveTVViewMode.List -> liveTvListFocusRequester
                        LiveTVViewMode.Guide -> guideFocusRequester
                        null -> pillRequesters[tab]
                    }
                    if (tab == AppTab.Settings && entries.size > 1) Spacer(Modifier.weight(1f))
                    QueTvRailTab(
                        entry = entry,
                        selected = tab == selected && (entry.liveTvMode == null || entry.liveTvMode == liveTvMode),
                        height = tileHeight,
                        onClick = {
                            entry.liveTvMode?.let(onLiveTvModeSelect)
                            onSelect(tab)
                        },
                        onFocused = {
                            entry.liveTvMode?.let(onLiveTvModeFocused)
                            onTabFocused(tab)
                        },
                        modifier = (requester?.let { Modifier.focusRequester(it) } ?: Modifier)
                            .focusProperties {
                                // The first tile's Up reaches the global action row.
                                if (entry == entries.firstOrNull()) {
                                    toolbarFocusRequester?.let { up = it }
                                }
                            },
                    )
                }
            }
        }
    }
}

@Composable
private fun QueTvRailTab(
    entry: QueRailEntry,
    selected: Boolean,
    height: Dp,
    onClick: () -> Unit,
    onFocused: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tab = entry.tab
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    // Change the fill and ink together so no transition frame can show white on white.
    val fill = when {
        focused -> Color.White
        selected -> RoyalBlueRaised
        else -> Color.Transparent
    }
    val ink = if (focused) RoyalBlueRail else Color.White
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
            .height(height)
            .onFocusChanged { if (it.isFocused) onFocused() }
            .clip(RoundedCornerShape(16.dp))
            .background(fill)
            .semantics { this.selected = selected }
            .clickable(interactionSource = interaction, indication = null, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected && !focused) {
            Box(
                Modifier.align(Alignment.CenterStart)
                    .width(4.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Icon(
                imageVector = if (entry.liveTvMode == LiveTVViewMode.Guide) Icons.Outlined.ViewWeek
                    else if (selected) tab.iconSelected else tab.iconUnselected,
                contentDescription = null,
                tint = ink,
                modifier = Modifier.size(if (height < 60.dp) 22.dp else 26.dp),
            )
            Text(
                text = if (entry.liveTvMode == LiveTVViewMode.Guide) "Guide" else queTabLabel(tab),
                color = ink,
                fontSize = if (height < 60.dp) 11.sp else 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

/** Global actions stay available above the page while the tab rail stays fixed. */
@Composable
internal fun QueTvToolbar(
    selected: AppTab,
    liveTvMode: LiveTVViewMode,
    onSearch: () -> Unit,
    searchFocusRequester: FocusRequester?,
    refreshFocusRequester: FocusRequester?,
    railFocusRequester: FocusRequester?,
    tabEntryFocus: State<FocusRequester?>,
    onRefresh: () -> Unit,
    refreshing: Boolean,
    retainedCount: Int,
    onRetainedClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isGuide = selected == AppTab.LiveTV && liveTvMode == LiveTVViewMode.Guide
    Row(
        modifier = modifier
            .height(52.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .focusProperties {
                onExit = {
                    if (requestedFocusDirection == FocusDirection.Down ||
                        requestedFocusDirection == FocusDirection.Left
                    ) {
                        val target = if (requestedFocusDirection == FocusDirection.Left) railFocusRequester
                            else tabEntryFocus.value
                        if (target != null && runCatching { target.requestFocus() }.getOrDefault(false)) {
                            cancelFocusChange()
                        }
                    }
                }
            }
            .focusGroup(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (isGuide) "TV Guide" else queTabLabel(selected),
            color = Color.White,
            fontSize = if (isGuide) 26.sp else 22.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
        if (isGuide) {
            Text("Today", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
        if (selected != AppTab.Settings) {
            if (isGuide) Spacer(Modifier.weight(1f))
            else {
                QueTvSearchAction(
                    selected = selected == AppTab.Search,
                    onClick = onSearch,
                    modifier = Modifier.weight(1f)
                        .then(searchFocusRequester?.let { Modifier.focusRequester(it) } ?: Modifier),
                )
            }
            QueTvToolbarAction(
                icon = Icons.Filled.Refresh,
                description = "Refresh channels and guide",
                onClick = onRefresh,
                spinning = refreshing,
                modifier = refreshFocusRequester?.let { Modifier.focusRequester(it) } ?: Modifier,
            )
            if (retainedCount > 0) {
                QueTvToolbarAction(
                    icon = Icons.Filled.FiberSmartRecord,
                    description = "$retainedCount channels kept live",
                    onClick = onRetainedClick,
                )
            }
        }
    }
}

@Composable
private fun QueTvSearchAction(selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val ink = if (focused) RoyalBlueRail else Color.White
    Row(
        modifier = modifier
            .height(36.dp)
            .clip(CircleShape)
            .background(if (focused) Color.White else RoyalBlueSurface)
            .border(1.dp, Color.White.copy(alpha = if (selected) 1f else 0.5f), CircleShape)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Icon(AppTab.Search.iconUnselected, contentDescription = null, tint = ink, modifier = Modifier.size(17.dp))
        Text(
            text = "Search channels, movies and shows",
            color = ink,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun QueTvToolbarAction(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    spinning: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val ink = if (focused) RoyalBlueRail else Color.White
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (focused) Color.White else Color.Transparent)
            .clickable(interactionSource = interaction, indication = null, enabled = !spinning, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (spinning) TvGatedSpinner(color = ink, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
        else Icon(icon, contentDescription = description, tint = ink, modifier = Modifier.size(22.dp))
    }
}

private fun queTabLabel(tab: AppTab): String = if (tab == AppTab.DVR) "Recordings" else tab.label

private data class QueRailEntry(val tab: AppTab, val liveTvMode: LiveTVViewMode? = null)
