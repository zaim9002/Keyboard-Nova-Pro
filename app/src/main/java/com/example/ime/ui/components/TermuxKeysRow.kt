package com.example.ime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.util.HapticHelper

@Composable
fun TermuxKeysRow(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    hapticEnabled: Boolean,
    onTextInput: (String) -> Unit,
    onMoveCursor: (Int) -> Unit,
    onHome: () -> Unit,
    onEnd: () -> Unit,
    onTab: () -> Unit,
    onEsc: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    var isCtrlActive by remember { mutableStateOf(false) }
    var isAltActive by remember { mutableStateOf(false) }

    val termuxKeyBg = colorScheme.specialKeyBackground.copy(alpha = 0.85f)
    val termuxText = colorScheme.accent

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(horizontal = 2.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Row 1: ESC, /, -, HOME, ▲, END, PGUP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            TermuxKey(
                text = "ESC",
                modifier = Modifier.weight(1f),
                bg = termuxKeyBg,
                color = termuxText,
                onClick = {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    onEsc()
                }
            )
            TermuxKey(
                text = "/",
                modifier = Modifier.weight(1f),
                bg = termuxKeyBg,
                color = colorScheme.keyText,
                onClick = {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    onTextInput("/")
                }
            )
            TermuxKey(
                text = "-",
                modifier = Modifier.weight(1f),
                bg = termuxKeyBg,
                color = colorScheme.keyText,
                onClick = {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    onTextInput("-")
                }
            )
            TermuxKey(
                text = "HOME",
                modifier = Modifier.weight(1.2f),
                bg = termuxKeyBg,
                color = termuxText,
                onClick = {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    onHome()
                }
            )
            TermuxKey(
                text = "▲",
                modifier = Modifier.weight(1f),
                bg = termuxKeyBg,
                color = termuxText,
                onClick = {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    onMoveCursor(-25)
                }
            )
            TermuxKey(
                text = "END",
                modifier = Modifier.weight(1.2f),
                bg = termuxKeyBg,
                color = termuxText,
                onClick = {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    onEnd()
                }
            )
            TermuxKey(
                text = "PGUP",
                modifier = Modifier.weight(1.2f),
                bg = termuxKeyBg,
                color = termuxText,
                onClick = {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    onMoveCursor(-50)
                }
            )
        }
        // Row 2: TAB, CTRL, ALT, ◀, ▼, ▶, PGDN
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            TermuxKey(
                text = "TAB",
                modifier = Modifier.weight(1.2f),
                bg = termuxKeyBg,
                color = termuxText,
                onClick = {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    onTab()
                }
            )
            TermuxKey(
                text = "CTRL",
                modifier = Modifier.weight(1.2f),
                bg = if (isCtrlActive) colorScheme.accent else termuxKeyBg,
                color = if (isCtrlActive) Color.Black else termuxText,
                onClick = {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    isCtrlActive = !isCtrlActive
                }
            )
            TermuxKey(
                text = "ALT",
                modifier = Modifier.weight(1.1f),
                bg = if (isAltActive) colorScheme.accent else termuxKeyBg,
                color = if (isAltActive) Color.Black else termuxText,
                onClick = {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    isAltActive = !isAltActive
                }
            )
            TermuxKey(
                text = "◀",
                modifier = Modifier.weight(1f),
                bg = termuxKeyBg,
                color = termuxText,
                onClick = {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    onMoveCursor(-1)
                }
            )
            TermuxKey(
                text = "▼",
                modifier = Modifier.weight(1f),
                bg = termuxKeyBg,
                color = termuxText,
                onClick = {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    onMoveCursor(25)
                }
            )
            TermuxKey(
                text = "▶",
                modifier = Modifier.weight(1f),
                bg = termuxKeyBg,
                color = termuxText,
                onClick = {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    onMoveCursor(1)
                }
            )
            TermuxKey(
                text = "PGDN",
                modifier = Modifier.weight(1.2f),
                bg = termuxKeyBg,
                color = termuxText,
                onClick = {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    onMoveCursor(50)
                }
            )
        }
    }
}

@Composable
private fun TermuxKey(
    text: String,
    modifier: Modifier = Modifier,
    bg: Color,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
