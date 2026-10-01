package com.example.ui.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
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
import com.example.core.layout.KeyboardLayoutType
import com.example.core.layout.SymbolsKeyMatrix
import com.example.core.theme.NovaTheme
import com.example.ui.components.KeyButton

@Composable
fun SymbolsLayoutView(
    dispatcher: InputConnectionDispatcher,
    keyHeight: Dp,
    returnLayout: KeyboardLayoutType,
    onFeedback: () -> Unit,
    onReturnToAlpha: () -> Unit,
    onLanguageToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NovaTheme.colors
    var isPage2 by remember { mutableStateOf(false) }

    val row1 = if (isPage2) SymbolsKeyMatrix.page2Row1 else SymbolsKeyMatrix.page1Row1
    val row2 = if (isPage2) SymbolsKeyMatrix.page2Row2 else SymbolsKeyMatrix.page1Row2
    val row3 = if (isPage2) SymbolsKeyMatrix.page2Row3 else SymbolsKeyMatrix.page1Row3

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            row1.forEach { char ->
                KeyButton(
                    label = char,
                    height = keyHeight,
                    modifier = Modifier.weight(1f),
                    onTap = {
                        dispatcher.commitText(char)
                        onFeedback()
                    }
                )
            }
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            row2.forEach { char ->
                KeyButton(
                    label = char,
                    height = keyHeight,
                    modifier = Modifier.weight(1f),
                    onTap = {
                        dispatcher.commitText(char)
                        onFeedback()
                    }
                )
            }
        }

        // Row 3 (Page toggle + symbols + Backspace)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Page toggle button (1/2 <-> 2/2)
            KeyButton(
                label = if (isPage2) "1/2" else "=\\<",
                height = keyHeight,
                modifier = Modifier.weight(1.4f),
                isSpecial = true,
                onTap = {
                    isPage2 = !isPage2
                    onFeedback()
                }
            )

            row3.forEach { char ->
                KeyButton(
                    label = char,
                    height = keyHeight,
                    modifier = Modifier.weight(1f),
                    onTap = {
                        dispatcher.commitText(char)
                        onFeedback()
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

        // Row 4 (Alphabet return, Spacebar, Action)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Alphabet Return button (ABC / عربي)
            KeyButton(
                label = if (returnLayout == KeyboardLayoutType.ARABIC) "عربي" else "ABC",
                height = keyHeight,
                modifier = Modifier.weight(1.4f),
                isAccent = true,
                onTap = {
                    onReturnToAlpha()
                    onFeedback()
                }
            )

            // Comma key
            KeyButton(
                label = ",",
                height = keyHeight,
                modifier = Modifier.weight(1f),
                onTap = {
                    dispatcher.commitText(",")
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
                modifier = Modifier.weight(3.6f),
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
                onTap = {
                    dispatcher.commitText(".")
                    onFeedback()
                }
            )

            // Action Return
            KeyButton(
                icon = Icons.AutoMirrored.Filled.KeyboardReturn,
                height = keyHeight,
                modifier = Modifier.weight(1.4f),
                isAccent = true,
                onTap = {
                    dispatcher.performImeAction()
                    onFeedback()
                }
            )
        }
    }
}
