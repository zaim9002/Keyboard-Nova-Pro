package com.example.ime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.util.HapticHelper

enum class KeyboardPanel {
    NONE,
    MENU,
    THEMES,
    TOOLBAR_EDITOR,
    CLIPBOARD,
    EMOJI,
    STICKERS,
    GIFS,
    DECORATIONS,
    VOICE,
    TRANSLATE,
    DICTIONARY,
    EDITING,
    RESIZE,
    AI_ASSISTANT,
    GAME,
    CALCULATOR,
    NOTES,
    HANDWRITING,
    INSTA_FONTS,
    NEWS
}

@Composable
fun KeyboardToolbar(
    modifier: Modifier = Modifier,
    activePanel: KeyboardPanel,
    currentLanguage: String,
    isIncognito: Boolean,
    autoTranslateOnEnter: Boolean = false,
    oneHandedMode: String = "OFF",
    showTermuxKeys: Boolean = false,
    showQuickSnippets: Boolean = false,
    showUndoRedo: Boolean = true,
    colorScheme: KeyboardColorScheme,
    onPanelSelect: (KeyboardPanel) -> Unit,
    onSwitchLanguage: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenThemes: () -> Unit = {},
    onUndo: (() -> Unit)? = null,
    onRedo: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    onToggleOneHanded: ((String) -> Unit)? = null,
    onToggleTermuxKeys: (() -> Unit)? = null,
    onToggleQuickSnippets: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val view = LocalView.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        LazyRow(
            modifier = modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(colorScheme.background),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 1. Primary Feature Menu Button (قائمة ميزات كيبورد محمد v.1)
            item(key = "menu") {
                ToolbarIconButton(
                    icon = Icons.Default.MoreHoriz,
                    tooltip = "قائمة ميزات كيبورد محمد v.1",
                    isSelected = activePanel == KeyboardPanel.MENU,
                    colorScheme = colorScheme,
                    onClick = {
                        onPanelSelect(if (activePanel == KeyboardPanel.MENU) KeyboardPanel.NONE else KeyboardPanel.MENU)
                    }
                )
            }

            // 2. Voice Input
            item(key = "voice") {
                ToolbarIconButton(
                    icon = Icons.Default.Mic,
                    tooltip = "الكتابة بالصوت",
                    isSelected = activePanel == KeyboardPanel.VOICE,
                    colorScheme = colorScheme,
                    onClick = {
                        onPanelSelect(if (activePanel == KeyboardPanel.VOICE) KeyboardPanel.NONE else KeyboardPanel.VOICE)
                    }
                )
            }

            // 3. Theme Selector
            item(key = "theme") {
                ToolbarIconButtonWithBadge(
                    icon = Icons.Default.Checkroom,
                    tooltip = "المظاهر والثيمات",
                    hasBadge = false,
                    isSelected = activePanel == KeyboardPanel.THEMES,
                    colorScheme = colorScheme,
                    onClick = {
                        onPanelSelect(if (activePanel == KeyboardPanel.THEMES) KeyboardPanel.NONE else KeyboardPanel.THEMES)
                    }
                )
            }

            // 4. Translation
            item(key = "translate") {
                ToolbarIconButton(
                    icon = Icons.Default.Translate,
                    tooltip = "الترجمة الفورية",
                    isSelected = activePanel == KeyboardPanel.TRANSLATE,
                    colorScheme = colorScheme,
                    onClick = {
                        onPanelSelect(if (activePanel == KeyboardPanel.TRANSLATE) KeyboardPanel.NONE else KeyboardPanel.TRANSLATE)
                    }
                )
            }

            // 5. Smart Clipboard
            item(key = "clipboard") {
                ToolbarIconButton(
                    icon = Icons.Default.Assignment,
                    tooltip = "الحافظة المشفرة",
                    isSelected = activePanel == KeyboardPanel.CLIPBOARD,
                    colorScheme = colorScheme,
                    onClick = {
                        onPanelSelect(if (activePanel == KeyboardPanel.CLIPBOARD) KeyboardPanel.NONE else KeyboardPanel.CLIPBOARD)
                    }
                )
            }

            // 6. Settings Gear
            item(key = "settings") {
                ToolbarIconButton(
                    icon = Icons.Default.Settings,
                    tooltip = "الإعدادات",
                    isSelected = false,
                    colorScheme = colorScheme,
                    onClick = onOpenSettings
                )
            }

            // 7. AI Assistant Panel (Gemini AI)
            item(key = "ai_assistant") {
                ToolbarIconButtonWithBadge(
                    icon = Icons.Default.AutoAwesome,
                    tooltip = "مساعد الذكاء الاصطناعي",
                    hasBadge = true,
                    isSelected = activePanel == KeyboardPanel.AI_ASSISTANT,
                    colorScheme = colorScheme,
                    onClick = {
                        onPanelSelect(if (activePanel == KeyboardPanel.AI_ASSISTANT) KeyboardPanel.NONE else KeyboardPanel.AI_ASSISTANT)
                    }
                )
            }

            // 8. GIF Library Button
            item(key = "gif_panel") {
                ToolbarIconButtonWithText(
                    text = "GIF",
                    tooltip = "مكتبة صور متحركة GIF",
                    isSelected = activePanel == KeyboardPanel.GIFS,
                    colorScheme = colorScheme,
                    onClick = {
                        onPanelSelect(if (activePanel == KeyboardPanel.GIFS) KeyboardPanel.NONE else KeyboardPanel.GIFS)
                    }
                )
            }

            // 9. Emoji & Stickers
            item(key = "emoji") {
                ToolbarIconButton(
                    icon = Icons.Default.Mood,
                    tooltip = "الملصقات والإيموجي",
                    isSelected = activePanel == KeyboardPanel.EMOJI,
                    colorScheme = colorScheme,
                    onClick = {
                        onPanelSelect(if (activePanel == KeyboardPanel.EMOJI) KeyboardPanel.NONE else KeyboardPanel.EMOJI)
                    }
                )
            }

            // 10. Decorations Panel Button (زخرفة)
            item(key = "decorations") {
                ToolbarIconButton(
                    icon = Icons.Default.TextFields,
                    tooltip = "زخرفة النصوص",
                    isSelected = activePanel == KeyboardPanel.DECORATIONS,
                    colorScheme = colorScheme,
                    onClick = {
                        onPanelSelect(if (activePanel == KeyboardPanel.DECORATIONS) KeyboardPanel.NONE else KeyboardPanel.DECORATIONS)
                    }
                )
            }

            // 11. Text Editing / Cursor Controller
            item(key = "editing") {
                ToolbarIconButton(
                    icon = Icons.Default.OpenWith,
                    tooltip = "تحريك المؤشر والتحكم",
                    isSelected = activePanel == KeyboardPanel.EDITING,
                    colorScheme = colorScheme,
                    onClick = {
                        onPanelSelect(if (activePanel == KeyboardPanel.EDITING) KeyboardPanel.NONE else KeyboardPanel.EDITING)
                    }
                )
            }

            // 12. Primary Menu Button
            item(key = "menu") {
                ToolbarIconButton(
                    icon = Icons.Default.GridView,
                    tooltip = "قائمة الميزات",
                    isSelected = activePanel == KeyboardPanel.MENU,
                    colorScheme = colorScheme,
                    onClick = {
                        onPanelSelect(if (activePanel == KeyboardPanel.MENU) KeyboardPanel.NONE else KeyboardPanel.MENU)
                    }
                )
            }

            // 13. Settings Gear
            item(key = "settings") {
                ToolbarIconButton(
                    icon = Icons.Default.Settings,
                    tooltip = "الإعدادات",
                    isSelected = false,
                    colorScheme = colorScheme,
                    onClick = onOpenSettings
                )
            }

            // 14. Undo / Redo
            if (showUndoRedo && onUndo != null) {
                item(key = "undo") {
                    ToolbarIconButton(
                        icon = Icons.AutoMirrored.Filled.Undo,
                        tooltip = "تراجع",
                        isSelected = false,
                        colorScheme = colorScheme,
                        onClick = onUndo
                    )
                }
            }
            if (showUndoRedo && onRedo != null) {
                item(key = "redo") {
                    ToolbarIconButton(
                        icon = Icons.AutoMirrored.Filled.Redo,
                        tooltip = "إعادة",
                        isSelected = false,
                        colorScheme = colorScheme,
                        onClick = onRedo
                    )
                }
            }
        }
    }
}

