package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.core.theme.NovaTheme

@Composable
fun KeyButton(
    modifier: Modifier = Modifier,
    label: String = "",
    icon: ImageVector? = null,
    subHint: String? = null,
    height: Dp = 50.dp,
    isAccent: Boolean = false,
    isSpecial: Boolean = false,
    alternateVariants: List<String> = emptyList(),
    onTap: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    onVariantSelected: ((String) -> Unit)? = null,
    onPressStart: (() -> Unit)? = null,
    onPressEnd: (() -> Unit)? = null
) {
    val colors = NovaTheme.colors
    var isPressed by remember { mutableStateOf(false) }
    var showPopup by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = tween(durationMillis = 60),
        label = "keyScale"
    )

    val keyBackground = when {
        isPressed -> colors.keyPressedBackground
        isAccent -> colors.keyAccentBackground
        isSpecial -> colors.keySpecialBackground
        else -> colors.keyBackground
    }

    val keyBorderColor = when {
        isPressed -> colors.keyBorderActive
        isAccent -> colors.accentPrimary
        else -> colors.keyBorder
    }

    val textColor = when {
        isAccent -> colors.keyAccentText
        isSpecial -> colors.keyText
        else -> colors.keyText
    }

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 2.5.dp)
            .height(height)
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 0.dp else 2.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = colors.neonGlow.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(keyBackground)
            .border(
                width = if (isPressed || isAccent) 1.5.dp else 1.dp,
                color = keyBorderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .pointerInput(label, alternateVariants) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onPressStart?.invoke()
                        tryAwaitRelease()
                        isPressed = false
                        onPressEnd?.invoke()
                    },
                    onTap = {
                        onTap()
                    },
                    onLongPress = {
                        if (alternateVariants.isNotEmpty()) {
                            showPopup = true
                        } else {
                            onLongPress?.invoke()
                        }
                    }
                )
            }
            .testTag("key_$label"),
        contentAlignment = Alignment.Center
    ) {
        if (subHint != null && subHint.isNotEmpty()) {
            Text(
                text = subHint,
                color = colors.keyTextSecondary.copy(alpha = 0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 4.dp, top = 2.dp)
            )
        }

        if (icon != null) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = label,
                color = textColor,
                fontSize = if (label.length > 2) 13.sp else 18.sp,
                fontWeight = if (isAccent) FontWeight.Bold else FontWeight.Medium
            )
        }

        // Long press alternate variants popup (e.g. Harakat or Arabic variants)
        if (showPopup && alternateVariants.isNotEmpty()) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, -130),
                onDismissRequest = { showPopup = false },
                properties = PopupProperties(focusable = true)
            ) {
                Row(
                    modifier = Modifier
                        .shadow(8.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.accentPrimary, RoundedCornerShape(12.dp))
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    alternateVariants.forEach { variant ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.keyBackground)
                                .border(0.5.dp, colors.keyBorder, RoundedCornerShape(8.dp))
                                .pointerInput(variant) {
                                    detectTapGestures {
                                        onVariantSelected?.invoke(variant)
                                        showPopup = false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = variant,
                                color = colors.accentPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
