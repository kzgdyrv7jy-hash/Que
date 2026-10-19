package com.aeriotv.android.feature.movies.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeriotv.android.core.tv.TvActionMenuDialog
import com.aeriotv.android.core.tv.TvMenuAction
import com.aeriotv.android.core.tv.rememberTvMenuGuard
import com.aeriotv.android.feature.main.LocalTabIsActive
import com.aeriotv.android.feature.main.LocalTvChromeCollapsed
import com.aeriotv.android.feature.main.LocalTvChromeScroll
import com.aeriotv.android.feature.main.LocalTvRequestCurrentTabPill
import com.aeriotv.android.feature.main.LocalTvTabEntryFocus
import com.aeriotv.android.feature.main.LocalTvTopNavFocusRequester
import com.aeriotv.android.ui.theme.RoyalBlueFocus
import com.aeriotv.android.ui.theme.RoyalBlueRaised
import com.aeriotv.android.ui.theme.RoyalBlueSurface
import com.aeriotv.android.ui.tv.TvKeyboardOnOkHost
import com.aeriotv.android.ui.tv.tvFormFieldInput
import kotlinx.coroutines.launch

/** Que follows the reference's compact library layout; other themes keep the TV carousel layout. */
@Composable
internal fun <T> QueTvMediaPage(
    gridState: LazyGridState,
    heroPages: List<TvHeroPage>,
    shelves: List<TvShelf<*>>,
    headerTitle: String,
    headerCount: Int,
    sortActions: List<TvMenuAction>,
    onRefresh: (() -> Unit)?,
    onFilter: (() -> Unit)?,
    filterActive: Boolean,
    searchEnabled: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    searchPlaceholder: String,
    isSearching: Boolean,
    searchExtras: List<@Composable () -> Unit>,
    pills: List<String>,
    selectedPill: String?,
    onPill: (String?) -> Unit,
    gridItems: List<T>,
    gridKey: (T) -> Any,
    cell: @Composable (T, TvCellScope) -> Unit,
    emptyContent: @Composable () -> Unit,
    returnKey: Any?,
    returnOffset: Pair<Int, Int>?,
    returnSource: TvReturnSource?,
    indexOfKey: ((Any) -> Int)?,
    onReturnHandled: () -> Unit,
    pageId: String,
    heroSectionTitle: String?,
    footer: (@Composable () -> Unit)?,
) {
    val recordings = pageId == "dvr"
    val columns = if (recordings) 3 else 5
    val scope = rememberCoroutineScope()
    val active = LocalTabIsActive.current
    val tabEntry = LocalTvTabEntryFocus.current
    val topNav = LocalTvTopNavFocusRequester.current
    val requestTab = LocalTvRequestCurrentTabPill.current
    val chromeCollapsed = LocalTvChromeCollapsed.current
    val chromeScroll = LocalTvChromeScroll.current
    val entry = remember { FocusRequester() }
    val firstPill = remember { FocusRequester() }
    val cellRequesters = remember { HashMap<Any, FocusRequester>() }
    val auxiliaryRequesters = remember { HashMap<String, FocusRequester>() }
    val guard = rememberTvMenuGuard()
    var optionsOpen by remember { mutableStateOf(false) }
    var sortOpen by remember { mutableStateOf(false) }
    var pageHasFocus by remember { mutableStateOf(false) }
    var scheduledMode by rememberSaveable { mutableStateOf(false) }
    var recordingSearchOpen by rememberSaveable { mutableStateOf(false) }
    var returnHandled by remember { mutableStateOf(false) }
    val showSearch = searchEnabled && (!recordings || recordingSearchOpen || isSearching)
    val scheduled = shelves.firstOrNull { it.title == "Scheduled" }
    val visibleShelves = shelves.filter { it.items.isNotEmpty() && (!recordings || it.title != "Scheduled") }
    @Suppress("UNCHECKED_CAST")
    val scheduledShelf = scheduled as? TvShelf<T>
    val shownItems = if (recordings && scheduledMode) scheduledShelf?.items.orEmpty() else gridItems
    val shownKey: (T) -> Any = if (recordings && scheduledMode && scheduledShelf != null) scheduledShelf.key else gridKey
    val extraCount = if (isSearching) searchExtras.size else 0
    val leadingCount = 1 + extraCount
    val continueIndex = leadingCount + shownItems.size
    val shelfStartIndex = continueIndex + if (heroPages.isNotEmpty()) 1 else 0
    val title = when {
        isSearching -> "Results"
        recordings -> "Recordings"
        else -> headerTitle.removePrefix("All ")
    }

    fun railFocus() {
        if (requestTab?.invoke() != true) runCatching { topNav?.requestFocus() }
    }

    DisposableEffect(active, entry) {
        if (active) {
            tabEntry.value = entry
            chromeCollapsed?.value = false
            chromeScroll?.value = 0
        }
        onDispose { if (tabEntry.value === entry) tabEntry.value = null }
    }

    // Save and restore the actual activated item, including the compact continuation shelves.
    LaunchedEffect(returnKey, returnSource, shownItems.size, heroPages.map { it.key }, visibleShelves.map { it.items.size }, active) {
        if (!active || returnHandled || (returnKey == null && returnSource == null)) return@LaunchedEffect
        val index = returnKey?.let { key ->
            if (!scheduledMode && indexOfKey != null) indexOfKey(key)
            else shownItems.indexOfFirst { shownKey(it) == key }
        } ?: -1
        val source = returnSource
        val auxiliaryKey: String?
        val targetIndex: Int
        when {
            source is TvReturnSource.Hero && heroPages.isNotEmpty() -> {
                targetIndex = continueIndex
                auxiliaryKey = "hero:${source.pageKey}"
            }
            source is TvReturnSource.Shelf && source.shelf in visibleShelves.indices -> {
                targetIndex = shelfStartIndex + source.shelf
                auxiliaryKey = "shelf:${source.shelf}:${source.index}"
            }
            index in shownItems.indices -> {
                targetIndex = leadingCount + index
                auxiliaryKey = null
            }
            else -> return@LaunchedEffect
        }
        val exact = returnOffset?.takeIf { auxiliaryKey == null && it.first in 0 until gridState.layoutInfo.totalItemsCount }
        gridState.scrollToItem(exact?.first ?: targetIndex, exact?.second ?: 0)
        repeat(12) {
            withFrameNanos { }
            val requester = if (auxiliaryKey != null) auxiliaryRequesters[auxiliaryKey] else returnKey?.let { cellRequesters[it] }
            if (runCatching { requester?.requestFocus() }.getOrNull() == true) {
                returnHandled = true
                onReturnHandled()
                return@LaunchedEffect
            }
        }
    }

    BackHandler(enabled = active && pageHasFocus && !optionsOpen && !sortOpen) {
        when {
            query.isNotBlank() -> onClearSearch()
            recordings && recordingSearchOpen -> {
                recordingSearchOpen = false
                scope.launch { withFrameNanos { }; runCatching { entry.requestFocus() } }
            }
            gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0 -> scope.launch {
                gridState.animateScrollToItem(0)
                runCatching { entry.requestFocus() }
            }
            else -> railFocus()
        }
    }

    val options = buildList {
        if (recordings && searchEnabled) add(TvMenuAction(if (showSearch) "Close search" else "Search recordings") {
            recordingSearchOpen = !showSearch
            if (!recordingSearchOpen) onClearSearch()
            scope.launch {
                gridState.scrollToItem(0)
                withFrameNanos { }
                runCatching { entry.requestFocus() }
            }
        })
        if (sortActions.isNotEmpty()) add(TvMenuAction("Sort") { sortOpen = true })
        if (onFilter != null) add(TvMenuAction(if (filterActive) "Filter \u2713" else "Filter") { onFilter() })
        if (recordings && pills.isNotEmpty()) {
            add(TvMenuAction(if (selectedPill == null) "All recordings \u2713" else "All recordings") { onPill(null) })
            pills.forEach { p -> add(TvMenuAction(if (selectedPill == p) "$p \u2713" else p) { onPill(p) }) }
        }
        if (heroPages.isNotEmpty()) add(TvMenuAction(heroSectionTitle ?: "Continue Watching") {
            scope.launch {
                gridState.scrollToItem(continueIndex)
                withFrameNanos { }
                auxiliaryRequesters["hero:${heroPages.first().key}"]?.let { runCatching { it.requestFocus() } }
            }
        })
        visibleShelves.forEachIndexed { i, shelf -> add(TvMenuAction(shelf.title) {
            scope.launch {
                gridState.scrollToItem(shelfStartIndex + i)
                withFrameNanos { }
                auxiliaryRequesters["shelf:$i:0"]?.let { runCatching { it.requestFocus() } }
            }
        }) }
    }

    TvKeyboardOnOkHost {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns), state = gridState,
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().onFocusChanged { pageHasFocus = it.hasFocus },
        ) {
            item(key = "que-header", span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val titleInteraction = remember { MutableInteractionSource() }
                        val titleFocused by titleInteraction.collectIsFocusedAsState()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .then(if (!showSearch) Modifier.focusRequester(entry) else Modifier)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (titleFocused) RoyalBlueFocus else Color.Transparent)
                                .border(2.dp, if (titleFocused) Color.White else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable(interactionSource = titleInteraction, indication = null, enabled = options.isNotEmpty()) { optionsOpen = true }
                                .semantics { contentDescription = "$title, library options" }
                                .padding(vertical = 8.dp),
                        ) {
                            Text(title, color = Color.White, fontSize = 26.sp, lineHeight = 30.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            if (!recordings) Text(headerCount.toString(), color = Color(0xFFDCE9FF), fontSize = 16.sp, maxLines = 1)
                        }
                        if (showSearch) QueMediaSearch(
                            query = query, onQueryChange = onQueryChange, placeholder = searchPlaceholder,
                            modifier = Modifier.weight(1f).focusRequester(entry).onPreviewKeyEvent {
                                if (it.type == KeyEventType.KeyDown && it.key == Key.DirectionUp) { railFocus(); true } else false
                            },
                        )
                        if (recordings && !showSearch) Spacer(Modifier.weight(1f))
                        if (onRefresh != null) {
                            val refreshInteraction = remember { MutableInteractionSource() }
                            val refreshFocused by refreshInteraction.collectIsFocusedAsState()
                            Icon(
                                Icons.Outlined.Refresh, contentDescription = "Refresh recordings", tint = Color.White,
                                modifier = Modifier.size(42.dp).clip(CircleShape)
                                    .background(if (refreshFocused) RoyalBlueFocus else Color.Transparent)
                                    .border(2.dp, if (refreshFocused) Color.White else Color.Transparent, CircleShape)
                                    .clickable(interactionSource = refreshInteraction, indication = null, onClick = onRefresh)
                                    .padding(7.dp),
                            )
                        }
                    }
                    if (recordings) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            QueMediaPill("Recorded ($headerCount)", !scheduledMode, modifier = Modifier.focusRequester(firstPill)) {
                                scheduledMode = false
                                scope.launch { gridState.scrollToItem(0) }
                            }
                            QueMediaPill("Scheduled (${scheduled?.items?.size ?: 0})", scheduledMode) {
                                scheduledMode = true
                                scope.launch { gridState.scrollToItem(0) }
                            }
                        }
                    } else if (pills.isNotEmpty() && !isSearching) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 2.dp)) {
                            item(key = "all") { QueMediaPill("All", selectedPill == null, modifier = Modifier.focusRequester(firstPill)) { onPill(null) } }
                            items(pills.size, key = { pills[it] }) { i ->
                                val pill = pills[i]
                                QueMediaPill(pill, selectedPill == pill) { onPill(if (selectedPill == pill) null else pill) }
                            }
                        }
                    }
                }
            }
            if (isSearching) searchExtras.forEachIndexed { i, extra ->
                item(key = "que-extra:$i", span = { GridItemSpan(maxLineSpan) }) { extra() }
            }
            if (shownItems.isEmpty()) item(key = "que-empty", span = { GridItemSpan(maxLineSpan) }) {
                Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) { emptyContent() }
            }
            itemsIndexed(shownItems, key = { _, item -> shownKey(item) }) { index, item ->
                val key = shownKey(item)
                val requester = remember(key) { cellRequesters.getOrPut(key) { FocusRequester() } }
                val modifier = Modifier.focusRequester(requester)
                    .onFocusChanged { if (it.isFocused) TvReturnMemory.focusSource[pageId] = TvReturnSource.Grid(index / columns, index % columns) }
                    .onPreviewKeyEvent {
                        if (it.type != KeyEventType.KeyDown) false
                        else when {
                            it.key == Key.DirectionLeft && index % columns == 0 -> { railFocus(); true }
                            it.key == Key.DirectionUp && index < columns -> {
                                scope.launch {
                                    gridState.animateScrollToItem(0)
                                    withFrameNanos { }
                                    val destination = if (recordings || (pills.isNotEmpty() && !isSearching)) firstPill else entry
                                    runCatching { destination.requestFocus() }
                                }
                                true
                            }
                            else -> false
                        }
                    }
                if (recordings && scheduledMode && scheduledShelf != null) scheduledShelf.card(item, modifier)
                else cell(item, TvCellScope(modifier, requester))
            }
            if (heroPages.isNotEmpty()) item(key = "que-continue", span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(heroSectionTitle ?: "Continue Watching", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 4.dp)) {
                        items(heroPages.size, key = { heroPages[it].key }) { i ->
                            val page = heroPages[i]
                            val requester = remember(page.key) { auxiliaryRequesters.getOrPut("hero:${page.key}") { FocusRequester() } }
                            TvRecordingCard(
                                title = page.title, meta = page.meta.joinToString(" · "), artUrl = page.artUrl,
                                logoUrl = null, channelName = "", trailing = page.rating.orEmpty(), progress = 0f, badge = null,
                                onClick = { page.buttons.firstOrNull()?.onClick?.invoke() },
                                longPressActions = page.buttons.map { button -> TvMenuAction(button.label, icon = button.icon, onClick = button.onClick) } + page.longPressActions,
                                modifier = Modifier.width(274.dp).focusRequester(requester).onFocusChanged {
                                    if (it.isFocused) TvReturnMemory.focusSource[pageId] = TvReturnSource.Hero(page.key, page.buttons.firstOrNull()?.id ?: "Primary")
                                },
                            )
                        }
                    }
                }
            }
            visibleShelves.forEachIndexed { shelfIndex, shelf -> item(key = "que-shelf:${shelf.title}", span = { GridItemSpan(maxLineSpan) }) {
                QueMediaShelf(shelf, shelfIndex, recordings, auxiliaryRequesters, pageId)
            } }
            if (footer != null) item(key = "que-footer", span = { GridItemSpan(maxLineSpan) }) { footer() }
        }
    }
    if (optionsOpen) TvActionMenuDialog(title = title, actions = options, guard = guard, onDismiss = { optionsOpen = false })
    if (sortOpen) TvActionMenuDialog(title = "Sort", actions = sortActions, guard = guard, onDismiss = { sortOpen = false })
}

