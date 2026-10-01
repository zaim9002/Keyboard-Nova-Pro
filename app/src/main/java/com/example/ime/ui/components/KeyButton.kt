package com.example.ime.ui.components

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.util.HapticHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val KeyShadowColor = Color.Black.copy(alpha = 0.15f)

@Composable
fun KeyButton(
    modifier: Modifier = Modifier,
    text: String,
    secondaryText: String? = null,
    isSpecial: Boolean = false,
    fontSize: TextUnit = 19.sp,
    secondaryFontSize: TextUnit = 9.sp,
    height: Dp = 48.dp,
    cornerRadius: Dp = 6.dp,
    strokeBorder: Boolean = false,
    colorScheme: KeyboardColorScheme,
    hapticEnabled: Boolean = true,
    hapticIntensity: String = "Medium",
    hapticDurationMs: Int = 20,
    soundEnabled: Boolean = false,
    soundType: String = "CLICK",
    soundVolume: Int = 50,
    showPreview: Boolean = !isSpecial,
    onLongClickWithCoords: ((LayoutCoordinates) -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onPreviewChange: ((String, LayoutCoordinates?, Boolean) -> Unit)? = null,
    onClick: () -> Unit
) {
    val view = LocalView.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }
    var keyCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLongClick by rememberUpdatedState(onLongClick)
    val currentOnLongClickWithCoords by rememberUpdatedState(onLongClickWithCoords)
    val currentOnPreviewChange by rememberUpdatedState(onPreviewChange)
    val currentHapticEnabled by rememberUpdatedState(hapticEnabled)
    val currentHapticIntensity by rememberUpdatedState(hapticIntensity)
    val currentHapticDurationMs by rememberUpdatedState(hapticDurationMs)
    val currentSoundEnabled by rememberUpdatedState(soundEnabled)
    val currentSoundType by rememberUpdatedState(soundType)
    val currentSoundVolume by rememberUpdatedState(soundVolume)
    val currentShowPreview by rememberUpdatedState(showPreview)
    val currentText by rememberUpdatedState(text)
    val currentIsSpecial by rememberUpdatedState(isSpecial)

    val hasLongClick = currentOnLongClickWithCoords != null || currentOnLongClick != null

    val bgColor = if (isSpecial) {
        if (isPressed) colorScheme.specialKeyBackground.copy(alpha = 0.8f) else colorScheme.specialKeyBackground
    } else {
        if (isPressed) colorScheme.accent.copy(alpha = 0.35f) else colorScheme.keyBackground
    }
    val textColor = if (isSpecial) colorScheme.specialKeyText else colorScheme.keyText
    val keyShape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }

    val baseModifier = modifier
        .padding(horizontal = 1.5.dp, vertical = 2.dp)
        .height(height)

    val positionedModifier = baseModifier.onGloballyPositioned { keyCoordinates = it }

    val boxModifier = if (strokeBorder) {
        positionedModifier
            .shadow(
                elevation = if (isPressed) 0.5.dp else 1.2.dp,
                shape = keyShape,
                ambientColor = KeyShadowColor,
                spotColor = KeyShadowColor
            )
            .border(
                width = 0.8.dp,
                color = if (isSpecial) colorScheme.borderColor.copy(alpha = 0.3f) else colorScheme.borderColor.copy(alpha = 0.45f),
                shape = keyShape
            )
            .clip(keyShape)
            .background(bgColor)
    } else {
        positionedModifier
            .shadow(
                elevation = if (isPressed) 0.5.dp else 1.2.dp,
                shape = keyShape,
                ambientColor = KeyShadowColor,
                spotColor = KeyShadowColor
            )
            .clip(keyShape)
            .background(bgColor)
    }

    Box(
        modifier = boxModifier
            .pointerInput(hasLongClick) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    isPressed = true

                    if (currentShowPreview && !currentIsSpecial && currentText.isNotBlank()) {
                        currentOnPreviewChange?.invoke(currentText, keyCoordinates, true)
                    }

                    if (currentHapticEnabled) {
                        HapticHelper.performKeyHaptic(context, view, currentHapticIntensity, currentHapticDurationMs)
                    }
                    if (currentSoundEnabled) {
                        HapticHelper.performKeySound(context, view, currentSoundType, currentSoundVolume)
                    }

                    var isLongTriggered = false
                    val longPressJob: Job? = if (hasLongClick) {
                        coroutineScope.launch {
                            delay(350L)
                            isLongTriggered = true
                            if (currentHapticEnabled) {
                                HapticHelper.performKeyHaptic(context, view, currentHapticIntensity, currentHapticDurationMs)
                            }
                            keyCoordinates?.let { coords ->
                                currentOnLongClickWithCoords?.invoke(coords)
                            } ?: currentOnLongClick?.invoke()
                        }
                    } else null

                    var isCancelled = false
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) {
                            change.consume()
                            break
                        }
                        val delta = change.position - down.position
                        if (delta.getDistance() > 120f) {
                            isCancelled = true
                            longPressJob?.cancel()
                            break
                        }
                    }

                    longPressJob?.cancel()
                    isPressed = false
                    if (currentShowPreview && !currentIsSpecial) {
                        currentOnPreviewChange?.invoke(currentText, null, false)
                    }

                    if (!isCancelled && !isLongTriggered) {
                        currentOnClick()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = if (isSpecial || isPressed) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )

        if (!secondaryText.isNullOrEmpty()) {
            Text(
                text = secondaryText,
                color = textColor.copy(alpha = 0.55f),
                fontSize = secondaryFontSize,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 1.5.dp, end = 3.dp)
            )
        }
    }
}

