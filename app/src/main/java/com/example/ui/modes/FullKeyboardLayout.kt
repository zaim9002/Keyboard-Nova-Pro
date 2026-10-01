package com.example.ui.modes

import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardCapslock
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ime.HapticFeedbackManager
import com.example.core.ime.InputConnectionDispatcher
import com.example.core.layout.ArabicKeyMatrix
import com.example.core.layout.EnglishKeyMatrix
import com.example.core.layout.KeyboardLayoutType
import com.example.core.layout.ShiftState
import com.example.core.layout.SymbolsKeyMatrix
import com.example.core.theme.NovaTheme
import com.example.ui.components.KeyButton

@Composable
fun FullKeyboardLayout(
    layoutType: KeyboardLayoutType,
    shiftState: ShiftState,
    dispatcher: InputConnectionDispatcher,
    hapticManager: HapticFeedbackManager,
    isHapticEnabled: Boolean,
    isAudioEnabled: Boolean,
    keyHeightDp: Dp,
    showHarakatRow: Boolean,
    imeAction: Int = EditorInfo.IME_ACTION_DONE,
    onLayoutChange: (KeyboardLayoutType) -> Unit,
    onShiftToggle: () -> Unit,
    onLanguageToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NovaTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(horizontal = 3.dp, vertical = 2.dp)
    ) {
        when (layoutType) {
            KeyboardLayoutType.ARABIC -> {
                ArabicKeyboardDeck(
                    dispatcher = dispatcher,
                    hapticManager = hapticManager,
                    isHapticEnabled = isHapticEnabled,
                    isAudioEnabled = isAudioEnabled,
                    keyHeightDp = keyHeightDp,
                    showHarakatRow = showHarakatRow,
                    imeAction = imeAction,
                    onSwitchToSymbols = { onLayoutChange(KeyboardLayoutType.SYMBOLS) },
                    onLanguageToggle = onLanguageToggle
                )
            }
            KeyboardLayoutType.ENGLISH -> {
                EnglishKeyboardDeck(
                    shiftState = shiftState,
                    dispatcher = dispatcher,
                    hapticManager = hapticManager,
                    isHapticEnabled = isHapticEnabled,
                    isAudioEnabled = isAudioEnabled,
                    keyHeightDp = keyHeightDp,
                    imeAction = imeAction,
                    onShiftToggle = onShiftToggle,
                    onSwitchToSymbols = { onLayoutChange(KeyboardLayoutType.SYMBOLS) },
                    onLanguageToggle = onLanguageToggle
                )
            }
            KeyboardLayoutType.SYMBOLS -> {
                SymbolsKeyboardDeck(
                    isPageTwo = false,
                    dispatcher = dispatcher,
                    hapticManager = hapticManager,
                    isHapticEnabled = isHapticEnabled,
                    isAudioEnabled = isAudioEnabled,
                    keyHeightDp = keyHeightDp,
                    imeAction = imeAction,
                    onToggleSymbolsPage = { onLayoutChange(KeyboardLayoutType.MORE_SYMBOLS) },
                    onReturnToAlphabet = { onLayoutChange(KeyboardLayoutType.ARABIC) },
                    onLanguageToggle = onLanguageToggle
                )
            }
            KeyboardLayoutType.MORE_SYMBOLS -> {
                SymbolsKeyboardDeck(
                    isPageTwo = true,
                    dispatcher = dispatcher,
                    hapticManager = hapticManager,
                    isHapticEnabled = isHapticEnabled,
                    isAudioEnabled = isAudioEnabled,
                    keyHeightDp = keyHeightDp,
                    imeAction = imeAction,
                    onToggleSymbolsPage = { onLayoutChange(KeyboardLayoutType.SYMBOLS) },
                    onReturnToAlphabet = { onLayoutChange(KeyboardLayoutType.ARABIC) },
                    onLanguageToggle = onLanguageToggle
                )
            }
        }
    }
}

