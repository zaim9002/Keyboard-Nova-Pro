package com.example.ui.keyboard

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.core.ime.HapticFeedbackManager
import com.example.core.ime.InputConnectionDispatcher
import com.example.core.layout.KeyboardLayoutType
import com.example.core.theme.KeyboardMode
import com.example.core.theme.KeyboardPreferences
import com.example.core.theme.LocalNovaColors
import com.example.core.theme.NovaThemePalette
import com.example.core.theme.OneHandedSide
import com.example.core.theme.getColorsForPalette
import com.example.data.db.SmartClipboardManager
import com.example.ui.components.PredictionBar
import com.example.ui.components.UtilityToolbar
import com.example.ui.modes.FloatingKeyboardWindow
import com.example.ui.modes.OneHandedLayout
import com.example.ui.modes.SplitTabletKeyboard
import com.example.ui.panels.ClipboardHistoryPanel
import com.example.ui.panels.EmojiStickerPicker

enum class ActivePanel {
    NONE,
    EMOJI,
    CLIPBOARD
}

@Composable
fun KeyboardRootView(
    dispatcher: InputConnectionDispatcher,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    initialLayout: KeyboardLayoutType = KeyboardLayoutType.ARABIC,
    onVoiceInputRequested: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val prefs = remember { KeyboardPreferences(context) }
    val hapticManager = remember { HapticFeedbackManager(context) }
    val clipboardManager = remember { SmartClipboardManager(context) }

    var currentLayout by remember { mutableStateOf(initialLayout) }
    var previousAlphaLayout by remember { mutableStateOf(initialLayout) }
    var currentMode by remember { mutableStateOf(prefs.keyboardMode) }
    var currentTheme by remember { mutableStateOf(prefs.currentTheme) }
    var oneHandedSide by remember { mutableStateOf(prefs.oneHandedSide) }
    var floatingAlpha by remember { mutableStateOf(prefs.floatingAlpha) }
    var activePanel by remember { mutableStateOf(ActivePanel.NONE) }
    var currentWordForPrediction by remember { mutableStateOf("") }

    val colors = remember(currentTheme) { getColorsForPalette(currentTheme) }

    fun playFeedback() {
        hapticManager.performKeyFeedback(
            isHapticEnabled = prefs.hapticFeedback,
            isAudioEnabled = prefs.audioFeedback
        )
    }

    fun playLongPressFeedback() {
        hapticManager.performLongPressFeedback(isHapticEnabled = prefs.hapticFeedback)
    }

    CompositionLocalProvider(LocalNovaColors provides colors) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(colors.background)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 1. Utility Toolbar (Emojis, Clipboard, Mode Switch, Themes, Language, Hide)
                UtilityToolbar(
                    currentLayout = currentLayout,
                    currentMode = currentMode,
                    onEmojiClick = {
                        activePanel = if (activePanel == ActivePanel.EMOJI) ActivePanel.NONE else ActivePanel.EMOJI
                        playFeedback()
                    },
                    onClipboardClick = {
                        activePanel = if (activePanel == ActivePanel.CLIPBOARD) ActivePanel.NONE else ActivePanel.CLIPBOARD
                        playFeedback()
                    },
                    onModeChange = { newMode ->
                        currentMode = newMode
                        prefs.keyboardMode = newMode
                        playFeedback()
                    },
                    onThemeCycle = {
                        val all = NovaThemePalette.values()
                        val next = all[(currentTheme.ordinal + 1) % all.size]
                        currentTheme = next
                        prefs.currentTheme = next
                        playFeedback()
                    },
                    onLanguageToggle = {
                        if (currentLayout == KeyboardLayoutType.ARABIC) {
                            currentLayout = KeyboardLayoutType.ENGLISH
                            previousAlphaLayout = KeyboardLayoutType.ENGLISH
                        } else {
                            currentLayout = KeyboardLayoutType.ARABIC
                            previousAlphaLayout = KeyboardLayoutType.ARABIC
                        }
                        activePanel = ActivePanel.NONE
                        playFeedback()
                    },
                    onVoiceClick = {
                        onVoiceInputRequested?.invoke()
                        playFeedback()
                    },
                    onDismissRequest = onDismissRequest
                )

                // 2. Active Panel Overlay (Emoji Picker or Clipboard) OR Standard Keyboard Views
                when (activePanel) {
                    ActivePanel.EMOJI -> {
                        EmojiStickerPicker(
                            onEmojiSelected = { emoji ->
                                dispatcher.commitText(emoji)
                                playFeedback()
                            },
                            onClose = { activePanel = ActivePanel.NONE }
                        )
                    }
                    ActivePanel.CLIPBOARD -> {
                        ClipboardHistoryPanel(
                            clipboardManager = clipboardManager,
                            onClipSelected = { text ->
                                dispatcher.commitText(text)
                                activePanel = ActivePanel.NONE
                                playFeedback()
                            },
                            onClose = { activePanel = ActivePanel.NONE }
                        )
                    }
                    ActivePanel.NONE -> {
                        // Prediction Bar
                        PredictionBar(
                            currentWord = currentWordForPrediction,
                            layoutType = currentLayout,
                            onSuggestionSelected = { word ->
                                dispatcher.commitText("$word ")
                                playFeedback()
                            }
                        )

                        // 3. Adaptive Keyboard Container (Full, One-Handed, Floating, Split)
                        val keyHeight = prefs.keyHeightDp.dp

                        val keyboardContent: @Composable () -> Unit = {
                            when (currentLayout) {
                                KeyboardLayoutType.ARABIC -> {
                                    ArabicLayoutView(
                                        dispatcher = dispatcher,
                                        keyHeight = keyHeight,
                                        showHarakatRow = prefs.harakatShortcutRow,
                                        onFeedback = { playFeedback() },
                                        onLongPressFeedback = { playLongPressFeedback() },
                                        onSwitchLayout = { newLayout -> currentLayout = newLayout },
                                        onLanguageToggle = {
                                            currentLayout = KeyboardLayoutType.ENGLISH
                                            previousAlphaLayout = KeyboardLayoutType.ENGLISH
                                        }
                                    )
                                }
                                KeyboardLayoutType.ENGLISH -> {
                                    EnglishLayoutView(
                                        dispatcher = dispatcher,
                                        keyHeight = keyHeight,
                                        onFeedback = { playFeedback() },
                                        onLongPressFeedback = { playLongPressFeedback() },
                                        onSwitchLayout = { newLayout -> currentLayout = newLayout },
                                        onLanguageToggle = {
                                            currentLayout = KeyboardLayoutType.ARABIC
                                            previousAlphaLayout = KeyboardLayoutType.ARABIC
                                        }
                                    )
                                }
                                KeyboardLayoutType.SYMBOLS, KeyboardLayoutType.MORE_SYMBOLS -> {
                                    SymbolsLayoutView(
                                        dispatcher = dispatcher,
                                        keyHeight = keyHeight,
                                        returnLayout = previousAlphaLayout,
                                        onFeedback = { playFeedback() },
                                        onReturnToAlpha = { currentLayout = previousAlphaLayout },
                                        onLanguageToggle = {
                                            val next = if (previousAlphaLayout == KeyboardLayoutType.ARABIC) KeyboardLayoutType.ENGLISH else KeyboardLayoutType.ARABIC
                                            previousAlphaLayout = next
                                            currentLayout = next
                                        }
                                    )
                                }
                            }
                        }

                        when (currentMode) {
                            KeyboardMode.FULL -> {
                                keyboardContent()
                            }
                            KeyboardMode.ONE_HANDED -> {
                                OneHandedLayout(
                                    side = oneHandedSide,
                                    scaleFactor = prefs.oneHandedScale,
                                    onFlipSide = {
                                        oneHandedSide = if (oneHandedSide == OneHandedSide.RIGHT) OneHandedSide.LEFT else OneHandedSide.RIGHT
                                        prefs.oneHandedSide = oneHandedSide
                                    },
                                    onExpandFull = {
                                        currentMode = KeyboardMode.FULL
                                        prefs.keyboardMode = KeyboardMode.FULL
                                    },
                                    content = keyboardContent
                                )
                            }
                            KeyboardMode.FLOATING -> {
                                FloatingKeyboardWindow(
                                    alpha = floatingAlpha,
                                    scale = prefs.floatingScale,
                                    onExpandFull = {
                                        currentMode = KeyboardMode.FULL
                                        prefs.keyboardMode = KeyboardMode.FULL
                                    },
                                    onAlphaChange = { newAlpha ->
                                        floatingAlpha = newAlpha
                                        prefs.floatingAlpha = newAlpha
                                    },
                                    content = keyboardContent
                                )
                            }
                            KeyboardMode.SPLIT -> {
                                SplitTabletKeyboard(
                                    dispatcher = dispatcher,
                                    keyHeight = keyHeight,
                                    onExpandFull = {
                                        currentMode = KeyboardMode.FULL
                                        prefs.keyboardMode = KeyboardMode.FULL
                                    },
                                    onClipboardClick = {
                                        activePanel = ActivePanel.CLIPBOARD
                                        playFeedback()
                                    },
                                    onFeedback = { playFeedback() },
                                    leftDeckContent = keyboardContent,
                                    rightDeckContent = keyboardContent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
