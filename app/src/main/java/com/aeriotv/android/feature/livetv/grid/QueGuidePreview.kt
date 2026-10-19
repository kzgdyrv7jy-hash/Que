package com.aeriotv.android.feature.livetv.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeriotv.android.core.data.EPGProgramme
import com.aeriotv.android.core.data.M3UChannel
import com.aeriotv.android.core.ui.ClockFormat
import com.aeriotv.android.core.ui.rememberClockMode
import com.aeriotv.android.feature.player.MiniPlayerChrome
import java.util.Date

internal val QueGuidePreviewHeight = 132.dp

/** Text-only program hero from the Que reference; the shared cobalt backdrop shows through. */
@Composable
internal fun QueGuidePreview(
    program: EPGProgramme?,
    channel: M3UChannel?,
    nowMs: Long,
    onOpenInfo: () -> Unit,
    descriptionFocus: FocusRequester,
    downTarget: FocusRequester?,
    upTarget: FocusRequester?,
    onDescriptionFocusChanged: (Boolean) -> Unit,
    onDown: (() -> Boolean)?,
    miniActive: Boolean,
    modifier: Modifier = Modifier,
) {
    val clockMode = rememberClockMode()
    val formatter = remember(clockMode) { ClockFormat.guideShort(clockMode) }
    DisposableEffect(Unit) {
        onDispose { MiniPlayerChrome.bannerArtBottomPx.value = 0f }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(QueGuidePreviewHeight)
            .onGloballyPositioned { MiniPlayerChrome.bannerArtBottomPx.value = it.boundsInRoot().bottom }
            .padding(start = 20.dp, top = 4.dp, end = if (miniActive) 231.dp else 20.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = channel?.name.orEmpty(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = program?.title ?: "Select a program",
            color = Color.White,
            fontSize = 26.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (program != null && !program.isPlaceholder) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val duration = ((program.endMillis - program.startMillis) / 60_000L).coerceAtLeast(1L)
                val durationLabel = when {
                    duration < 60 -> "${duration}m"
                    duration % 60 == 0L -> "${duration / 60}h"
                    else -> "${duration / 60}h ${duration % 60}m"
                }
                val episodeLabel = listOfNotNull(
                    program.season?.let { "S${it.toString().padStart(2, '0')}" },
                    program.episode?.let { "E${it.toString().padStart(2, '0')}" },
                ).joinToString(" ")
                QueGuideMeta(durationLabel)
                if (episodeLabel.isNotEmpty()) QueGuideMeta(episodeLabel)
                val start = formatter.format(Date(program.startMillis))
                QueGuideMeta(if (program.startMillis <= nowMs) "Started at $start" else "Starts at $start")
            }
        }
        if (program?.description?.isNotBlank() == true) {
            val interaction = remember { MutableInteractionSource() }
            var focused by remember { mutableStateOf(false) }
            Text(
                text = program.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp,
                lineHeight = 21.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .offset(x = (-6).dp)
                    .focusRequester(descriptionFocus)
                    .focusProperties {
                        if (downTarget != null) down = downTarget
                        if (upTarget != null) up = upTarget
                    }
                    .onPreviewKeyEvent { event ->
                        onDown != null && event.type == KeyEventType.KeyDown &&
                            event.key == Key.DirectionDown && onDown()
                    }
                    .onFocusChanged {
                        if (it.isFocused && !focused) com.aeriotv.android.ui.tv.TvFocusTrace.focus("banner:description")
                        else if (!it.isFocused && focused) com.aeriotv.android.ui.tv.TvFocusTrace.blurred("banner:description")
                        focused = it.isFocused
                        onDescriptionFocusChanged(it.isFocused)
                    }
                    .clip(RoundedCornerShape(5.dp))
                    .background(if (focused) com.aeriotv.android.ui.theme.RoyalBlueFocus else Color.Transparent)
                    .clickable(interactionSource = interaction, indication = null, onClick = onOpenInfo)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
    }
}

@Composable
private fun QueGuideMeta(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
