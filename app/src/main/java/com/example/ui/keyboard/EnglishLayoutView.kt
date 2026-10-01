package com.example.ui.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Language
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.core.ime.InputConnectionDispatcher
import com.example.core.layout.EnglishKeyMatrix
import com.example.core.layout.KeyboardLayoutType
import com.example.core.layout.ShiftState
import com.example.core.theme.NovaTheme
import com.example.ui.components.KeyButton

@Composable
fun EnglishLayoutView(
    dispatcher: InputConnectionDispatcher,
    keyHeight: Dp,
    onFeedback: () -> Unit,
    onLongPressFeedback: () -> Unit,
    onSwitchLayout: (KeyboardLayoutType) -> Unit,
    onLanguageToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NovaTheme.colors
    var shiftState by remember { mutableStateOf(ShiftState.OFF) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        // Row 1 (Q W E R T Y U I O P)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EnglishKeyMatrix.row1.forEachIndexed { index, char ->
                val displayText = if (shiftState != ShiftState.OFF) char.uppercase() else char
                val subHint = EnglishKeyMatrix.row1NumberHints.getOrNull(index)
                val variants = EnglishKeyMatrix.longPressVariants[char] ?: emptyList()

                KeyButton(
                    label = displayText,
                    subHint = subHint,
                    height = keyHeight,
                    alternateVariants = variants,
                    modifier = Modifier.weight(1f),
                    onTap = {
                        dispatcher.commitText(displayText)
                        onFeedback()
                        if (shiftState == ShiftState.ONCE) {
                            shiftState = ShiftState.OFF
                        }
                    },
                    onVariantSelected = { variant ->
                        val text = if (shiftState != ShiftState.OFF) variant.uppercase() else variant
                        dispatcher.commitText(text)
                        onFeedback()
                        if (shiftState == ShiftState.ONCE) {
                            shiftState = ShiftState.OFF
                        }
                    }
                )
            }
        }

        // Row 2 (A S D F G H J K L)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EnglishKeyMatrix.row2.forEach { char ->
                val displayText = if (shiftState != ShiftState.OFF) char.uppercase() else char
                val variants = EnglishKeyMatrix.longPressVariants[char] ?: emptyList()

                KeyButton(
                    label = displayText,
                    height = keyHeight,
                    alternateVariants = variants,
                    modifier = Modifier.weight(1f),
                    onTap = {
                        dispatcher.commitText(displayText)
                        onFeedback()
                        if (shiftState == ShiftState.ONCE) {
                            shiftState = ShiftState.OFF
                        }
                    },
                    onVariantSelected = { variant ->
                        val text = if (shiftState != ShiftState.OFF) variant.uppercase() else variant
                        dispatcher.commitText(text)
                        onFeedback()
                        if (shiftState == ShiftState.ONCE) {
                            shiftState = ShiftState.OFF
                        }
                    }
                )
            }
        }

        // Row 3 (Shift + Z X C V B N M + Backspace)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shift Key with 3-state toggle (OFF -> ONCE -> LOCKED)
            KeyButton(
                icon = Icons.Default.ArrowUpward,
                height = keyHeight,
                modifier = Modifier.weight(1.4f),
                isAccent = shiftState == ShiftState.LOCKED,
                isSpecial = shiftState == ShiftState.ONCE,
                onTap = {
                    shiftState = when (shiftState) {
                        ShiftState.OFF -> ShiftState.ONCE
                        ShiftState.ONCE -> ShiftState.LOCKED
                        ShiftState.LOCKED -> ShiftState.OFF
                    }
                    onFeedback()
                }
            )

            EnglishKeyMatrix.row3.forEach { char ->
                val displayText = if (shiftState != ShiftState.OFF) char.uppercase() else char
                val variants = EnglishKeyMatrix.longPressVariants[char] ?: emptyList()

                KeyButton(
                    label = displayText,
                    height = keyHeight,
                    alternateVariants = variants,
                    modifier = Modifier.weight(1f),
                    onTap = {
                        dispatcher.commitText(displayText)
                        onFeedback()
                        if (shiftState == ShiftState.ONCE) {
                            shiftState = ShiftState.OFF
                        }
                    },
                    onVariantSelected = { variant ->
                        val text = if (shiftState != ShiftState.OFF) variant.uppercase() else variant
                        dispatcher.commitText(text)
                        onFeedback()
                        if (shiftState == ShiftState.ONCE) {
                            shiftState = ShiftState.OFF
                        }
                    }
                )
            }

            // Backspace Key
            KeyButton(
                icon = Icons.AutoMirrored.Filled.Backspace,
                height = keyHeight,
                modifier = Modifier.weight(1.4f),
                isSpecial = true,
                onTap = {
                    dispatcher.deleteBackspace()
                    onFeedback()
                },
                onPressStart = {
                    dispatcher.startBackspaceRepeat(onFeedback)
                },
                onPressEnd = {
                    dispatcher.stopBackspaceRepeat()
                }
            )
        }

        // Row 4 (Bottom Bar)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Numbers & Symbols toggle
            KeyButton(
                label = "?123",
                height = keyHeight,
                modifier = Modifier.weight(1.3f),
                isSpecial = true,
                onTap = {
                    onSwitchLayout(KeyboardLayoutType.SYMBOLS)
                    onFeedback()
                }
            )

            // Comma key
            KeyButton(
                label = ",",
                height = keyHeight,
                modifier = Modifier.weight(1f),
                alternateVariants = listOf(",", "?", "!", ":", ";", "/"),
                onTap = {
                    dispatcher.commitText(",")
                    onFeedback()
                },
                onVariantSelected = { p ->
                    dispatcher.commitText(p)
                    onFeedback()
                }
            )

            // Language Switcher
            KeyButton(
                icon = Icons.Default.Language,
                height = keyHeight,
                modifier = Modifier.weight(1f),
                isSpecial = true,
                onTap = {
                    onLanguageToggle()
                    onFeedback()
                }
            )

            // Spacebar
            SpacebarKey(
                label = "NOVA",
                height = keyHeight,
                modifier = Modifier.weight(3.8f),
                onTap = {
                    dispatcher.commitText(" ")
                    onFeedback()
                }
            )

            // Dot key
            KeyButton(
                label = ".",
                height = keyHeight,
                modifier = Modifier.weight(1f),
                alternateVariants = listOf(".", "?", "!", ":", ";", "/", "@"),
                onTap = {
                    dispatcher.commitText(".")
                    onFeedback()
                },
                onVariantSelected = { p ->
                    dispatcher.commitText(p)
                    onFeedback()
                }
            )

            // Action / Return key
            KeyButton(
                icon = Icons.AutoMirrored.Filled.KeyboardReturn,
                height = keyHeight,
                modifier = Modifier.weight(1.3f),
                isAccent = true,
                onTap = {
                    dispatcher.performImeAction()
                    onFeedback()
                }
            )
        }
    }
}