@Composable
fun ToolbarIconButton(
    icon: ImageVector,
    tooltip: String,
    isSelected: Boolean,
    colorScheme: KeyboardColorScheme,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) colorScheme.accent.copy(alpha = 0.25f) else Color.Transparent)
            .clickable(role = androidx.compose.ui.semantics.Role.Button) {
                HapticHelper.performKeyHaptic(context, view)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tooltip,
            tint = if (isSelected) colorScheme.accent else colorScheme.keyText.copy(alpha = 0.85f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ToolbarIconButtonWithBadge(
    icon: ImageVector,
    tooltip: String,
    hasBadge: Boolean,
    isSelected: Boolean,
    colorScheme: KeyboardColorScheme,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) colorScheme.accent.copy(alpha = 0.25f) else Color.Transparent)
            .clickable(role = androidx.compose.ui.semantics.Role.Button) {
                HapticHelper.performKeyHaptic(context, view)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tooltip,
            tint = if (isSelected) colorScheme.accent else colorScheme.keyText.copy(alpha = 0.85f),
            modifier = Modifier.size(20.dp)
        )
        if (hasBadge) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF3B30))
                    .align(Alignment.TopStart)
            )
        }
    }
}

@Composable
fun ToolbarIconButtonWithText(
    text: String,
    tooltip: String,
    isSelected: Boolean,
    colorScheme: KeyboardColorScheme,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) colorScheme.accent.copy(alpha = 0.25f) else Color.Transparent)
            .clickable(role = androidx.compose.ui.semantics.Role.Button) {
                HapticHelper.performKeyHaptic(context, view)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) colorScheme.accent else colorScheme.keyText.copy(alpha = 0.85f),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