@Composable
private fun ArabicKeyboardDeck(
    dispatcher: InputConnectionDispatcher,
    hapticManager: HapticFeedbackManager,
    isHapticEnabled: Boolean,
    isAudioEnabled: Boolean,
    keyHeightDp: Dp,
    showHarakatRow: Boolean,
    imeAction: Int,
    onSwitchToSymbols: () -> Unit,
    onLanguageToggle: () -> Unit
) {
    val colors = NovaTheme.colors

    // Optional Quick Harakat top strip
    if (showHarakatRow) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ArabicKeyMatrix.harakatRow.forEach { mark ->
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .padding(horizontal = 2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(colors.surface)
                        .border(0.5.dp, colors.keyBorder, RoundedCornerShape(6.dp))
                        .clickable {
                            hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                            dispatcher.commitText(mark)
                        }
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = mark, color = colors.accentPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Row 1
    Row(modifier = Modifier.fillMaxWidth()) {
        ArabicKeyMatrix.row1.forEach { char ->
            KeyButton(
                modifier = Modifier.weight(1f),
                label = char,
                height = keyHeightDp,
                alternateVariants = ArabicKeyMatrix.longPressVariants[char] ?: emptyList(),
                onTap = {
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(char)
                },
                onVariantSelected = { variant ->
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(variant)
                }
            )
        }
    }

    // Row 2
    Row(modifier = Modifier.fillMaxWidth()) {
        ArabicKeyMatrix.row2.forEach { char ->
            KeyButton(
                modifier = Modifier.weight(1f),
                label = char,
                height = keyHeightDp,
                alternateVariants = ArabicKeyMatrix.longPressVariants[char] ?: emptyList(),
                onTap = {
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(char)
                },
                onVariantSelected = { variant ->
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(variant)
                }
            )
        }
    }

    // Row 3
    Row(modifier = Modifier.fillMaxWidth()) {
        // Backspace key on left for Arabic RTL
        KeyButton(
            modifier = Modifier.weight(1.3f),
            label = "",
            icon = Icons.AutoMirrored.Filled.Backspace,
            height = keyHeightDp,
            isSpecial = true,
            onTap = {
                hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                dispatcher.deleteBackspace()
            },
            onPressStart = {
                dispatcher.startBackspaceRepeat {
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                }
            },
            onPressEnd = {
                dispatcher.stopBackspaceRepeat()
            }
        )

        ArabicKeyMatrix.row3.forEach { char ->
            KeyButton(
                modifier = Modifier.weight(1f),
                label = char,
                height = keyHeightDp,
                alternateVariants = ArabicKeyMatrix.longPressVariants[char] ?: emptyList(),
                onTap = {
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(char)
                },
                onVariantSelected = { variant ->
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(variant)
                }
            )
        }
    }

    // Row 4: Bottom bar (Symbols, Language, Spacebar, Period, Action/Enter)
    BottomBarRow(
        symbolsLabel = "?١٢٣",
        spacebarLabel = "نوفا • مسافة",
        keyHeightDp = keyHeightDp,
        imeAction = imeAction,
        onSwitchToSymbols = onSwitchToSymbols,
        onLanguageToggle = onLanguageToggle,
        onCommitText = { text ->
            hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
            dispatcher.commitText(text)
        },
        onAction = {
            hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
            dispatcher.performImeAction(imeAction)
        }
    )
}

@Composable
private fun EnglishKeyboardDeck(
    shiftState: ShiftState,
    dispatcher: InputConnectionDispatcher,
    hapticManager: HapticFeedbackManager,
    isHapticEnabled: Boolean,
    isAudioEnabled: Boolean,
    keyHeightDp: Dp,
    imeAction: Int,
    onShiftToggle: () -> Unit,
    onSwitchToSymbols: () -> Unit,
    onLanguageToggle: () -> Unit
) {
    // Row 1
    Row(modifier = Modifier.fillMaxWidth()) {
        EnglishKeyMatrix.row1.forEachIndexed { index, char ->
            val displayChar = if (shiftState != ShiftState.OFF) char.uppercase() else char
            KeyButton(
                modifier = Modifier.weight(1f),
                label = displayChar,
                subHint = EnglishKeyMatrix.row1NumberHints.getOrNull(index),
                height = keyHeightDp,
                alternateVariants = EnglishKeyMatrix.longPressVariants[char] ?: emptyList(),
                onTap = {
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(displayChar)
                    if (shiftState == ShiftState.ONCE) onShiftToggle()
                },
                onVariantSelected = { variant ->
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(variant)
                }
            )
        }
    }

    // Row 2
    Row(modifier = Modifier.fillMaxWidth()) {
        EnglishKeyMatrix.row2.forEach { char ->
            val displayChar = if (shiftState != ShiftState.OFF) char.uppercase() else char
            KeyButton(
                modifier = Modifier.weight(1f),
                label = displayChar,
                height = keyHeightDp,
                alternateVariants = EnglishKeyMatrix.longPressVariants[char] ?: emptyList(),
                onTap = {
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(displayChar)
                    if (shiftState == ShiftState.ONCE) onShiftToggle()
                },
                onVariantSelected = { variant ->
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(variant)
                }
            )
        }
    }

    // Row 3
    Row(modifier = Modifier.fillMaxWidth()) {
        // Shift Key
        val shiftIcon: ImageVector = when (shiftState) {
            ShiftState.LOCKED -> Icons.Default.KeyboardCapslock
            ShiftState.ONCE -> Icons.Default.ArrowUpward
            ShiftState.OFF -> Icons.Default.ArrowDownward
        }
        KeyButton(
            modifier = Modifier.weight(1.3f),
            label = "",
            icon = shiftIcon,
            height = keyHeightDp,
            isSpecial = true,
            isAccent = shiftState != ShiftState.OFF,
            onTap = {
                hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                onShiftToggle()
            }
        )

        EnglishKeyMatrix.row3.forEach { char ->
            val displayChar = if (shiftState != ShiftState.OFF) char.uppercase() else char
            KeyButton(
                modifier = Modifier.weight(1f),
                label = displayChar,
                height = keyHeightDp,
                alternateVariants = EnglishKeyMatrix.longPressVariants[char] ?: emptyList(),
                onTap = {
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(displayChar)
                    if (shiftState == ShiftState.ONCE) onShiftToggle()
                },
                onVariantSelected = { variant ->
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(variant)
                }
            )
        }

        // Backspace Key
        KeyButton(
            modifier = Modifier.weight(1.3f),
            label = "",
            icon = Icons.AutoMirrored.Filled.Backspace,
            height = keyHeightDp,
            isSpecial = true,
            onTap = {
                hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                dispatcher.deleteBackspace()
            },
            onPressStart = {
                dispatcher.startBackspaceRepeat {
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                }
            },
            onPressEnd = {
                dispatcher.stopBackspaceRepeat()
            }
        )
    }

    // Row 4
    BottomBarRow(
        symbolsLabel = "?123",
        spacebarLabel = "NOVA • SPACE",
        keyHeightDp = keyHeightDp,
        imeAction = imeAction,
        onSwitchToSymbols = onSwitchToSymbols,
        onLanguageToggle = onLanguageToggle,
        onCommitText = { text ->
            hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
            dispatcher.commitText(text)
        },
        onAction = {
            hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
            dispatcher.performImeAction(imeAction)
        }
    )
}

@Composable
private fun SymbolsKeyboardDeck(
    isPageTwo: Boolean,
    dispatcher: InputConnectionDispatcher,
    hapticManager: HapticFeedbackManager,
    isHapticEnabled: Boolean,
    isAudioEnabled: Boolean,
    keyHeightDp: Dp,
    imeAction: Int,
    onToggleSymbolsPage: () -> Unit,
    onReturnToAlphabet: () -> Unit,
    onLanguageToggle: () -> Unit
) {
    val row1 = if (!isPageTwo) SymbolsKeyMatrix.page1Row1 else SymbolsKeyMatrix.page2Row1
    val row2 = if (!isPageTwo) SymbolsKeyMatrix.page1Row2 else SymbolsKeyMatrix.page2Row2
    val row3 = if (!isPageTwo) SymbolsKeyMatrix.page1Row3 else SymbolsKeyMatrix.page2Row3

    // Row 1
    Row(modifier = Modifier.fillMaxWidth()) {
        row1.forEach { char ->
            KeyButton(
                modifier = Modifier.weight(1f),
                label = char,
                height = keyHeightDp,
                onTap = {
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(char)
                }
            )
        }
    }

    // Row 2
    Row(modifier = Modifier.fillMaxWidth()) {
        row2.forEach { char ->
            KeyButton(
                modifier = Modifier.weight(1f),
                label = char,
                height = keyHeightDp,
                onTap = {
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(char)
                }
            )
        }
    }

    // Row 3
    Row(modifier = Modifier.fillMaxWidth()) {
        // Toggle 1/2 vs 2/2
        KeyButton(
            modifier = Modifier.weight(1.3f),
            label = if (!isPageTwo) "=\\<" else "?123",
            height = keyHeightDp,
            isSpecial = true,
            onTap = {
                hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                onToggleSymbolsPage()
            }
        )

        row3.forEach { char ->
            KeyButton(
                modifier = Modifier.weight(1f),
                label = char,
                height = keyHeightDp,
                onTap = {
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                    dispatcher.commitText(char)
                }
            )
        }

        // Backspace
        KeyButton(
            modifier = Modifier.weight(1.3f),
            label = "",
            icon = Icons.AutoMirrored.Filled.Backspace,
            height = keyHeightDp,
            isSpecial = true,
            onTap = {
                hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                dispatcher.deleteBackspace()
            },
            onPressStart = {
                dispatcher.startBackspaceRepeat {
                    hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
                }
            },
            onPressEnd = {
                dispatcher.stopBackspaceRepeat()
            }
        )
    }

    // Row 4
    BottomBarRow(
        symbolsLabel = "ABC",
        spacebarLabel = "NOVA",
        keyHeightDp = keyHeightDp,
        imeAction = imeAction,
        onSwitchToSymbols = onReturnToAlphabet,
        onLanguageToggle = onLanguageToggle,
        onCommitText = { text ->
            hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
            dispatcher.commitText(text)
        },
        onAction = {
            hapticManager.performKeyFeedback(isHapticEnabled = isHapticEnabled, isAudioEnabled = isAudioEnabled)
            dispatcher.performImeAction(imeAction)
        }
    )
}

@Composable
private fun BottomBarRow(
    symbolsLabel: String,
    spacebarLabel: String,
    keyHeightDp: Dp,
    imeAction: Int,
    onSwitchToSymbols: () -> Unit,
    onLanguageToggle: () -> Unit,
    onCommitText: (String) -> Unit,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Symbols toggle button
        KeyButton(
            modifier = Modifier.weight(1.2f),
            label = symbolsLabel,
            height = keyHeightDp,
            isSpecial = true,
            onTap = onSwitchToSymbols
        )

        // Comma
        KeyButton(
            modifier = Modifier.weight(0.9f),
            label = ",",
            height = keyHeightDp,
            onTap = { onCommitText(",") }
        )

        // Language toggle
        KeyButton(
            modifier = Modifier.weight(0.9f),
            label = "",
            icon = Icons.Default.Language,
            height = keyHeightDp,
            isSpecial = true,
            onTap = onLanguageToggle
        )

        // Spacebar with gradient glow styling
        KeyButton(
            modifier = Modifier.weight(3.8f),
            label = spacebarLabel,
            height = keyHeightDp,
            onTap = { onCommitText(" ") }
        )

        // Period with long-press punctuation variants
        KeyButton(
            modifier = Modifier.weight(0.9f),
            label = ".",
            height = keyHeightDp,
            alternateVariants = listOf(".", "،", "!", "؟", ":", ";", "-"),
            onTap = { onCommitText(".") },
            onVariantSelected = onCommitText
        )

        // Action/Enter button
        val actionIcon = when (imeAction) {
            EditorInfo.IME_ACTION_SEARCH -> Icons.Default.Search
            EditorInfo.IME_ACTION_SEND -> Icons.Default.Send
            EditorInfo.IME_ACTION_GO -> Icons.AutoMirrored.Filled.KeyboardReturn
            EditorInfo.IME_ACTION_DONE -> Icons.Default.Check
            else -> Icons.AutoMirrored.Filled.KeyboardReturn
        }

        KeyButton(
            modifier = Modifier.weight(1.3f),
            label = "",
            icon = actionIcon,
            height = keyHeightDp,
            isAccent = true,
            onTap = onAction
        )
    }
}
