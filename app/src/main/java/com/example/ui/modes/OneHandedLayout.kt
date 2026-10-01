package com.example.ui.modes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.theme.NovaTheme
import com.example.core.theme.OneHandedSide

@Composable
fun OneHandedLayout(
    side: OneHandedSide,
    scaleFactor: Float,
    onFlipSide: () -> Unit,
    onExpandFull: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NovaTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background),
        verticalAlignment = Alignment.Bottom
    ) {
        if (side == OneHandedSide.RIGHT) {
            // Sidebar on the Left
            OneHandedSideControls(
                isRightSided = true,
                onFlipSide = onFlipSide,
                onExpandFull = onExpandFull,
                modifier = Modifier
                    .width(48.dp)
                    .padding(vertical = 12.dp)
            )
            // Scaled Keyboard
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(scaleFactor)
            ) {
                content()
            }
        } else {
            // Scaled Keyboard
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(scaleFactor)
            ) {
                content()
            }
            // Sidebar on the Right
            OneHandedSideControls(
                isRightSided = false,
                onFlipSide = onFlipSide,
                onExpandFull = onExpandFull,
                modifier = Modifier
                    .width(48.dp)
                    .padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun OneHandedSideControls(
    isRightSided: Boolean,
    onFlipSide: () -> Unit,
    onExpandFull: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NovaTheme.colors

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        // Expand to Full Mode
        IconButton(
            onClick = onExpandFull,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(colors.surface)
                .border(1.dp, colors.accentPrimary, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Fullscreen,
                contentDescription = "Expand to Full Mode",
                tint = colors.accentPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        // Flip Thumb Side (Left <-> Right)
        IconButton(
            onClick = onFlipSide,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(colors.surface)
                .border(1.dp, colors.accentSecondary, CircleShape)
        ) {
            Icon(
                imageVector = if (isRightSided) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Flip Side",
                tint = colors.accentSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