@Composable
private fun QueMediaSearch(query: String, onQueryChange: (String) -> Unit, placeholder: String, modifier: Modifier) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val keyboard = LocalSoftwareKeyboardController.current
    val focus = LocalFocusManager.current
    BasicTextField(
        value = query, onValueChange = onQueryChange, singleLine = true, interactionSource = interaction,
        textStyle = TextStyle(color = Color.White, fontSize = 16.sp), cursorBrush = SolidColor(Color.White),
        keyboardOptions = com.aeriotv.android.ui.textfield.aerioTextFieldKeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboard?.hide(); focus.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }),
        modifier = modifier.height(56.dp).clip(CircleShape)
            .background(if (focused) RoyalBlueFocus else RoyalBlueSurface)
            .border(if (focused) 3.dp else 1.dp, if (focused) Color.White else Color(0xFF91B3FF), CircleShape)
            .tvFormFieldInput(),
        decorationBox = { inner ->
            Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFFDCE9FF), modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) Text(placeholder, color = Color(0xFFDCE9FF), fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    inner()
                }
            }
        },
    )
}

@Composable
private fun QueMediaPill(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    Text(
        label, color = Color.White, fontSize = 15.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium, maxLines = 1,
        modifier = modifier.clip(CircleShape).background(when { focused -> RoyalBlueFocus; selected -> RoyalBlueRaised; else -> RoyalBlueSurface })
            .border(2.dp, if (focused) Color.White else Color.Transparent, CircleShape)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 11.dp),
    )
}

@Composable
private fun <T> QueMediaShelf(
    shelf: TvShelf<T>, shelfIndex: Int, recordings: Boolean,
    requesters: HashMap<String, FocusRequester>, pageId: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(shelf.title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 4.dp)) {
            items(shelf.items.size, key = { shelf.key(shelf.items[it]) }) { i ->
                val requester = remember(shelfIndex, i) { requesters.getOrPut("shelf:$shelfIndex:$i") { FocusRequester() } }
                shelf.card(shelf.items[i], Modifier.width(if (recordings) 274.dp else 156.dp).focusRequester(requester).onFocusChanged {
                    if (it.isFocused) TvReturnMemory.focusSource[pageId] = TvReturnSource.Shelf(shelfIndex, i)
                })
            }
        }
    }
}
