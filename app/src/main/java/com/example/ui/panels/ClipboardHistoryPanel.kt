package com.example.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.theme.NovaTheme
import com.example.data.db.ClipboardClip
import com.example.data.db.SmartClipboardManager
import kotlinx.coroutines.launch

@Composable
fun ClipboardHistoryPanel(
    clipboardManager: SmartClipboardManager,
    onClipSelected: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NovaTheme.colors
    val scope = rememberCoroutineScope()
    var searchKeyword by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val allClipsFlow = remember(searchKeyword) {
        if (searchKeyword.isBlank()) {
            clipboardManager.getAllClips()
        } else {
            clipboardManager.searchClips(searchKeyword)
        }
    }
    val clips by allClipsFlow.collectAsState(initial = emptyList())

    val filteredClips = remember(clips, selectedFilter) {
        when (selectedFilter) {
            "PINNED" -> clips.filter { it.isPinned }
            "LINK" -> clips.filter { it.category == ClipboardClip.CATEGORY_LINK }
            "CODE" -> clips.filter { it.category == ClipboardClip.CATEGORY_CODE }
            "NOTE" -> clips.filter { it.category == ClipboardClip.CATEGORY_NOTE }
            "SENSITIVE" -> clips.filter { it.isSensitive }
            else -> clips
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(colors.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.accentPrimary
                )
            }

            OutlinedTextField(
                value = searchKeyword,
                onValueChange = { searchKeyword = it },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                placeholder = { Text("بحث في الحافظة المشفرة...", color = colors.keyTextSecondary, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = colors.keyTextSecondary, modifier = Modifier.size(16.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accentPrimary,
                    unfocusedBorderColor = colors.keyBorder,
                    focusedTextColor = colors.keyText,
                    unfocusedTextColor = colors.keyText,
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface
                )
            )

            IconButton(
                onClick = {
                    scope.launch { clipboardManager.clearUnpinned() }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear unpinned",
                    tint = colors.accentSecondary
                )
            }
        }

        // Privacy banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(colors.surface)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = colors.accentPrimary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "حفظ محلي مشفر 100% • الحذف التلقائي للحساس بعد 15 دقيقة",
                color = colors.keyTextSecondary,
                fontSize = 10.sp
            )
        }

        // Filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(label = "الكل (${clips.size})", isSelected = selectedFilter == "ALL") { selectedFilter = "ALL" }
            FilterChip(label = "📌 المثبتة", isSelected = selectedFilter == "PINNED") { selectedFilter = "PINNED" }
            FilterChip(label = "🔗 روابط", isSelected = selectedFilter == "LINK") { selectedFilter = "LINK" }
            FilterChip(label = "💻 كود", isSelected = selectedFilter == "CODE") { selectedFilter = "CODE" }
            FilterChip(label = "📝 ملاحظات", isSelected = selectedFilter == "NOTE") { selectedFilter = "NOTE" }
            FilterChip(label = "🔒 سرية", isSelected = selectedFilter == "SENSITIVE") { selectedFilter = "SENSITIVE" }
        }

        // Clips List
        if (filteredClips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد عناصر محفوظة في الحافظة",
                    color = colors.keyTextSecondary,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredClips, key = { it.id }) { clip ->
                    ClipboardItemRow(
                        clip = clip,
                        onSelect = { onClipSelected(clip.text) },
                        onTogglePin = {
                            scope.launch { clipboardManager.togglePin(clip.id, clip.isPinned) }
                        },
                        onDelete = {
                            scope.launch { clipboardManager.deleteClip(clip.id) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = NovaTheme.colors
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) colors.accentPrimary.copy(alpha = 0.2f) else colors.surface)
            .border(
                1.dp,
                if (isSelected) colors.accentPrimary else colors.keyBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) colors.accentPrimary else colors.keyTextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun ClipboardItemRow(
    clip: ClipboardClip,
    onSelect: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = NovaTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.keyBackground)
            .border(
                1.dp,
                if (clip.isPinned) colors.accentSecondary else colors.keyBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onSelect)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category icon
        val icon = when (clip.category) {
            ClipboardClip.CATEGORY_LINK -> Icons.Default.Link
            ClipboardClip.CATEGORY_CODE -> Icons.Default.Code
            ClipboardClip.CATEGORY_NOTE -> Icons.Default.Notes
            ClipboardClip.CATEGORY_SENSITIVE -> Icons.Default.Lock
            else -> Icons.Default.Notes
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (clip.isSensitive) colors.accentSecondary else colors.accentPrimary,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (clip.isSensitive) "•••••••• (عنصر سري: OTP/كلمة مرور)" else clip.text,
                color = colors.keyText,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (clip.isSensitive && clip.expiresAt != null) {
                val remainingMins = ((clip.expiresAt - System.currentTimeMillis()) / 60000).coerceAtLeast(0)
                Text(
                    text = "حذف تلقائي بعد $remainingMins دقيقة",
                    color = colors.accentSecondary,
                    fontSize = 10.sp
                )
            }
        }

        IconButton(
            onClick = onTogglePin,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (clip.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                contentDescription = "Pin",
                tint = if (clip.isPinned) colors.accentSecondary else colors.keyTextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