@Composable
fun RepeatingDeleteKeyButton(
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    cornerRadius: Dp = 6.dp,
    strokeBorder: Boolean = false,
    colorScheme: KeyboardColorScheme,
    hapticEnabled: Boolean = true,
    hapticIntensity: String = "Medium",
    hapticDurationMs: Int = 20,
    soundEnabled: Boolean = false,
    soundType: String = "CLICK",
    soundVolume: Int = 50,
    onDelete: () -> Unit,
    onDeleteWord: (() -> Unit)? = null,
    onDeleteAll: (() -> Unit)? = null
) {
    val view = LocalView.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }

    val currentOnDelete by rememberUpdatedState(onDelete)
    val currentOnDeleteWord by rememberUpdatedState(onDeleteWord)
    val currentOnDeleteAll by rememberUpdatedState(onDeleteAll)
    val currentHapticEnabled by rememberUpdatedState(hapticEnabled)
    val currentHapticIntensity by rememberUpdatedState(hapticIntensity)
    val currentHapticDurationMs by rememberUpdatedState(hapticDurationMs)
    val currentSoundEnabled by rememberUpdatedState(soundEnabled)
    val currentSoundType by rememberUpdatedState(soundType)
    val currentSoundVolume by rememberUpdatedState(soundVolume)

    val keyShape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val baseModifier = modifier
        .padding(horizontal = 1.5.dp, vertical = 2.dp)
        .height(height)

    val boxModifier = if (strokeBorder) {
        baseModifier
            .shadow(
                elevation = if (isPressed) 0.5.dp else 1.2.dp,
                shape = keyShape,
                ambientColor = KeyShadowColor,
                spotColor = KeyShadowColor
            )
            .border(0.8.dp, colorScheme.borderColor.copy(alpha = 0.35f), keyShape)
            .clip(keyShape)
            .background(if (isPressed) colorScheme.specialKeyBackground.copy(alpha = 0.8f) else colorScheme.specialKeyBackground)
    } else {
        baseModifier
            .shadow(
                elevation = if (isPressed) 0.5.dp else 1.2.dp,
                shape = keyShape,
                ambientColor = KeyShadowColor,
                spotColor = KeyShadowColor
            )
            .clip(keyShape)
            .background(if (isPressed) colorScheme.specialKeyBackground.copy(alpha = 0.8f) else colorScheme.specialKeyBackground)
    }

    Box(
        modifier = boxModifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    if (currentHapticEnabled) {
                        HapticHelper.performKeyHaptic(context, view, currentHapticIntensity, currentHapticDurationMs)
                    }
                    if (currentSoundEnabled) {
                        HapticHelper.performKeySound(context, view, currentSoundType, currentSoundVolume)
                    }
                    currentOnDelete()

                    var isSwipedWord = false
                    var totalDragX = 0f
                    val repeatJob = coroutineScope.launch {
                        delay(380L)
                        var interval = 80L
                        while (true) {
                            if (!isSwipedWord) {
                                currentOnDelete()
                                if (currentHapticEnabled) {
                                    HapticHelper.performKeyHaptic(context, view, "Light", 10)
                                }
                            }
                            delay(interval)
                            if (interval > 40L) interval -= 5L
                        }
                    }

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) {
                            change.consume()
                            break
                        }
                        val dragAmountX = change.position.x - down.position.x
                        totalDragX = dragAmountX
                        if (!isSwipedWord && dragAmountX < -60f) {
                            isSwipedWord = true
                            repeatJob.cancel()
                            if (currentHapticEnabled) {
                                HapticHelper.performKeyHaptic(context, view, "Heavy", 30)
                            }
                            currentOnDeleteWord?.invoke() ?: currentOnDelete()
                        }
                    }

                    repeatJob.cancel()
                    isPressed = false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Icon(
            imageVector = Icons.AutoMirrored.Filled.Backspace,
            contentDescription = "مسح",
            tint = colorScheme.specialKeyText,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun EnterKeyButton(
    modifier: Modifier = Modifier,
    keyHeight: Dp = 48.dp,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    colorScheme: KeyboardColorScheme,
    cornerRadius: Dp = 6.dp,
    strokeBorder: Boolean = false,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    soundType: String = "CLICK",
    soundVolume: Int = 50,
    hapticIntensity: String = "Medium",
    hapticDurationMs: Int = 20,
    onEnter: () -> Unit,
    onLongPressEnter: (() -> Unit)? = null
) {
    val view = LocalView.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }

    val currentOnEnter by rememberUpdatedState(onEnter)
    val currentOnLongPressEnter by rememberUpdatedState(onLongPressEnter)
    val currentHapticEnabled by rememberUpdatedState(hapticEnabled)
    val currentHapticIntensity by rememberUpdatedState(hapticIntensity)
    val currentHapticDurationMs by rememberUpdatedState(hapticDurationMs)
    val currentSoundEnabled by rememberUpdatedState(soundEnabled)
    val currentSoundType by rememberUpdatedState(soundType)
    val currentSoundVolume by rememberUpdatedState(soundVolume)

    val keyShape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val baseModifier = modifier
        .padding(horizontal = 1.5.dp, vertical = 2.dp)
        .height(keyHeight)

    val boxModifier = if (strokeBorder) {
        baseModifier
            .shadow(
                elevation = if (isPressed) 0.5.dp else 1.2.dp,
                shape = keyShape,
                ambientColor = KeyShadowColor,
                spotColor = KeyShadowColor
            )
            .border(0.8.dp, colorScheme.borderColor.copy(alpha = 0.4f), keyShape)
            .clip(keyShape)
            .background(if (isPressed) colorScheme.accent.copy(alpha = 0.8f) else colorScheme.accent)
    } else {
        baseModifier
            .shadow(
                elevation = if (isPressed) 0.5.dp else 1.2.dp,
                shape = keyShape,
                ambientColor = KeyShadowColor,
                spotColor = KeyShadowColor
            )
            .clip(keyShape)
            .background(if (isPressed) colorScheme.accent.copy(alpha = 0.8f) else colorScheme.accent)
    }

    Box(
        modifier = boxModifier
            .pointerInput(currentOnLongPressEnter != null) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    if (currentHapticEnabled) {
                        HapticHelper.performKeyHaptic(context, view, currentHapticIntensity, currentHapticDurationMs)
                    }
                    if (currentSoundEnabled) {
                        HapticHelper.performKeySound(context, view, currentSoundType, currentSoundVolume)
                    }

                    var isLongTriggered = false
                    val longJob = if (currentOnLongPressEnter != null) {
                        coroutineScope.launch {
                            delay(400L)
                            isLongTriggered = true
                            if (currentHapticEnabled) {
                                HapticHelper.performKeyHaptic(context, view, "Heavy")
                            }
                            currentOnLongPressEnter?.invoke()
                        }
                    } else null

                    var isCancelled = false
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) {
                            change.consume()
                            break
                        }
                        val delta = change.position - down.position
                        if (delta.getDistance() > 100f) {
                            isCancelled = true
                            longJob?.cancel()
                            break
                        }
                    }

                    longJob?.cancel()
                    isPressed = false
                    if (!isCancelled && !isLongTriggered) {
                        currentOnEnter()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Icon(
            imageVector = actionIcon,
            contentDescription = "إدخال",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}
