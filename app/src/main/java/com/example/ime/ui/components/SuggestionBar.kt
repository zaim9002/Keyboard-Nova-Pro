package com.example.ime.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SuggestionBar(
    modifier: Modifier = Modifier,
    suggestions: List<String>,
    latestClip: String? = null,
    currentTypedWord: String? = null,
    isCurrentWordKnown: Boolean = true,
    colorScheme: KeyboardColorScheme,
    onSelectSuggestion: (String) -> Unit,
    onPasteClip: (String) -> Unit = {},
    onAddWordToDictionary: (String) -> Unit = {}
) {
    if (suggestions.isEmpty() && latestClip.isNullOrBlank() && (isCurrentWordKnown || currentTypedWord.isNullOrBlank())) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(38.dp)
                .background(colorScheme.suggestionBar)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("،", "؟", "!", ".", "...", "@", "#").forEach { punct ->
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onSelectSuggestion(punct) }
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = punct,
                        color = colorScheme.suggestionText.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        return
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(suggestions) {
        if (suggestions.isNotEmpty() && listState.firstVisibleItemIndex != 0) {
            listState.scrollToItem(0)
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(colorScheme.suggestionBar)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 1. Show Quick Save Chip if user typed an unknown/new custom word
        if (!isCurrentWordKnown && !currentTypedWord.isNullOrBlank() && currentTypedWord.length >= 2) {
            item(key = "save_custom_word") {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(colorScheme.accent.copy(alpha = 0.25f))
                        .border(0.5.dp, colorScheme.accent, RoundedCornerShape(6.dp))
                        .clickable { onAddWordToDictionary(currentTypedWord) }
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ حفظ '$currentTypedWord'",
                        color = colorScheme.accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // 2. Show Latest Clipboard item as first chip if available
        if (!latestClip.isNullOrBlank()) {
            item(key = "clipboard_chip") {
                val clipPreview = latestClip.replace("\n", " ").trim()
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(min = 90.dp, max = 160.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(colorScheme.accent.copy(alpha = 0.22f))
                        .border(0.5.dp, colorScheme.accent.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .clickable { onPasteClip(latestClip) }
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📋 $clipPreview",
                        color = colorScheme.accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 3. Multi-item suggestions horizontally scrollable and selectable
        itemsIndexed(
            items = suggestions,
            key = { index, item -> "${index}_$item" }
        ) { index, suggestion ->
            val isPrimary = index == 0 && latestClip.isNullOrBlank()
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(min = 68.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isPrimary) colorScheme.accent.copy(alpha = 0.2f)
                        else colorScheme.keyBackground.copy(alpha = 0.65f)
                    )
                    .border(
                        0.5.dp,
                        if (isPrimary) colorScheme.accent.copy(alpha = 0.8f) else colorScheme.borderColor,
                        RoundedCornerShape(6.dp)
                    )
                    .combinedClickable(
                        onClick = {
                            coroutineScope.launch {
                                val targetIndex = (index + 1).coerceAtMost(suggestions.size - 1)
                                listState.animateScrollToItem(targetIndex)
                            }
                            onSelectSuggestion(suggestion)
                        },
                        onLongClick = { onAddWordToDictionary(suggestion) }
                    )
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = suggestion,
                    color = if (isPrimary) colorScheme.accent else colorScheme.suggestionText,
                    fontSize = 13.sp,
                    fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
