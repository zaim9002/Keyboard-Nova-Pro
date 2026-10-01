package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.local.entity.ClipboardEntity
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.util.HapticHelper
import java.text.SimpleDateFormat
import java.util.*

private var lastSavedClipScrollIndex = 0
private var lastSavedClipScrollOffset = 0

@Composable
fun ClipboardPanel(
    modifier: Modifier = Modifier,
    clips: List<ClipboardEntity>,
    colorScheme: KeyboardColorScheme,
    onClipClick: (String) -> Unit,
    onTogglePin: (Long, Boolean) -> Unit,
    onDeleteClip: (Long) -> Unit,
    onClearUnpinned: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFolder by remember { mutableStateOf("الكل") }
    val folders = listOf("الكل", "عام", "مهم", "عمل", "روابط", "أكواد")

    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = lastSavedClipScrollIndex.coerceIn(0, (clips.size - 1).coerceAtLeast(0)),
        initialFirstVisibleItemScrollOffset = lastSavedClipScrollOffset
    )

    DisposableEffect(gridState) {
        onDispose {
            lastSavedClipScrollIndex = gridState.firstVisibleItemIndex
            lastSavedClipScrollOffset = gridState.firstVisibleItemScrollOffset
        }
    }

    val filteredClips = remember(clips, searchQuery, selectedFolder) {
        clips.filter { clip ->
            val matchesSearch = searchQuery.isEmpty() || clip.text.contains(searchQuery, ignoreCase = true)
            val matchesFolder = selectedFolder == "الكل" || clip.folder == selectedFolder
            matchesSearch && matchesFolder
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(colorScheme.background)
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            HapticHelper.performKeyHaptic(context, view)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "إعدادات الحافظة",
                            tint = colorScheme.keyText.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            HapticHelper.performKeyHaptic(context, view)
                            onClearUnpinned()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "مسح غير المثبت",
                            tint = colorScheme.keyText.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "الحافظة (${filteredClips.size})",
                        color = colorScheme.accent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = {
                        HapticHelper.performKeyHaptic(context, view)
                        onClose()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = colorScheme.keyText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorScheme.keyBackground)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "بحث في الحافظة...",
                            color = colorScheme.keyText.copy(alpha = 0.5f),
                            fontSize = 11.sp
                        )
                    }
                    androidx.compose.foundation.text.BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = colorScheme.keyText,
                            fontSize = 12.sp
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (folder in folders.take(3)) {
                        val isSelected = selectedFolder == folder
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) colorScheme.accent else colorScheme.keyBackground.copy(alpha = 0.7f))
                                .clickable {
                                    HapticHelper.performKeyHaptic(context, view)
                                    selectedFolder = folder
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = folder,
                                color = if (isSelected) Color.White else colorScheme.keyText,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            if (filteredClips.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "لا توجد نتائج مطابقة" else "الحافظة فارغة",
                        color = colorScheme.keyText.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                }
            } else {
                val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = gridState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredClips, key = { it.id }) { clip ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = colorScheme.keyBackground),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    HapticHelper.performKeyHaptic(context, view)
                                    onClipClick(clip.text)
                                    onClose()
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = clip.text,
                                    color = colorScheme.keyText,
                                    fontSize = 12.sp,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = dateFormat.format(Date(clip.timestamp)),
                                        color = colorScheme.keyText.copy(alpha = 0.45f),
                                        fontSize = 10.sp
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                HapticHelper.performKeyHaptic(context, view)
                                                onTogglePin(clip.id, !clip.isPinned)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PushPin,
                                                contentDescription = if (clip.isPinned) "إلغاء التثبيت" else "تثبيت",
                                                tint = if (clip.isPinned) colorScheme.accent else colorScheme.keyText.copy(alpha = 0.35f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                HapticHelper.performKeyHaptic(context, view)
                                                onDeleteClip(clip.id)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "حذف",
                                                tint = colorScheme.keyText.copy(alpha = 0.35f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
