package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewSidebar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.layout.KeyboardLayoutType
import com.example.core.theme.KeyboardMode
import com.example.core.theme.NovaTheme

@Composable
fun UtilityToolbar(
    currentLayout: KeyboardLayoutType,
    currentMode: KeyboardMode,
    onEmojiClick: () -> Unit,
    onClipboardClick: () -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
    onThemeCycle: () -> Unit,
    onLanguageToggle: () -> Unit,
    onVoiceClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NovaTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(colors.toolbarBackground)
            .padding(horizontal = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolbarIconButton(
                icon = Icons.Default.Mood,
                contentDescription = "Emoji & Stickers",
                tint = colors.accentPrimary,
                onClick = onEmojiClick
            )

            ToolbarIconButton(
                icon = Icons.Default.Assignment,
                contentDescription = "Smart Clipboard",
                tint = colors.accentSecondary,
                onClick = onClipboardClick
            )

            ToolbarIconButton(
                icon = Icons.Default.Language,
                contentDescription = "Language",
                tint = colors.keyText,
                onClick = onLanguageToggle
            )

            // Layout Mode Switcher Icon (cycles or toggles)
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.keyBackground)
                    .clickable {
                        val nextMode = when (currentMode) {
                            KeyboardMode.FULL -> KeyboardMode.ONE_HANDED
                            KeyboardMode.ONE_HANDED -> KeyboardMode.FLOATING
                            KeyboardMode.FLOATING -> KeyboardMode.SPLIT
                            KeyboardMode.SPLIT -> KeyboardMode.FULL
                        }
                        onModeChange(nextMode)
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (currentMode) {
                        KeyboardMode.FULL -> "FULL"
                        KeyboardMode.ONE_HANDED -> "1-HAND"
                        KeyboardMode.FLOATING -> "FLOAT"
                        KeyboardMode.SPLIT -> "SPLIT"
                    },
                    color = colors.accentPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            ToolbarIconButton(
                icon = Icons.Default.ColorLens,
                contentDescription = "Cycle Themes",
                tint = colors.accentPrimary,
                onClick = onThemeCycle
            )

            ToolbarIconButton(
                icon = Icons.Default.Mic,
                contentDescription = "Voice Input",
                tint = colors.keyTextSecondary,
                onClick = onVoiceClick
            )
        }

        ToolbarIconButton(
            icon = Icons.Default.KeyboardHide,
            contentDescription = "Hide Keyboard",
            tint = colors.keyTextSecondary,
            onClick = onDismissRequest
        )
    }
}

@Composable
private fun ToolbarIconButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(34.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(19.dp)
        )
    }
}
