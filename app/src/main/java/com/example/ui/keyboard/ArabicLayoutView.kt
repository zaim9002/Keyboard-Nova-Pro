package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ime.InputConnectionDispatcher
import com.example.core.layout.ArabicKeyMatrix
import com.example.core.layout.KeyboardLayoutType
import com.example.core.theme.NovaTheme
import com.example.ui.components.KeyButton

@Composable
fun ArabicLayoutView(
    dispatcher: InputConnectionDispatcher,
    keyHeight: Dp,
    showHarakatRow: Boolean,
    onFeedback: () -> Unit,
    onLongPressFeedback: () -> Unit,
    onSwitchLayout: (KeyboardLayoutType) -> Unit,
    onLanguageToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NovaTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        // Optional Quick Harakat / Diacritics Row
        if (showHarakatRow) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ArabicKeyMatrix.harakatRow.forEach { mark ->
                    KeyButton(
                        label = mark,
                        height = 36.dp,
                        modifier = Modifier.weight(1f),
                        isSpecial = true,
                        onTap = {
                            dispatcher.commitText(mark)
                            onFeedback()
                        }
                    )
                }
            }
        }

        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ArabicKeyMatrix.row1.forEach { char ->
                val variants = ArabicKeyMatrix.longPressVariants[char] ?: emptyList()
                KeyButton(
                    label = char,
                    height = keyHeight,
                    alternateVariants = variants,
                    modifier = Modifier.weight(1f),
                    onTap = {
                        dispatcher.commitText(char)
                        onFeedback()
                    },
                    onVariantSelected = { variant ->
                        dispatcher.commitText(variant)
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
            ArabicKeyMatrix.row2.forEach { char ->
                val variants = ArabicKeyMatrix.longPressVariants[char] ?: emptyList()
                KeyButton(
                    label = char,
                    height = keyHeight,
                    alternateVariants = variants,
                    modifier = Modifier.weight(1f),
                    onTap = {
                        dispatcher.commitText(char)
                        onFeedback()
                    },
                    onVariantSelected = { variant ->
                        dispatcher.commitText(variant)
                        onFeedback()
                    }
                )
            }
        }

        // Row 3 (Special chars + Backspace)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Harakat Quick key
            KeyButton(
                label = "ـَـُـِ",
                height = keyHeight,
                modifier = Modifier.weight(1.3f),
                isSpecial = true,
                alternateVariants = listOf("َ", "ً", "ُ", "ٌ", "ِ", "ٍ", "ْ", "ّ"),
                onTap = {
                    dispatcher.commitText("ّ")
                    onFeedback()
                },
                onVariantSelected = { haraka ->
                    dispatcher.commitText(haraka)
                    onFeedback()
                }
            )

            ArabicKeyMatrix.row3.forEach { char ->
                val variants = ArabicKeyMatrix.longPressVariants[char] ?: emptyList()
                KeyButton(
                    label = char,
                    height = keyHeight,
                    alternateVariants = variants,
                    modifier = Modifier.weight(1f),
                    onTap = {
                        dispatcher.commitText(char)
                        onFeedback()
                    },
                    onVariantSelected = { variant ->
                        dispatcher.commitText(variant)
                        onFeedback()
                    }
                )
            }

            // Backspace Key with repeat
            KeyButton(
                icon = Icons.AutoMirrored.Filled.Backspace,
                height = keyHeight,
                modifier = Modifier.weight(1.3f),
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

        // Row 4 (Bottom navigation, spacebar, action)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Numbers & Symbols toggle
            KeyButton(
                label = "?١٢٣",
                height = keyHeight,
                modifier = Modifier.weight(1.3f),
                isSpecial = true,
                onTap = {
                    onSwitchLayout(KeyboardLayoutType.SYMBOLS)
                    onFeedback()
                }
            )

            // Comma / punctuation
            KeyButton(
                label = "،",
                height = keyHeight,
                modifier = Modifier.weight(1f),
                alternateVariants = listOf("،", "؛", ":", "؟", "-"),
                onTap = {
                    dispatcher.commitText("،")
                    onFeedback()
                },
                onVariantSelected = { p ->
                    dispatcher.commitText(p)
                    onFeedback()
                }
            )

            // Language switcher
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

            // Spacebar with Glowing Cyber Gradient
            SpacebarKey(
                label = "مسافة (NOVA)",
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
                alternateVariants = listOf(".", "!", "؟", ":", "/", "@"),
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

@Composable
fun SpacebarKey(
    label: String,
    height: Dp,
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    val colors = NovaTheme.colors
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 2.5.dp)
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isPressed) colors.keyPressedBackground else colors.keyBackground
            )
            .border(
                width = if (isPressed) 1.5.dp else 1.dp,
                brush = if (isPressed) colors.spacebarGradient else androidx.compose.ui.graphics.SolidColor(colors.keyBorder),
                shape = RoundedCornerShape(8.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onTap() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Subtle cyber center glow line
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(2.dp)
                .background(colors.accentPrimary.copy(alpha = if (isPressed) 0.9f else 0.4f), RoundedCornerShape(1.dp))
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
        )

        Text(
            text = label,
            color = colors.keyTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
