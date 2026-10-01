package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ime.InputConnectionDispatcher
import com.example.core.theme.NovaTheme

@Composable
fun SplitTabletKeyboard(
    dispatcher: InputConnectionDispatcher,
    keyHeight: Dp,
    onExpandFull: () -> Unit,
    onClipboardClick: () -> Unit,
    onFeedback: () -> Unit,
    leftDeckContent: @Composable () -> Unit,
    rightDeckContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NovaTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // Left Thumb Deck
        Box(
            modifier = Modifier.weight(1f)
        ) {
            leftDeckContent()
        }

        // Center Productivity Island (D-Pad & Shortcuts)
        Column(
            modifier = Modifier
                .width(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surface)
                .border(1.dp, colors.keyBorder, RoundedCornerShape(12.dp))
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Expand mode
            IconButton(
                onClick = onExpandFull,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Expand to Full",
                    tint = colors.accentPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Directional Up
            IconButton(
                onClick = {
                    dispatcher.moveCursorUp()
                    onFeedback()
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Cursor Up",
                    tint = colors.accentPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Left / Right Navigation Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        dispatcher.moveCursorLeft()
                        onFeedback()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Cursor Left",
                        tint = colors.accentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = {
                        dispatcher.moveCursorRight()
                        onFeedback()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Cursor Right",
                        tint = colors.accentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Directional Down
            IconButton(
                onClick = {
                    dispatcher.moveCursorDown()
                    onFeedback()
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Cursor Down",
                    tint = colors.accentPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Quick Clipboard
            IconButton(
                onClick = onClipboardClick,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(colors.keyBackground)
            ) {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = "Clipboard",
                    tint = colors.accentSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Right Thumb Deck
        Box(
            modifier = Modifier.weight(1f)
        ) {
            rightDeckContent()
        }
    }
}
