package com.example.ime.ui

import android.view.SoundEffectConstants
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.util.HapticHelper
import com.example.ime.util.KeyboardLayoutController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import com.example.KeyboardProApp
import com.example.engine.DecorationEngine
import com.example.data.local.entity.ClipboardEntity
import com.example.data.local.entity.UserWordEntity
import com.example.ime.ui.panels.AiAssistantPanel
import com.example.ime.layout.KeyboardLayouts
import com.example.ime.layout.KeyModel
import com.example.ime.layout.KeyType
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.ui.components.*
import com.example.ime.ui.panels.*
import com.example.language.layout.KeyboardLayoutData
import com.example.language.layout.KeyboardLayoutManager
import com.example.language.model.LayoutFamily
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.border
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import com.example.ime.util.CharacterVariants

data class KeyCalloutState(
    val key: KeyModel,
    val keyRect: Rect,
    val options: List<String>
)

data class KeyPreviewState(
    val text: String,
    val centerX: Float,
    val topY: Float,
    val keyWidth: Float,
    val keyHeight: Float,
    val visible: Boolean
)

enum class LayoutMode {
    ALPHA,
    SYMBOLS_1,
    SYMBOLS_2,
    NUMPAD
}

enum class ShiftState {
    OFF,
    ON,
    CAPS_LOCK
}

@Composable
fun KeyboardScreen(
    colorScheme: KeyboardColorScheme,
    currentLanguage: String,
    imeOptions: Int,
    isIncognito: Boolean,
    keyboardHeight: String,
    showNumberRow: Boolean,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    oneHandedMode: String,
    suggestions: List<String>,
    clipboardList: List<ClipboardEntity>,
    isVoiceListening: Boolean,
    voiceStatusText: String,
    voicePartialText: String,
    showSuggestions: Boolean = true,
    arabicNumerals: Boolean = true,
    autoTranslateOnEnter: Boolean = false,
    spacebarLanguageSwitch: Boolean = true,
    inputType: Int = android.text.InputType.TYPE_CLASS_TEXT,
    initialLayoutMode: LayoutMode? = null,
    onToggleAutoTranslate: (Boolean) -> Unit = {},
    onChangeKeyboardHeight: (String) -> Unit = {},
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteWord: () -> Unit = {},
    onDeleteAll: () -> Unit = {},
    onEnter: () -> Unit,
    onSpace: () -> Unit,
    onSwitchLanguage: () -> Unit,
    onSelectLanguage: (String) -> Unit = {},
    onMoveCursor: (Int) -> Unit,
    onSelectSuggestion: (String) -> Unit,
    onTogglePinClip: (Long, Boolean) -> Unit,
    onDeleteClip: (Long) -> Unit,
    onClearUnpinnedClips: () -> Unit,
    onStartVoice: () -> Unit,
    onStopVoice: () -> Unit,
    onSelectAll: () -> Unit,
    onCut: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleOneHanded: (String) -> Unit,
    currentTypedWord: String? = null,
    isCurrentWordKnown: Boolean = true,
    userWords: List<UserWordEntity> = emptyList(),
    geminiApiKey: String? = null,
    currentDraftText: String = "",
    onApplyAiText: (String) -> Unit = {},
    onAddWordToDictionary: (String) -> Unit = {},
    onDeleteUserWord: (Long) -> Unit = {},
    onLongPressEnter: (() -> Unit)? = null,
    onTranslateNow: ((String, String) -> Unit)? = null,
    showKeyPreview: Boolean = true,
    bottomChinPadding: String = "AUTO",
    showToolbarUndoRedo: Boolean = true,
    heightPercent: Int = 100,
    widthPercent: Int = 100,
    keyFontSizeSp: Int = 19,
    secondaryFontSizeSp: Int = 9,
    keyCornerRadiusDp: Int = 6,
    keyStrokeBorderEnabled: Boolean = false,
    showArrowRow: Boolean = false,
    soundType: String = "CLICK",
    soundVolume: Int = 50,
    hapticIntensity: String = "Medium",
    hapticDurationMs: Int = 20,
    applyNavigationBarsPadding: Boolean = true,
    onSwitchIme: (() -> Unit)? = null,
    onSwitchPreviousLanguage: (() -> Unit)? = null,
    onLaunchVoiceActivity: () -> Unit = {},
    onChangeKeyboardHeightPercent: (Int) -> Unit = {},
    onChangeKeyboardWidthPercent: (Int) -> Unit = {},
    onChangeKeyFontSize: (Int) -> Unit = {},
    onChangeSecondaryFontSize: (Int) -> Unit = {},
    onHideKeyboard: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    onCommitGif: ((com.example.engine.GifItem) -> Unit)? = null,
    onRegisterBackHandler: (((() -> Boolean)) -> Unit)? = null
) {
    val defaultMode = remember(inputType, initialLayoutMode) {
        if (initialLayoutMode != null) {
            initialLayoutMode
        } else {
            val inputClass = inputType and android.text.InputType.TYPE_MASK_CLASS
            if (inputClass == android.text.InputType.TYPE_CLASS_NUMBER ||
                inputClass == android.text.InputType.TYPE_CLASS_PHONE ||
                inputClass == android.text.InputType.TYPE_CLASS_DATETIME
            ) {
                LayoutMode.NUMPAD
            } else {
                LayoutMode.ALPHA
            }
        }
    }

    var layoutMode by remember { mutableStateOf(initialLayoutMode ?: defaultMode) }
    var lastHandledInputClass by remember { mutableStateOf(inputType and android.text.InputType.TYPE_MASK_CLASS) }
    var isNumberRowVisible by remember(showNumberRow) { mutableStateOf(showNumberRow) }

    LaunchedEffect(inputType) {
        val newClass = inputType and android.text.InputType.TYPE_MASK_CLASS
        if (newClass != lastHandledInputClass) {
            lastHandledInputClass = newClass
            if (newClass == android.text.InputType.TYPE_CLASS_NUMBER ||
                newClass == android.text.InputType.TYPE_CLASS_PHONE ||
                newClass == android.text.InputType.TYPE_CLASS_DATETIME
            ) {
                layoutMode = LayoutMode.NUMPAD
            } else {
                layoutMode = LayoutMode.ALPHA
            }
        }
    }

    var shiftState by remember { mutableStateOf(ShiftState.OFF) }
    var activePanel by remember { mutableStateOf(KeyboardPanel.NONE) }
    val panelStack = remember { mutableStateListOf<KeyboardPanel>() }

    val navigateToPanel: (KeyboardPanel) -> Unit = { panel ->
        if (activePanel != KeyboardPanel.NONE && activePanel != panel) {
            panelStack.add(activePanel)
        }
        activePanel = panel
    }

    val closeCurrentPanel: () -> Unit = {
        if (panelStack.isNotEmpty()) {
            activePanel = panelStack.removeAt(panelStack.lastIndex)
        } else {
            activePanel = KeyboardPanel.NONE
        }
    }

    var showTashkeelRow by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }
    var activeCalloutState by remember { mutableStateOf<KeyCalloutState?>(null) }
    var keyboardContainerCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var isInlineTranslateOpen by remember { mutableStateOf(false) }
    var showTermuxKeys by remember { mutableStateOf(false) }
    var showQuickSnippets by remember { mutableStateOf(false) }
    var translateSourceLang by remember { mutableStateOf("ar") }
    var translateTargetLang by remember { mutableStateOf("en") }
    val coroutineScope = rememberCoroutineScope()

    val handleBackAction: () -> Boolean = {
        when {
            activeCalloutState != null -> {
                activeCalloutState = null
                true
            }
            showLanguagePicker -> {
                showLanguagePicker = false
                true
            }
            isInlineTranslateOpen -> {
                isInlineTranslateOpen = false
                true
            }
            showTermuxKeys -> {
                showTermuxKeys = false
                true
            }
            showQuickSnippets -> {
                showQuickSnippets = false
                true
            }
            showTashkeelRow -> {
                showTashkeelRow = false
                true
            }
            panelStack.isNotEmpty() -> {
                activePanel = panelStack.removeAt(panelStack.lastIndex)
                true
            }
            activePanel != KeyboardPanel.NONE -> {
                activePanel = KeyboardPanel.NONE
                true
            }
            layoutMode != LayoutMode.ALPHA -> {
                layoutMode = LayoutMode.ALPHA
                true
            }
            else -> false
        }
    }

    BackHandler(
        enabled = (activeCalloutState != null ||
                showLanguagePicker ||
                isInlineTranslateOpen ||
                showTermuxKeys ||
                showQuickSnippets ||
                showTashkeelRow ||
                activePanel != KeyboardPanel.NONE ||
                panelStack.isNotEmpty() ||
                layoutMode != LayoutMode.ALPHA)
    ) {
        handleBackAction()
    }

    LaunchedEffect(handleBackAction) {
        onRegisterBackHandler?.invoke(handleBackAction)
    }

    val keyHeight = KeyboardLayoutController.getKeyHeight(keyboardHeight, heightPercent)
    val keyFontSize = KeyboardLayoutController.getKeyFontSize("Medium", keyFontSizeSp)
    val secondaryFontSize = KeyboardLayoutController.getSecondaryFontSize(secondaryFontSizeSp)
    val keyCornerRadius = keyCornerRadiusDp.dp
    val panelHeight = remember(keyboardHeight, isNumberRowVisible, heightPercent) {
        KeyboardLayoutController.getPanelHeight(keyboardHeight, isNumberRowVisible, heightPercent)
    }

    val actionIcon = remember(imeOptions) {
        val action = imeOptions and EditorInfo.IME_MASK_ACTION
        when (action) {
            EditorInfo.IME_ACTION_SEARCH -> Icons.Default.Search
            EditorInfo.IME_ACTION_SEND -> Icons.Default.Send
            EditorInfo.IME_ACTION_GO -> Icons.Default.ArrowForward
            EditorInfo.IME_ACTION_DONE -> Icons.Default.Check
            EditorInfo.IME_ACTION_NEXT -> Icons.Default.ArrowDownward
            else -> Icons.Default.KeyboardReturn
        }
    }

    val handleLongPressKey: (KeyModel, LayoutCoordinates?) -> Unit = remember(shiftState) {
        { key, coords ->
            val root = keyboardContainerCoordinates
            val isUpper = shiftState != ShiftState.OFF
            val effectiveChar = if (isUpper && key.primaryText.length == 1 && key.primaryText[0].isLetter()) {
                key.primaryText.uppercase()
            } else {
                key.primaryText
            }
            val effectiveKey = key.copy(primaryText = effectiveChar)
            val variants = CharacterVariants.getVariants(effectiveChar, key.popupOptions)
            val decorations = DecorationEngine.getLetterDecorations(effectiveChar)
            val allOptions = (variants + decorations + listOfNotNull(key.secondaryText)).distinct().filter { it.isNotBlank() }

            if (root != null && coords != null && coords.isAttached && root.isAttached) {
                val offset = root.localPositionOf(coords, Offset.Zero)
                val size = coords.size
                val rect = Rect(offset, Size(size.width.toFloat(), size.height.toFloat()))
                activeCalloutState = KeyCalloutState(effectiveKey, rect, allOptions)
            } else {
                val fallbackRect = Rect(Offset(200f, 300f), Size(100f, 100f))
                activeCalloutState = KeyCalloutState(effectiveKey, fallbackRect, allOptions)
            }
        }
    }

    val activeKeyPreviewState = remember { mutableStateOf<KeyPreviewState?>(null) }
    val handleKeyPreview: (String, LayoutCoordinates?, Boolean) -> Unit = remember(showKeyPreview) {
        { text, coords, isDown ->
            if (!showKeyPreview || text.isBlank()) {
                if (activeKeyPreviewState.value?.visible == true) {
                    activeKeyPreviewState.value = activeKeyPreviewState.value?.copy(visible = false)
                }
            } else if (isDown && coords != null && coords.isAttached) {
                val root = keyboardContainerCoordinates
                if (root != null && root.isAttached) {
                    val pos = root.localPositionOf(coords, Offset.Zero)
                    val size = coords.size
                    activeKeyPreviewState.value = KeyPreviewState(
                        text = text,
                        centerX = pos.x + size.width / 2f,
                        topY = pos.y,
                        keyWidth = size.width.toFloat(),
                        keyHeight = size.height.toFloat(),
                        visible = true
                    )
                }
            } else {
                if (activeKeyPreviewState.value?.visible == true) {
                    activeKeyPreviewState.value = activeKeyPreviewState.value?.copy(visible = false)
                }
            }
        }
    }

    val onSwitchToSymbols1 = remember { { layoutMode = LayoutMode.SYMBOLS_1 } }
    val onSwitchToAlpha = remember { { layoutMode = LayoutMode.ALPHA } }
    val onSwitchToSymbols2 = remember { { layoutMode = LayoutMode.SYMBOLS_2 } }
    val onSwitchToNumpad = remember { { layoutMode = LayoutMode.NUMPAD } }
    val onShowLanguagePicker = remember { { showLanguagePicker = true } }
    val onOpenClipboardPanel = remember { { activePanel = KeyboardPanel.CLIPBOARD } }
    val onOpenEmojiPanel = remember { { activePanel = KeyboardPanel.EMOJI } }
    val onToggleInlineTranslate = remember { { isInlineTranslateOpen = !isInlineTranslateOpen } }
    val onToggleTashkeelRow = remember { { showTashkeelRow = !showTashkeelRow } }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.Bottom)
                .background(colorScheme.background)
                .then(if (applyNavigationBarsPadding) Modifier.navigationBarsPadding() else Modifier)
        ) {
            // 0. News & Trend Ticker Bar
            if (activePanel == KeyboardPanel.NONE) {
                KeyboardTickerBar(
                    colorScheme = colorScheme,
                    onOpenMenu = { navigateToPanel(KeyboardPanel.MENU) },
                    onHeadlineClick = { headline -> onTextInput(headline) }
                )
            }

            // 1. Toolbar
            KeyboardToolbar(
                activePanel = activePanel,
                currentLanguage = currentLanguage,
                isIncognito = isIncognito,
                autoTranslateOnEnter = autoTranslateOnEnter,
                oneHandedMode = oneHandedMode,
                showTermuxKeys = showTermuxKeys,
                showQuickSnippets = showQuickSnippets,
                showUndoRedo = showToolbarUndoRedo,
                colorScheme = colorScheme,
                onPanelSelect = { panel ->
                    if (panel == KeyboardPanel.TRANSLATE) {
                        isInlineTranslateOpen = !isInlineTranslateOpen
                        activePanel = KeyboardPanel.NONE
                    } else if (activePanel == panel) {
                        closeCurrentPanel()
                    } else {
                        navigateToPanel(panel)
                    }
                },
                onSwitchLanguage = onSwitchLanguage,
                onOpenSettings = onOpenSettings,
                onOpenThemes = { navigateToPanel(KeyboardPanel.THEMES) },
                onUndo = onUndo,
                onRedo = onRedo,
                onDelete = onDelete,
                onSearch = onSearch,
                onToggleOneHanded = onToggleOneHanded,
                onToggleTermuxKeys = { showTermuxKeys = !showTermuxKeys },
                onToggleQuickSnippets = { showQuickSnippets = !showQuickSnippets }
            )

            // 1.5 Inline Translation Bar
            if (isInlineTranslateOpen && activePanel == KeyboardPanel.NONE) {
                InlineTranslateBar(
                    sourceLang = translateSourceLang,
                    targetLang = translateTargetLang,
                    colorScheme = colorScheme,
                    onSourceLangChange = { translateSourceLang = it },
                    onTargetLangChange = { translateTargetLang = it },
                    onSwapLanguages = {
                        val temp = translateSourceLang
                        translateSourceLang = translateTargetLang
                        translateTargetLang = temp
                    },
                    onTranslateNow = {
                        if (onTranslateNow != null) {
                            onTranslateNow(translateSourceLang, translateTargetLang)
                        } else {
                            val textToTranslate = currentDraftText.ifBlank { currentTypedWord ?: "" }
                            if (textToTranslate.isNotBlank()) {
                                coroutineScope.launch {
                                    val translated = com.example.engine.TranslationEngine.translateAsync(
                                        textToTranslate,
                                        translateSourceLang,
                                        translateTargetLang
                                    )
                                    onTextInput(translated)
                                }
                            }
                        }
                    },
                    onClose = { isInlineTranslateOpen = false }
                )
            }

            // 2. Suggestion Bar
            if (activePanel == KeyboardPanel.NONE && showSuggestions) {
                val latestClipText = remember(clipboardList) { clipboardList.firstOrNull()?.text }
                SuggestionBar(
                    suggestions = suggestions,
                    latestClip = latestClipText,
                    currentTypedWord = currentTypedWord,
                    isCurrentWordKnown = isCurrentWordKnown,
                    colorScheme = colorScheme,
                    onSelectSuggestion = onSelectSuggestion,
                    onPasteClip = { clipText -> onTextInput(clipText) },
                    onAddWordToDictionary = onAddWordToDictionary
                )
            }

            // 3. Main Area: Panels or Keys
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .onGloballyPositioned { keyboardContainerCoordinates = it }
            ) {
                when (activePanel) {
                    KeyboardPanel.THEMES -> {
                        ThemePickerPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            colorScheme = colorScheme,
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.RESIZE -> {
                        ResizePanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            currentHeight = keyboardHeight,
                            heightPercent = heightPercent,
                            widthPercent = widthPercent,
                            keyFontSizeSp = keyFontSizeSp,
                            secondaryFontSizeSp = secondaryFontSizeSp,
                            colorScheme = colorScheme,
                            onSelectHeight = { newH -> onChangeKeyboardHeight(newH) },
                            onChangeHeightPercent = onChangeKeyboardHeightPercent,
                            onChangeWidthPercent = onChangeKeyboardWidthPercent,
                            onChangeKeyFontSize = onChangeKeyFontSize,
                            onChangeSecondaryFontSize = onChangeSecondaryFontSize,
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.CLIPBOARD -> {
                        ClipboardPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            clips = clipboardList,
                            colorScheme = colorScheme,
                            onClipClick = { text ->
                                onTextInput(text)
                                closeCurrentPanel()
                            },
                            onTogglePin = onTogglePinClip,
                            onDeleteClip = onDeleteClip,
                            onClearUnpinned = onClearUnpinnedClips,
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.EMOJI -> {
                        EmojiPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            colorScheme = colorScheme,
                            onEmojiClick = { emoji -> onTextInput(emoji) },
                            onBackspace = onDelete,
                            onSwitchToGifs = { navigateToPanel(KeyboardPanel.GIFS) },
                            onSwitchToStickers = { navigateToPanel(KeyboardPanel.STICKERS) },
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.STICKERS -> {
                        StickersPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            colorScheme = colorScheme,
                            onStickerClick = { text ->
                                onTextInput(text)
                                closeCurrentPanel()
                            },
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.GIFS -> {
                        GifsPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            colorScheme = colorScheme,
                            onGifSelect = { gifItem ->
                                if (onCommitGif != null) {
                                    onCommitGif(gifItem)
                                } else {
                                    onTextInput(gifItem.gifUrl)
                                }
                                closeCurrentPanel()
                            },
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.DECORATIONS -> {
                        DecorationsPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            initialText = suggestions.firstOrNull() ?: "",
                            colorScheme = colorScheme,
                            onInsertText = { decorated ->
                                onTextInput(decorated)
                                closeCurrentPanel()
                            },
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.VOICE -> {
                        VoicePanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            isListening = isVoiceListening,
                            statusText = voiceStatusText,
                            partialResult = voicePartialText,
                            colorScheme = colorScheme,
                            onStartListening = onStartVoice,
                            onStopListening = onStopVoice,
                            onLaunchSystemVoice = onLaunchVoiceActivity,
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.TRANSLATE -> {
                        TranslatePanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            initialText = suggestions.firstOrNull() ?: "",
                            autoTranslateOnEnter = autoTranslateOnEnter,
                            colorScheme = colorScheme,
                            onToggleAutoTranslate = onToggleAutoTranslate,
                            onCommitTranslation = { translated ->
                                onTextInput(translated)
                                closeCurrentPanel()
                            },
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.DICTIONARY -> {
                        DictionaryPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            initialWord = suggestions.firstOrNull() ?: "",
                            colorScheme = colorScheme,
                            onReplaceWord = { word ->
                                onTextInput(word)
                                closeCurrentPanel()
                            },
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.EDITING -> {
                        TextEditingPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            colorScheme = colorScheme,
                            onMoveCursor = onMoveCursor,
                            onSelectAll = onSelectAll,
                            onCut = onCut,
                            onCopy = onCopy,
                            onPaste = onPaste,
                            onUndo = onUndo,
                            onRedo = onRedo,
                            onHome = { onMoveCursor(-999) },
                            onEnd = { onMoveCursor(999) },
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.AI_ASSISTANT -> {
                        AiAssistantPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            initialText = currentDraftText.ifBlank { suggestions.firstOrNull() ?: "" },
                            apiKey = geminiApiKey,
                            userWords = userWords,
                            colorScheme = colorScheme,
                            onApplyText = { text ->
                                onApplyAiText(text)
                                closeCurrentPanel()
                            },
                            onAddWordToDictionary = onAddWordToDictionary,
                            onDeleteUserWord = onDeleteUserWord,
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.MENU -> {
                        KeyboardMenuPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            colorScheme = colorScheme,
                            onOpenThemes = { navigateToPanel(KeyboardPanel.THEMES) },
                            onOpenVoice = { navigateToPanel(KeyboardPanel.VOICE) },
                            onOpenMiniGame = { navigateToPanel(KeyboardPanel.GAME) },
                            onOpenTranslate = {
                                isInlineTranslateOpen = true
                                closeCurrentPanel()
                            },
                            onOpenQuickText = { navigateToPanel(KeyboardPanel.CLIPBOARD) },
                            onOpenTextEditing = { navigateToPanel(KeyboardPanel.EDITING) },
                            onOpenCalculator = { navigateToPanel(KeyboardPanel.CALCULATOR) },
                            onOpenNotes = { navigateToPanel(KeyboardPanel.NOTES) },
                            onToggleOneHanded = {
                                val nextMode = when (oneHandedMode) {
                                    "OFF" -> "RIGHT"
                                    "RIGHT" -> "LEFT"
                                    else -> "OFF"
                                }
                                onToggleOneHanded(nextMode)
                                closeCurrentPanel()
                            },
                            onOpenNews = {
                                onTextInput("أخبار اليوم: تحديثات كيبورد محمد v.1 مع كامل الميزات والسرعة الفائقة ⚡")
                                closeCurrentPanel()
                            },
                            onOpenFonts = { navigateToPanel(KeyboardPanel.DECORATIONS) },
                            onOpenAi = { navigateToPanel(KeyboardPanel.AI_ASSISTANT) },
                            onOpenHandwriting = { navigateToPanel(KeyboardPanel.HANDWRITING) },
                            onOpenInstaFonts = { navigateToPanel(KeyboardPanel.INSTA_FONTS) },
                            onToggleTashkeel = {
                                showTashkeelRow = !showTashkeelRow
                                closeCurrentPanel()
                            },
                            onOpenResize = { navigateToPanel(KeyboardPanel.RESIZE) },
                            onOpenNumpad = {
                                layoutMode = LayoutMode.NUMPAD
                                closeCurrentPanel()
                            },
                            onToggleNumberRow = {
                                isNumberRowVisible = !isNumberRowVisible
                                com.example.KeyboardProApp.instance.preferences.showNumberRow = isNumberRowVisible
                                closeCurrentPanel()
                            },
                            onOpenSettings = onOpenSettings,
                            onOpenToolbarEditor = { navigateToPanel(KeyboardPanel.TOOLBAR_EDITOR) },
                            onSwitchIme = onSwitchIme,
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.TOOLBAR_EDITOR -> {
                        ToolbarEditorPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            colorScheme = colorScheme,
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.GAME -> {
                        MiniGamePanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            colorScheme = colorScheme,
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.CALCULATOR -> {
                        CalculatorPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            colorScheme = colorScheme,
                            onInsertResult = { res ->
                                onTextInput(res)
                                closeCurrentPanel()
                            },
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.NOTES -> {
                        NotesPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            colorScheme = colorScheme,
                            onInsertNote = { note ->
                                onTextInput(note)
                                closeCurrentPanel()
                            },
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.HANDWRITING -> {
                        HandwritingPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            colorScheme = colorScheme,
                            onTextInput = { text ->
                                onTextInput(text)
                                closeCurrentPanel()
                            },
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.INSTA_FONTS -> {
                        InstaFontPanel(
                            modifier = Modifier.fillMaxWidth().height(panelHeight),
                            draftText = currentDraftText,
                            colorScheme = colorScheme,
                            onInsertText = { text ->
                                onTextInput(text)
                                closeCurrentPanel()
                            },
                            onClose = closeCurrentPanel
                        )
                    }
                    KeyboardPanel.NEWS -> {
                        onTextInput("أخبار اليوم: تقنيات الذكاء الاصطناعي في لوحة المفاتيح.")
                        closeCurrentPanel()
                    }
                    KeyboardPanel.NONE -> {
                        Row(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                            if (oneHandedMode == "RIGHT") {
                                OneHandedSidebar(
                                    colorScheme = colorScheme,
                                    isLeft = true,
                                    onSwitchSide = { onToggleOneHanded("LEFT") },
                                    onExpand = { onToggleOneHanded("OFF") }
                                )
                            }
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 2.dp, vertical = 2.dp)
                                ) {
                                    if (currentLanguage.startsWith("ar") && showTashkeelRow && layoutMode == LayoutMode.ALPHA) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(colorScheme.specialKeyBackground.copy(alpha = 0.5f))
                                                .padding(horizontal = 2.dp, vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            for (tashkeel in KeyboardLayouts.arabicTashkeel) {
                                                KeyButton(
                                                    text = tashkeel,
                                                    isSpecial = true,
                                                    height = 36.dp,
                                                    fontSize = 20.sp,
                                                    colorScheme = colorScheme,
                                                    hapticEnabled = hapticEnabled,
                                                    soundEnabled = soundEnabled,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    onTextInput(tashkeel)
                                                }
                                            }
                                        }
                                    }

                                    if (isNumberRowVisible && layoutMode == LayoutMode.ALPHA) {
                                        val numRow = if (currentLanguage == "ar" && arabicNumerals) KeyboardLayouts.arabicNumbersRow else KeyboardLayouts.englishNumbersRow
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            for (numKey in numRow) {
                                                KeyButton(
                                                    text = numKey.primaryText,
                                                    secondaryText = numKey.secondaryText,
                                                    isSpecial = true,
                                                    height = 36.dp,
                                                    fontSize = 15.sp,
                                                    colorScheme = colorScheme,
                                                    hapticEnabled = hapticEnabled,
                                                    soundEnabled = soundEnabled,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    onTextInput(numKey.primaryText)
                                                }
                                            }
                                        }
                                    }

                                    if (showQuickSnippets && layoutMode == LayoutMode.ALPHA) {
                                        QuickSnippetsBar(
                                            colorScheme = colorScheme,
                                            hapticEnabled = hapticEnabled,
                                            onInsertText = onTextInput
                                        )
                                    }

                                    if (showTermuxKeys) {
                                        TermuxKeysRow(
                                            colorScheme = colorScheme,
                                            hapticEnabled = hapticEnabled,
                                            onTextInput = onTextInput,
                                            onMoveCursor = onMoveCursor,
                                            onHome = { onMoveCursor(-999) },
                                            onEnd = { onMoveCursor(999) },
                                            onTab = { onTextInput("\t") },
                                            onEsc = { }
                                        )
                                    }

                                    if (showArrowRow && layoutMode == LayoutMode.ALPHA) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 2.dp, vertical = 1.dp),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            val arrowButtons = listOf(
                                                "⏮" to { onMoveCursor(-999) },
                                                "◀" to { onMoveCursor(-1) },
                                                "▶" to { onMoveCursor(1) },
                                                "⏭" to { onMoveCursor(999) },
                                                "📋" to onOpenClipboardPanel,
                                                "⌫" to onDelete
                                            )
                                            for ((label, action) in arrowButtons) {
                                                KeyButton(
                                                    text = label,
                                                    isSpecial = true,
                                                    height = (keyHeight * 0.72f).coerceAtLeast(32.dp),
                                                    fontSize = 14.sp,
                                                    cornerRadius = keyCornerRadius,
                                                    strokeBorder = keyStrokeBorderEnabled,
                                                    colorScheme = colorScheme,
                                                    hapticEnabled = hapticEnabled,
                                                    hapticIntensity = hapticIntensity,
                                                    hapticDurationMs = hapticDurationMs,
                                                    soundEnabled = soundEnabled,
                                                    soundType = soundType,
                                                    soundVolume = soundVolume,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    action()
                                                }
                                            }
                                        }
                                    }

                                    when (layoutMode) {
                                        LayoutMode.ALPHA -> {
                                            val isArabic = currentLanguage.startsWith("ar")
                                            if (isArabic) {
                                                val arabicSpaceLabel = remember(currentLanguage) {
                                                    try {
                                                        val langInfo = KeyboardProApp.instance.languageManager.getLanguageInfo(currentLanguage)
                                                        langInfo?.nameArabic ?: "العربية"
                                                    } catch (e: Throwable) {
                                                        "العربية"
                                                    }
                                                }
                                                ArabicKeyboardLayout(
                                                    colorScheme = colorScheme,
                                                    keyHeight = keyHeight,
                                                    keyFontSize = keyFontSize,
                                                    secondaryFontSize = secondaryFontSize,
                                                    keyCornerRadius = keyCornerRadius,
                                                    keyStrokeBorder = keyStrokeBorderEnabled,
                                                    hapticEnabled = hapticEnabled,
                                                    hapticIntensity = hapticIntensity,
                                                    hapticDurationMs = hapticDurationMs,
                                                    soundEnabled = soundEnabled,
                                                    soundType = soundType,
                                                    soundVolume = soundVolume,
                                                    actionIcon = actionIcon,
                                                    autoTranslateOnEnter = autoTranslateOnEnter,
                                                    onToggleAutoTranslate = onToggleAutoTranslate,
                                                    spacebarLanguageSwitch = spacebarLanguageSwitch,
                                                    arabicNumerals = arabicNumerals,
                                                    currentLanguage = currentLanguage,
                                                    onTextInput = onTextInput,
                                                    onDelete = onDelete,
                                                    onDeleteWord = onDeleteWord,
                                                    onDeleteAll = onDeleteAll,
                                                    onSpace = onSpace,
                                                    onEnter = onEnter,
                                                    onLongPressEnter = onLongPressEnter,
                                                    onSwitchMode = onSwitchToSymbols1,
                                                    onSwitchLanguage = onSwitchLanguage,
                                                    onSwitchPreviousLanguage = onSwitchPreviousLanguage ?: onSwitchLanguage,
                                                    onLongPressLanguage = onShowLanguagePicker,
                                                    onOpenClipboard = onOpenClipboardPanel,
                                                    onOpenEmoji = onOpenEmojiPanel,
                                                    onOpenTranslate = onToggleInlineTranslate,
                                                    onToggleTashkeel = onToggleTashkeelRow,
                                                    onMoveCursor = onMoveCursor,
                                                    onLongPressKey = handleLongPressKey,
                                                    showKeyPreview = showKeyPreview,
                                                    onPreviewChange = handleKeyPreview,
                                                    spaceLabel = arabicSpaceLabel
                                                )
                                            } else {
                                                val layoutData = remember(currentLanguage) {
                                                    try {
                                                        KeyboardProApp.instance.languageManager.getCurrentLayout()
                                                    } catch (e: Throwable) {
                                                        KeyboardLayoutManager.getLayout("en", LayoutFamily.QWERTY)
                                                    }
                                                }
                                                DynamicKeyboardLayout(
                                                    layoutData = layoutData,
                                                    colorScheme = colorScheme,
                                                    keyHeight = keyHeight,
                                                    keyFontSize = keyFontSize,
                                                    secondaryFontSize = secondaryFontSize,
                                                    keyCornerRadius = keyCornerRadius,
                                                    keyStrokeBorder = keyStrokeBorderEnabled,
                                                    hapticIntensity = hapticIntensity,
                                                    hapticDurationMs = hapticDurationMs,
                                                    soundType = soundType,
                                                    soundVolume = soundVolume,
                                                    currentLanguage = currentLanguage,
                                                    shiftState = shiftState,
                                                    hapticEnabled = hapticEnabled,
                                                    soundEnabled = soundEnabled,
                                                    actionIcon = actionIcon,
                                                    autoTranslateOnEnter = autoTranslateOnEnter,
                                                    onToggleAutoTranslate = onToggleAutoTranslate,
                                                    spacebarLanguageSwitch = spacebarLanguageSwitch,
                                                    onTextInput = { char ->
                                                        val text = if (shiftState != ShiftState.OFF) char.uppercase() else char.lowercase()
                                                        onTextInput(text)
                                                        if (shiftState == ShiftState.ON) {
                                                            shiftState = ShiftState.OFF
                                                        }
                                                    },
                                                    onDelete = onDelete,
                                                    onDeleteWord = onDeleteWord,
                                                    onDeleteAll = onDeleteAll,
                                                    onSpace = onSpace,
                                                    onEnter = onEnter,
                                                    onLongPressEnter = onLongPressEnter,
                                                    onOpenClipboard = onOpenClipboardPanel,
                                                    onOpenTranslate = onToggleInlineTranslate,
                                                    onOpenEmoji = onOpenEmojiPanel,
                                                    onShiftClick = {
                                                        shiftState = when (shiftState) {
                                                            ShiftState.OFF -> ShiftState.ON
                                                            ShiftState.ON -> ShiftState.CAPS_LOCK
                                                            ShiftState.CAPS_LOCK -> ShiftState.OFF
                                                        }
                                                    },
                                                    onSwitchMode = onSwitchToSymbols1,
                                                    onSwitchLanguage = onSwitchLanguage,
                                                    onSwitchPreviousLanguage = onSwitchPreviousLanguage ?: onSwitchLanguage,
                                                    onLongPressLanguage = onShowLanguagePicker,
                                                    onMoveCursor = onMoveCursor,
                                                    onLongPressKey = handleLongPressKey,
                                                    showKeyPreview = showKeyPreview,
                                                    onPreviewChange = handleKeyPreview
                                                )
                                            }
                                        }
                                        LayoutMode.SYMBOLS_1 -> {
                                            Symbols1Layout(
                                                colorScheme = colorScheme,
                                                keyHeight = keyHeight,
                                                keyCornerRadius = keyCornerRadius,
                                                keyStrokeBorder = keyStrokeBorderEnabled,
                                                soundType = soundType,
                                                soundVolume = soundVolume,
                                                hapticIntensity = hapticIntensity,
                                                hapticDurationMs = hapticDurationMs,
                                                currentLanguage = currentLanguage,
                                                arabicNumerals = arabicNumerals,
                                                hapticEnabled = hapticEnabled,
                                                soundEnabled = soundEnabled,
                                                actionIcon = actionIcon,
                                                autoTranslateOnEnter = autoTranslateOnEnter,
                                                onToggleAutoTranslate = onToggleAutoTranslate,
                                                spacebarLanguageSwitch = spacebarLanguageSwitch,
                                                onTextInput = onTextInput,
                                                onDelete = onDelete,
                                                onDeleteWord = onDeleteWord,
                                                onDeleteAll = onDeleteAll,
                                                onSpace = onSpace,
                                                onEnter = onEnter,
                                                onLongPressEnter = onLongPressEnter,
                                                onSwitchToAlpha = onSwitchToAlpha,
                                                onSwitchToSymbols2 = onSwitchToSymbols2,
                                                onSwitchToNumpad = onSwitchToNumpad,
                                                onOpenClipboard = onOpenClipboardPanel,
                                                onOpenTranslate = onToggleInlineTranslate,
                                                onOpenEmoji = onOpenEmojiPanel,
                                                onSwitchLanguage = onSwitchLanguage,
                                                onSwitchPreviousLanguage = onSwitchPreviousLanguage ?: onSwitchLanguage,
                                                onLongPressLanguage = onShowLanguagePicker,
                                                onMoveCursor = onMoveCursor,
                                                onPreviewChange = handleKeyPreview
                                            )
                                        }
                                        LayoutMode.SYMBOLS_2 -> {
                                            Symbols2Layout(
                                                colorScheme = colorScheme,
                                                keyHeight = keyHeight,
                                                keyCornerRadius = keyCornerRadius,
                                                keyStrokeBorder = keyStrokeBorderEnabled,
                                                soundType = soundType,
                                                soundVolume = soundVolume,
                                                hapticIntensity = hapticIntensity,
                                                hapticDurationMs = hapticDurationMs,
                                                currentLanguage = currentLanguage,
                                                arabicNumerals = arabicNumerals,
                                                hapticEnabled = hapticEnabled,
                                                soundEnabled = soundEnabled,
                                                actionIcon = actionIcon,
                                                autoTranslateOnEnter = autoTranslateOnEnter,
                                                onToggleAutoTranslate = onToggleAutoTranslate,
                                                spacebarLanguageSwitch = spacebarLanguageSwitch,
                                                onTextInput = onTextInput,
                                                onDelete = onDelete,
                                                onDeleteWord = onDeleteWord,
                                                onDeleteAll = onDeleteAll,
                                                onSpace = onSpace,
                                                onEnter = onEnter,
                                                onLongPressEnter = onLongPressEnter,
                                                onSwitchToAlpha = onSwitchToAlpha,
                                                onSwitchToSymbols1 = onSwitchToSymbols1,
                                                onOpenClipboard = onOpenClipboardPanel,
                                                onOpenTranslate = onToggleInlineTranslate,
                                                onOpenEmoji = onOpenEmojiPanel,
                                                onSwitchLanguage = onSwitchLanguage,
                                                onSwitchPreviousLanguage = onSwitchPreviousLanguage ?: onSwitchLanguage,
                                                onLongPressLanguage = onShowLanguagePicker,
                                                onMoveCursor = onMoveCursor,
                                                onPreviewChange = handleKeyPreview
                                            )
                                        }
                                        LayoutMode.NUMPAD -> {
                                            NumpadKeyboardLayout(
                                                colorScheme = colorScheme,
                                                currentLanguage = currentLanguage,
                                                keyHeight = keyHeight,
                                                keyCornerRadius = keyCornerRadius,
                                                keyStrokeBorder = keyStrokeBorderEnabled,
                                                soundType = soundType,
                                                soundVolume = soundVolume,
                                                hapticIntensity = hapticIntensity,
                                                hapticDurationMs = hapticDurationMs,
                                                hapticEnabled = hapticEnabled,
                                                soundEnabled = soundEnabled,
                                                actionIcon = actionIcon,
                                                onTextInput = onTextInput,
                                                onDelete = onDelete,
                                                onDeleteWord = onDeleteWord,
                                                onDeleteAll = onDeleteAll,
                                                onEnter = onEnter,
                                                onLongPressEnter = onLongPressEnter,
                                                onSwitchToAlpha = onSwitchToAlpha,
                                                onSwitchToSymbols = onSwitchToSymbols1,
                                                onPreviewChange = handleKeyPreview
                                            )
                                        }
                                    }
                                }
                            }
                            if (oneHandedMode == "LEFT") {
                                OneHandedSidebar(
                                    colorScheme = colorScheme,
                                    isLeft = false,
                                    onSwitchSide = { onToggleOneHanded("RIGHT") },
                                    onExpand = { onToggleOneHanded("OFF") }
                                )
                            }
                        }
                    }
                }

                if (activeCalloutState != null) {
                    val callout = activeCalloutState!!
                    val options = callout.options
                    val density = LocalDensity.current
                    val context = LocalContext.current
                    val view = LocalView.current
                    val rootWidthPx = keyboardContainerCoordinates?.size?.width?.toFloat() ?: 1080f
                    val itemWidthDp = 42.dp
                    val itemHeightDp = 46.dp
                    val itemSpacingDp = 4.dp
                    val horizontalPaddingDp = 6.dp
                    val verticalPaddingDp = 6.dp
                    val maxVisibleItems = 7
                    val visibleCount = options.size.coerceAtMost(maxVisibleItems)
                    val isScrollable = options.size > maxVisibleItems

                    val totalWidthDp = (itemWidthDp * visibleCount) + (itemSpacingDp * (visibleCount - 1).coerceAtLeast(0)) + (horizontalPaddingDp * 2)
                    val totalWidthPx = with(density) { totalWidthDp.toPx() }
                    val calloutHeightPx = with(density) { itemHeightDp.toPx() + (verticalPaddingDp.toPx() * 2) }

                    val keyCenterX = callout.keyRect.center.x
                    val minMarginPx = with(density) { 6.dp.toPx() }
                    val rawLeftPx = keyCenterX - (totalWidthPx / 2f)
                    val clampedLeftPx = rawLeftPx.coerceIn(minMarginPx, (rootWidthPx - totalWidthPx - minMarginPx).coerceAtLeast(minMarginPx))

                    val gapPx = with(density) { 6.dp.toPx() }
                    val targetTopPx = (callout.keyRect.top - calloutHeightPx - gapPx).coerceAtLeast(with(density) { 2.dp.toPx() })

                    val offsetX = with(density) { clampedLeftPx.toDp() }
                    val offsetY = with(density) { targetTopPx.toDp() }

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { activeCalloutState = null }
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(x = offsetX, y = offsetY)
                                .shadow(8.dp, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .background(colorScheme.background)
                                .border(1.dp, colorScheme.accent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable(enabled = false) {}
                                .padding(horizontal = horizontalPaddingDp, vertical = verticalPaddingDp)
                                .then(
                                    if (isScrollable) Modifier.widthIn(max = totalWidthDp)
                                    else Modifier
                                )
                        ) {
                            Row(
                                modifier = if (isScrollable) Modifier.horizontalScroll(rememberScrollState()) else Modifier,
                                horizontalArrangement = Arrangement.spacedBy(itemSpacingDp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                options.forEach { option ->
                                    val isPrimary = option == callout.key.primaryText
                                    Box(
                                        modifier = Modifier
                                            .size(width = itemWidthDp, height = itemHeightDp)
                                            .shadow(if (isPrimary) 2.dp else 0.5.dp, RoundedCornerShape(6.dp))
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isPrimary) colorScheme.accent.copy(alpha = 0.28f) else colorScheme.keyBackground)
                                            .border(
                                                if (isPrimary) 1.dp else 0.5.dp,
                                                if (isPrimary) colorScheme.accent else colorScheme.borderColor,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable {
                                                HapticHelper.performKeyHaptic(context, view, "Medium")
                                                if (soundEnabled) {
                                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                                }
                                                onTextInput(option)
                                                activeCalloutState = null
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = option,
                                            color = colorScheme.keyText,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (showLanguagePicker) {
                    LanguagePickerModal(
                        currentLanguage = currentLanguage,
                        colorScheme = colorScheme,
                        onSelect = { langId ->
                            onSelectLanguage(langId)
                            showLanguagePicker = false
                        },
                        onOpenManager = {
                            showLanguagePicker = false
                            onOpenSettings()
                        },
                        onDismiss = { showLanguagePicker = false }
                    )
                }

                var showLanguageToast by remember { mutableStateOf(false) }
                var toastLanguageText by remember { mutableStateOf("") }
                var isInitialRender by remember { mutableStateOf(true) }

                LaunchedEffect(currentLanguage) {
                    if (isInitialRender) {
                        isInitialRender = false
                        return@LaunchedEffect
                    }
                    val langInfo = try {
                        KeyboardProApp.instance.languageManager.getLanguageInfo(currentLanguage)
                    } catch (e: Throwable) { null }
                    toastLanguageText = langInfo?.let { "${it.flag} ${it.nameArabic}" } ?: currentLanguage
                    showLanguageToast = true
                    delay(1200L)
                    showLanguageToast = false
                }

                if (showLanguageToast) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .shadow(12.dp, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(colorScheme.accent)
                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = toastLanguageText,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (showKeyPreview) {
                    CentralKeyPreviewOverlay(
                        previewState = activeKeyPreviewState,
                        containerCoordsProvider = { keyboardContainerCoordinates },
                        colorScheme = colorScheme
                    )
                }
            }
        }
    }
}

@Composable
private fun OneHandedSidebar(
    colorScheme: KeyboardColorScheme,
    isLeft: Boolean,
    onSwitchSide: () -> Unit,
    onExpand: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(48.dp)
            .fillMaxHeight()
            .background(colorScheme.specialKeyBackground.copy(alpha = 0.5f))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorScheme.keyBackground)
                .clickable(onClick = onSwitchSide),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isLeft) Icons.Default.ArrowForward else Icons.Default.ArrowBack,
                contentDescription = "تبديل الجهة",
                tint = colorScheme.accent,
                modifier = Modifier.size(20.dp)
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorScheme.keyBackground)
                .clickable(onClick = onExpand),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Fullscreen,
                contentDescription = "تكبير",
                tint = colorScheme.accent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ArabicKeyboardLayout(
    colorScheme: KeyboardColorScheme,
    keyHeight: androidx.compose.ui.unit.Dp,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    autoTranslateOnEnter: Boolean = false,
    onToggleAutoTranslate: ((Boolean) -> Unit)? = null,
    spacebarLanguageSwitch: Boolean = true,
    arabicNumerals: Boolean = true,
    currentLanguage: String = "ar",
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteWord: (() -> Unit)? = null,
    onDeleteAll: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onLongPressEnter: (() -> Unit)? = null,
    onSwitchMode: () -> Unit,
    onSwitchLanguage: () -> Unit,
    onSwitchPreviousLanguage: (() -> Unit)? = null,
    onLongPressLanguage: (() -> Unit)? = null,
    onOpenClipboard: () -> Unit,
    onOpenEmoji: () -> Unit,
    onToggleTashkeel: () -> Unit,
    onMoveCursor: (Int) -> Unit,
    onLongPressKey: (KeyModel, LayoutCoordinates?) -> Unit,
    onOpenTranslate: (() -> Unit)? = null,
    showKeyPreview: Boolean = true,
    spaceLabel: String = "مسافة",
    keyFontSize: androidx.compose.ui.unit.TextUnit = 19.sp,
    secondaryFontSize: androidx.compose.ui.unit.TextUnit = 9.sp,
    keyCornerRadius: androidx.compose.ui.unit.Dp = 6.dp,
    keyStrokeBorder: Boolean = false,
    soundType: String = "CLICK",
    soundVolume: Int = 50,
    hapticIntensity: String = "Medium",
    hapticDurationMs: Int = 20,
    onPreviewChange: ((String, androidx.compose.ui.layout.LayoutCoordinates?, Boolean) -> Unit)? = null
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.arabicRow1) {
            KeyButton(
                text = key.primaryText,
                secondaryText = key.secondaryText,
                height = keyHeight,
                fontSize = keyFontSize,
                secondaryFontSize = secondaryFontSize,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                showPreview = showKeyPreview,
                onPreviewChange = onPreviewChange,
                modifier = Modifier.weight(key.weight),
                onLongClickWithCoords = { coords -> onLongPressKey(key, coords) },
                onLongClick = { onLongPressKey(key, null) }
            ) {
                onTextInput(key.primaryText)
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.arabicRow2) {
            KeyButton(
                text = key.primaryText,
                secondaryText = key.secondaryText,
                height = keyHeight,
                fontSize = keyFontSize,
                secondaryFontSize = secondaryFontSize,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                showPreview = showKeyPreview,
                onPreviewChange = onPreviewChange,
                modifier = Modifier.weight(key.weight),
                onLongClickWithCoords = { coords -> onLongPressKey(key, coords) },
                onLongClick = { onLongPressKey(key, null) }
            ) {
                onTextInput(key.primaryText)
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.arabicRow3) {
            KeyButton(
                text = key.primaryText,
                secondaryText = key.secondaryText,
                height = keyHeight,
                fontSize = keyFontSize,
                secondaryFontSize = secondaryFontSize,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                showPreview = showKeyPreview,
                onPreviewChange = onPreviewChange,
                modifier = Modifier.weight(1f),
                onLongClickWithCoords = { coords -> onLongPressKey(key, coords) },
                onLongClick = { onLongPressKey(key, null) }
            ) {
                onTextInput(key.primaryText)
            }
        }

        RepeatingDeleteKeyButton(
            height = keyHeight,
            cornerRadius = keyCornerRadius,
            strokeBorder = keyStrokeBorder,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            hapticIntensity = hapticIntensity,
            hapticDurationMs = hapticDurationMs,
            soundEnabled = soundEnabled,
            soundType = soundType,
            soundVolume = soundVolume,
            modifier = Modifier.weight(1.35f),
            onDelete = onDelete,
            onDeleteWord = onDeleteWord,
            onDeleteAll = onDeleteAll
        )
    }

    BottomControlRow(
        modeLabel = if (arabicNumerals) "١٢٣؟" else "?123",
        currentLanguage = currentLanguage,
        spaceLabel = spaceLabel,
        commaLabel = "،",
        colorScheme = colorScheme,
        keyHeight = keyHeight,
        keyCornerRadius = keyCornerRadius,
        keyStrokeBorder = keyStrokeBorder,
        hapticEnabled = hapticEnabled,
        soundEnabled = soundEnabled,
        soundType = soundType,
        soundVolume = soundVolume,
        hapticIntensity = hapticIntensity,
        hapticDurationMs = hapticDurationMs,
        actionIcon = actionIcon,
        autoTranslateOnEnter = autoTranslateOnEnter,
        onToggleAutoTranslate = onToggleAutoTranslate,
        onSwitchMode = onSwitchMode,
        onSwitchLanguage = onSwitchLanguage,
        onSwitchPreviousLanguage = onSwitchPreviousLanguage ?: onSwitchLanguage,
        onLongPressLanguage = onLongPressLanguage,
        onOpenClipboard = onOpenClipboard,
        onOpenTranslate = onOpenTranslate,
        onSpace = onSpace,
        onEnter = onEnter,
        onLongPressEnter = onLongPressEnter,
        onOpenEmoji = onOpenEmoji,
        onMoveCursor = onMoveCursor,
        onTextInput = onTextInput,
        spacebarLanguageSwitch = spacebarLanguageSwitch
    )
}

@Composable
private fun DynamicKeyboardLayout(
    layoutData: KeyboardLayoutData,
    colorScheme: KeyboardColorScheme,
    keyHeight: androidx.compose.ui.unit.Dp,
    keyFontSize: androidx.compose.ui.unit.TextUnit = 19.sp,
    secondaryFontSize: androidx.compose.ui.unit.TextUnit = 9.sp,
    keyCornerRadius: androidx.compose.ui.unit.Dp = 6.dp,
    keyStrokeBorder: Boolean = false,
    soundType: String = "CLICK",
    soundVolume: Int = 50,
    hapticIntensity: String = "Medium",
    hapticDurationMs: Int = 20,
    currentLanguage: String = "en",
    shiftState: ShiftState,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    autoTranslateOnEnter: Boolean = false,
    onToggleAutoTranslate: ((Boolean) -> Unit)? = null,
    spacebarLanguageSwitch: Boolean = true,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteWord: (() -> Unit)? = null,
    onDeleteAll: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onLongPressEnter: (() -> Unit)? = null,
    onOpenClipboard: (() -> Unit)? = null,
    onOpenEmoji: (() -> Unit)? = null,
    onShiftClick: () -> Unit,
    onSwitchMode: () -> Unit,
    onSwitchLanguage: () -> Unit,
    onSwitchPreviousLanguage: (() -> Unit)? = null,
    onLongPressLanguage: (() -> Unit)? = null,
    onMoveCursor: (Int) -> Unit,
    onLongPressKey: (KeyModel, LayoutCoordinates?) -> Unit,
    onOpenTranslate: (() -> Unit)? = null,
    showKeyPreview: Boolean = true,
    onPreviewChange: ((String, androidx.compose.ui.layout.LayoutCoordinates?, Boolean) -> Unit)? = null
) {
    val isUpper = shiftState != ShiftState.OFF

    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in layoutData.row1) {
            val letter = if (isUpper) key.primaryText.uppercase() else key.primaryText.lowercase()
            KeyButton(
                text = letter,
                secondaryText = key.secondaryText,
                height = keyHeight,
                fontSize = keyFontSize,
                secondaryFontSize = secondaryFontSize,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                showPreview = showKeyPreview,
                onPreviewChange = onPreviewChange,
                modifier = Modifier.weight(key.weight),
                onLongClickWithCoords = { coords -> onLongPressKey(key, coords) },
                onLongClick = { onLongPressKey(key, null) }
            ) {
                onTextInput(letter)
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in layoutData.row2) {
            val letter = if (isUpper) key.primaryText.uppercase() else key.primaryText.lowercase()
            KeyButton(
                text = letter,
                height = keyHeight,
                fontSize = keyFontSize,
                secondaryFontSize = secondaryFontSize,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                showPreview = showKeyPreview,
                onPreviewChange = onPreviewChange,
                modifier = Modifier.weight(key.weight),
                onLongClickWithCoords = { coords -> onLongPressKey(key, coords) },
                onLongClick = { onLongPressKey(key, null) }
            ) {
                onTextInput(letter)
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        val shiftIcon = when (shiftState) {
            ShiftState.CAPS_LOCK -> "⇪"
            ShiftState.ON -> "⇧"
            ShiftState.OFF -> "⇧"
        }
        KeyButton(
            text = shiftIcon,
            isSpecial = true,
            fontSize = 18.sp,
            height = keyHeight,
            cornerRadius = keyCornerRadius,
            strokeBorder = keyStrokeBorder,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            hapticIntensity = hapticIntensity,
            hapticDurationMs = hapticDurationMs,
            soundEnabled = soundEnabled,
            soundType = soundType,
            soundVolume = soundVolume,
            modifier = Modifier.weight(1.3f)
        ) {
            onShiftClick()
        }

        for (key in layoutData.row3.filter { it.type == KeyType.CHARACTER }) {
            val letter = if (isUpper) key.primaryText.uppercase() else key.primaryText.lowercase()
            KeyButton(
                text = letter,
                height = keyHeight,
                fontSize = keyFontSize,
                secondaryFontSize = secondaryFontSize,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                showPreview = showKeyPreview,
                onPreviewChange = onPreviewChange,
                modifier = Modifier.weight(key.weight),
                onLongClickWithCoords = { coords -> onLongPressKey(key, coords) },
                onLongClick = { onLongPressKey(key, null) }
            ) {
                onTextInput(letter)
            }
        }

        RepeatingDeleteKeyButton(
            height = keyHeight,
            cornerRadius = keyCornerRadius,
            strokeBorder = keyStrokeBorder,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            hapticIntensity = hapticIntensity,
            hapticDurationMs = hapticDurationMs,
            soundEnabled = soundEnabled,
            soundType = soundType,
            soundVolume = soundVolume,
            modifier = Modifier.weight(1.35f),
            onDelete = onDelete,
            onDeleteWord = onDeleteWord,
            onDeleteAll = onDeleteAll
        )
    }

    BottomControlRow(
        modeLabel = "?123",
        currentLanguage = currentLanguage,
        spaceLabel = layoutData.spaceLabel,
        commaLabel = ",",
        colorScheme = colorScheme,
        keyHeight = keyHeight,
        keyCornerRadius = keyCornerRadius,
        keyStrokeBorder = keyStrokeBorder,
        hapticEnabled = hapticEnabled,
        soundEnabled = soundEnabled,
        soundType = soundType,
        soundVolume = soundVolume,
        hapticIntensity = hapticIntensity,
        hapticDurationMs = hapticDurationMs,
        actionIcon = actionIcon,
        autoTranslateOnEnter = autoTranslateOnEnter,
        onToggleAutoTranslate = onToggleAutoTranslate,
        onSwitchMode = onSwitchMode,
        onSwitchLanguage = onSwitchLanguage,
        onSwitchPreviousLanguage = onSwitchPreviousLanguage,
        onLongPressLanguage = onLongPressLanguage,
        onOpenClipboard = onOpenClipboard,
        onOpenTranslate = onOpenTranslate,
        onSpace = onSpace,
        onEnter = onEnter,
        onLongPressEnter = onLongPressEnter,
        onOpenEmoji = onOpenEmoji,
        onMoveCursor = onMoveCursor,
        onTextInput = onTextInput,
        spacebarLanguageSwitch = spacebarLanguageSwitch
    )
}

@Composable
private fun LanguagePickerModal(
    currentLanguage: String,
    colorScheme: KeyboardColorScheme,
    onSelect: (String) -> Unit,
    onOpenManager: () -> Unit,
    onDismiss: () -> Unit
) {
    val prefs = KeyboardProApp.instance.preferences
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("المثبتة") }
    val categories = listOf("المثبتة", "العربية", "العالمية", "الكل")
    val allLanguages = remember { com.example.language.repository.LanguageRepository.masterCatalog }
    val enabledIds = remember(prefs.enabledLanguages) { prefs.enabledLanguages }

    val filteredLanguages = remember(searchQuery, selectedCategory, enabledIds, allLanguages) {
        allLanguages.filter { lang ->
            val matchesCategory = when (selectedCategory) {
                "المثبتة" -> enabledIds.contains(lang.id)
                "العربية" -> lang.id.startsWith("ar")
                "العالمية" -> !lang.id.startsWith("ar")
                else -> true
            }
            val matchesQuery = searchQuery.isBlank() ||
                    lang.nameArabic.contains(searchQuery, ignoreCase = true) ||
                    lang.nativeName.contains(searchQuery, ignoreCase = true) ||
                    lang.id.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 340.dp)
                .fillMaxWidth(0.92f)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.background)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تبديل واختيار اللغة",
                        color = colorScheme.keyText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = colorScheme.keyText)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) colorScheme.accent else colorScheme.keyBackground)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.White else colorScheme.keyText,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorScheme.keyBackground)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = colorScheme.keyText.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = colorScheme.keyText,
                                fontSize = 13.sp
                            ),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        "بحث عن لغة...",
                                        fontSize = 12.sp,
                                        color = colorScheme.keyText.copy(alpha = 0.45f)
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(filteredLanguages, key = { it.id }) { lang ->
                        val isSelected = lang.id == currentLanguage
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) colorScheme.accent.copy(alpha = 0.22f) else colorScheme.keyBackground)
                                .clickable {
                                    if (!enabledIds.contains(lang.id)) {
                                        prefs.enabledLanguages = enabledIds + lang.id
                                    }
                                    onSelect(lang.id)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(lang.flag, fontSize = 22.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    lang.nameArabic,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) colorScheme.accent else colorScheme.keyText
                                )
                                Text(
                                    lang.nativeName,
                                    fontSize = 11.sp,
                                    color = colorScheme.keyText.copy(alpha = 0.6f)
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = colorScheme.accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = colorScheme.specialKeyBackground)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(colorScheme.specialKeyBackground)
                        .clickable(onClick = onOpenManager)
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Language,
                        contentDescription = null,
                        tint = colorScheme.accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "إدارة وتحميل اللغات (40+ لغة)",
                        color = colorScheme.accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun Symbols1Layout(
    colorScheme: KeyboardColorScheme,
    keyHeight: androidx.compose.ui.unit.Dp,
    keyCornerRadius: androidx.compose.ui.unit.Dp = 6.dp,
    keyStrokeBorder: Boolean = false,
    soundType: String = "CLICK",
    soundVolume: Int = 50,
    hapticIntensity: String = "Medium",
    hapticDurationMs: Int = 20,
    currentLanguage: String = "ar",
    arabicNumerals: Boolean = true,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    autoTranslateOnEnter: Boolean = false,
    onToggleAutoTranslate: ((Boolean) -> Unit)? = null,
    spacebarLanguageSwitch: Boolean = true,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteWord: (() -> Unit)? = null,
    onDeleteAll: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onLongPressEnter: (() -> Unit)? = null,
    onSwitchToAlpha: () -> Unit,
    onSwitchToSymbols2: () -> Unit,
    onSwitchToNumpad: (() -> Unit)? = null,
    onOpenClipboard: (() -> Unit)? = null,
    onOpenTranslate: (() -> Unit)? = null,
    onOpenEmoji: (() -> Unit)? = null,
    onSwitchLanguage: () -> Unit,
    onSwitchPreviousLanguage: (() -> Unit)? = null,
    onLongPressLanguage: (() -> Unit)? = null,
    onMoveCursor: (Int) -> Unit,
    onPreviewChange: ((String, androidx.compose.ui.layout.LayoutCoordinates?, Boolean) -> Unit)? = null
) {
    val isArabicNums = currentLanguage.startsWith("ar") && arabicNumerals
    val row1Keys = if (isArabicNums) KeyboardLayouts.arabicNumbersRow else KeyboardLayouts.symbols1Row1

    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in row1Keys) {
            KeyButton(
                text = key.primaryText,
                secondaryText = key.secondaryText,
                height = keyHeight,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                onPreviewChange = onPreviewChange,
                modifier = Modifier.weight(1f)
            ) { onTextInput(key.primaryText) }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.symbols1Row2) {
            KeyButton(
                text = key.primaryText,
                height = keyHeight,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                onPreviewChange = onPreviewChange,
                modifier = Modifier.weight(1f)
            ) { onTextInput(key.primaryText) }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.symbols1Row3) {
            KeyButton(
                text = key.primaryText,
                height = keyHeight,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                onPreviewChange = onPreviewChange,
                modifier = Modifier.weight(1f)
            ) { onTextInput(key.primaryText) }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        KeyButton(
            text = "1/2",
            secondaryText = "#+=",
            isSpecial = true,
            fontSize = 13.sp,
            secondaryFontSize = 8.sp,
            height = keyHeight,
            cornerRadius = keyCornerRadius,
            strokeBorder = keyStrokeBorder,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            hapticIntensity = hapticIntensity,
            hapticDurationMs = hapticDurationMs,
            soundEnabled = soundEnabled,
            soundType = soundType,
            soundVolume = soundVolume,
            modifier = Modifier.weight(1.15f)
        ) { onSwitchToSymbols2() }

        for (key in KeyboardLayouts.symbols1Row4) {
            KeyButton(
                text = key.primaryText,
                height = keyHeight,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                onPreviewChange = onPreviewChange,
                modifier = Modifier.weight(1f)
            ) { onTextInput(key.primaryText) }
        }

        if (onSwitchToNumpad != null) {
            KeyButton(
                text = "🔢",
                secondaryText = "123",
                isSpecial = true,
                fontSize = 14.sp,
                secondaryFontSize = 8.sp,
                height = keyHeight,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                modifier = Modifier.weight(1.05f)
            ) { onSwitchToNumpad() }
        }

        RepeatingDeleteKeyButton(
            height = keyHeight,
            cornerRadius = keyCornerRadius,
            strokeBorder = keyStrokeBorder,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            hapticIntensity = hapticIntensity,
            hapticDurationMs = hapticDurationMs,
            soundEnabled = soundEnabled,
            soundType = soundType,
            soundVolume = soundVolume,
            modifier = Modifier.weight(1.35f),
            onDelete = onDelete,
            onDeleteWord = onDeleteWord,
            onDeleteAll = onDeleteAll
        )
    }

    val returnToLettersLabel = if (currentLanguage.startsWith("ar")) "عربي" else "ABC"
    val spaceLabel = if (currentLanguage.startsWith("ar")) "مسافة" else "English"

    BottomControlRow(
        modeLabel = returnToLettersLabel,
        currentLanguage = currentLanguage,
        spaceLabel = spaceLabel,
        commaLabel = "،",
        colorScheme = colorScheme,
        keyHeight = keyHeight,
        keyCornerRadius = keyCornerRadius,
        keyStrokeBorder = keyStrokeBorder,
        hapticEnabled = hapticEnabled,
        soundEnabled = soundEnabled,
        soundType = soundType,
        soundVolume = soundVolume,
        hapticIntensity = hapticIntensity,
        hapticDurationMs = hapticDurationMs,
        actionIcon = actionIcon,
        autoTranslateOnEnter = autoTranslateOnEnter,
        onToggleAutoTranslate = onToggleAutoTranslate,
        onSwitchMode = onSwitchToAlpha,
        onSwitchLanguage = onSwitchLanguage,
        onSwitchPreviousLanguage = onSwitchPreviousLanguage ?: onSwitchLanguage,
        onLongPressLanguage = onLongPressLanguage,
        onOpenClipboard = onOpenClipboard,
        onOpenTranslate = onOpenTranslate,
        onOpenEmoji = onOpenEmoji,
        onSpace = onSpace,
        onEnter = onEnter,
        onLongPressEnter = onLongPressEnter,
        onMoveCursor = onMoveCursor,
        onTextInput = onTextInput,
        spacebarLanguageSwitch = spacebarLanguageSwitch
    )
}

@Composable
private fun Symbols2Layout(
    colorScheme: KeyboardColorScheme,
    keyHeight: androidx.compose.ui.unit.Dp,
    keyCornerRadius: androidx.compose.ui.unit.Dp = 6.dp,
    keyStrokeBorder: Boolean = false,
    soundType: String = "CLICK",
    soundVolume: Int = 50,
    hapticIntensity: String = "Medium",
    hapticDurationMs: Int = 20,
    currentLanguage: String = "ar",
    arabicNumerals: Boolean = true,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    autoTranslateOnEnter: Boolean = false,
    onToggleAutoTranslate: ((Boolean) -> Unit)? = null,
    spacebarLanguageSwitch: Boolean = true,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteWord: (() -> Unit)? = null,
    onDeleteAll: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onLongPressEnter: (() -> Unit)? = null,
    onSwitchToAlpha: () -> Unit,
    onSwitchToSymbols1: () -> Unit,
    onOpenClipboard: (() -> Unit)? = null,
    onOpenTranslate: (() -> Unit)? = null,
    onOpenEmoji: (() -> Unit)? = null,
    onSwitchLanguage: () -> Unit,
    onSwitchPreviousLanguage: (() -> Unit)? = null,
    onLongPressLanguage: (() -> Unit)? = null,
    onMoveCursor: (Int) -> Unit,
    onPreviewChange: ((String, androidx.compose.ui.layout.LayoutCoordinates?, Boolean) -> Unit)? = null
) {
    val isArabicNums = currentLanguage.startsWith("ar") && arabicNumerals
    val row1Keys = if (isArabicNums) KeyboardLayouts.arabicNumbersRow else KeyboardLayouts.symbols2Row1

    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in row1Keys) {
            KeyButton(
                text = key.primaryText,
                secondaryText = key.secondaryText,
                height = keyHeight,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                onPreviewChange = onPreviewChange,
                modifier = Modifier.weight(1f)
            ) { onTextInput(key.primaryText) }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.symbols2Row2) {
            KeyButton(
                text = key.primaryText,
                height = keyHeight,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                onPreviewChange = onPreviewChange,
                modifier = Modifier.weight(1f)
            ) { onTextInput(key.primaryText) }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.symbols2Row3) {
            KeyButton(
                text = key.primaryText,
                height = keyHeight,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                onPreviewChange = onPreviewChange,
                modifier = Modifier.weight(1f)
            ) { onTextInput(key.primaryText) }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        KeyButton(
            text = "2/2",
            secondaryText = "123",
            isSpecial = true,
            fontSize = 13.sp,
            secondaryFontSize = 8.sp,
            height = keyHeight,
            cornerRadius = keyCornerRadius,
            strokeBorder = keyStrokeBorder,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            hapticIntensity = hapticIntensity,
            hapticDurationMs = hapticDurationMs,
            soundEnabled = soundEnabled,
            soundType = soundType,
            soundVolume = soundVolume,
            modifier = Modifier.weight(1.2f)
        ) { onSwitchToSymbols1() }

        for (key in KeyboardLayouts.symbols2Row4) {
            KeyButton(
                text = key.primaryText,
                height = keyHeight,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                onPreviewChange = onPreviewChange,
                modifier = Modifier.weight(1f)
            ) { onTextInput(key.primaryText) }
        }

        RepeatingDeleteKeyButton(
            height = keyHeight,
            cornerRadius = keyCornerRadius,
            strokeBorder = keyStrokeBorder,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            hapticIntensity = hapticIntensity,
            hapticDurationMs = hapticDurationMs,
            soundEnabled = soundEnabled,
            soundType = soundType,
            soundVolume = soundVolume,
            modifier = Modifier.weight(1.35f),
            onDelete = onDelete,
            onDeleteWord = onDeleteWord,
            onDeleteAll = onDeleteAll
        )
    }

    val returnToLettersLabel = if (currentLanguage.startsWith("ar")) "عربي" else "ABC"
    val spaceLabel = if (currentLanguage.startsWith("ar")) "مسافة" else "English"

    BottomControlRow(
        modeLabel = returnToLettersLabel,
        currentLanguage = currentLanguage,
        spaceLabel = spaceLabel,
        commaLabel = "،",
        colorScheme = colorScheme,
        keyHeight = keyHeight,
        keyCornerRadius = keyCornerRadius,
        keyStrokeBorder = keyStrokeBorder,
        hapticEnabled = hapticEnabled,
        soundEnabled = soundEnabled,
        soundType = soundType,
        soundVolume = soundVolume,
        hapticIntensity = hapticIntensity,
        hapticDurationMs = hapticDurationMs,
        actionIcon = actionIcon,
        autoTranslateOnEnter = autoTranslateOnEnter,
        onToggleAutoTranslate = onToggleAutoTranslate,
        onSwitchMode = onSwitchToAlpha,
        onSwitchLanguage = onSwitchLanguage,
        onSwitchPreviousLanguage = onSwitchPreviousLanguage ?: onSwitchLanguage,
        onLongPressLanguage = onLongPressLanguage,
        onOpenClipboard = onOpenClipboard,
        onOpenTranslate = onOpenTranslate,
        onOpenEmoji = onOpenEmoji,
        onSpace = onSpace,
        onEnter = onEnter,
        onLongPressEnter = onLongPressEnter,
        onMoveCursor = onMoveCursor,
        onTextInput = onTextInput,
        spacebarLanguageSwitch = spacebarLanguageSwitch
    )
}

@Composable
private fun NumpadKeyboardLayout(
    colorScheme: KeyboardColorScheme,
    currentLanguage: String = "ar",
    keyHeight: androidx.compose.ui.unit.Dp,
    keyCornerRadius: androidx.compose.ui.unit.Dp = 6.dp,
    keyStrokeBorder: Boolean = false,
    soundType: String = "CLICK",
    soundVolume: Int = 50,
    hapticIntensity: String = "Medium",
    hapticDurationMs: Int = 20,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteWord: (() -> Unit)? = null,
    onDeleteAll: () -> Unit,
    onEnter: () -> Unit,
    onLongPressEnter: (() -> Unit)? = null,
    onSwitchToAlpha: () -> Unit,
    onSwitchToSymbols: () -> Unit,
    onPreviewChange: ((String, androidx.compose.ui.layout.LayoutCoordinates?, Boolean) -> Unit)? = null
) {
    val returnToLettersLabel = if (currentLanguage.startsWith("ar")) "عربي" else "ABC"

    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.numpadRow1) {
            val keyDisplay = if (key.type == KeyType.SWITCH_MODE) returnToLettersLabel else key.primaryText
            KeyButton(
                text = keyDisplay,
                isSpecial = key.type != KeyType.CHARACTER,
                height = keyHeight,
                fontSize = if (key.type == KeyType.CHARACTER) 20.sp else 13.sp,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                modifier = Modifier.weight(key.weight)
            ) {
                if (key.type == KeyType.SWITCH_MODE) {
                    onSwitchToAlpha()
                } else {
                    onTextInput(key.primaryText)
                }
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.numpadRow2) {
            KeyButton(
                text = key.primaryText,
                isSpecial = key.type != KeyType.CHARACTER,
                height = keyHeight,
                fontSize = 20.sp,
                cornerRadius = keyCornerRadius,
                strokeBorder = keyStrokeBorder,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
                hapticDurationMs = hapticDurationMs,
                soundEnabled = soundEnabled,
                soundType = soundType,
                soundVolume = soundVolume,
                modifier = Modifier.weight(key.weight)
            ) {
                onTextInput(key.primaryText)
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.numpadRow3) {
            if (key.type == KeyType.BACKSPACE) {
                RepeatingDeleteKeyButton(
                    height = keyHeight,
                    cornerRadius = keyCornerRadius,
                    strokeBorder = keyStrokeBorder,
                    colorScheme = colorScheme,
                    hapticEnabled = hapticEnabled,
                    hapticIntensity = hapticIntensity,
                    hapticDurationMs = hapticDurationMs,
                    soundEnabled = soundEnabled,
                    soundType = soundType,
                    soundVolume = soundVolume,
                    modifier = Modifier.weight(key.weight),
                    onDelete = onDelete,
                    onDeleteWord = onDeleteWord,
                    onDeleteAll = onDeleteAll
                )
            } else {
                KeyButton(
                    text = key.primaryText,
                    height = keyHeight,
                    fontSize = 20.sp,
                    cornerRadius = keyCornerRadius,
                    strokeBorder = keyStrokeBorder,
                    colorScheme = colorScheme,
                    hapticEnabled = hapticEnabled,
                    hapticIntensity = hapticIntensity,
                    hapticDurationMs = hapticDurationMs,
                    soundEnabled = soundEnabled,
                    soundType = soundType,
                    soundVolume = soundVolume,
                    modifier = Modifier.weight(key.weight)
                ) {
                    onTextInput(key.primaryText)
                }
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.numpadRow4) {
            if (key.type == KeyType.ENTER) {
                EnterKeyButton(
                    modifier = Modifier.weight(key.weight),
                    keyHeight = keyHeight,
                    actionIcon = actionIcon,
                    colorScheme = colorScheme,
                    cornerRadius = keyCornerRadius,
                    strokeBorder = keyStrokeBorder,
                    hapticEnabled = hapticEnabled,
                    soundEnabled = soundEnabled,
                    soundType = soundType,
                    soundVolume = soundVolume,
                    hapticIntensity = hapticIntensity,
                    hapticDurationMs = hapticDurationMs,
                    onEnter = onEnter,
                    onLongPressEnter = onLongPressEnter
                )
            } else if (key.type == KeyType.SWITCH_MODE) {
                KeyButton(
                    text = key.primaryText,
                    isSpecial = true,
                    height = keyHeight,
                    fontSize = 12.sp,
                    cornerRadius = keyCornerRadius,
                    strokeBorder = keyStrokeBorder,
                    colorScheme = colorScheme,
                    hapticEnabled = hapticEnabled,
                    hapticIntensity = hapticIntensity,
                    hapticDurationMs = hapticDurationMs,
                    soundEnabled = soundEnabled,
                    soundType = soundType,
                    soundVolume = soundVolume,
                    modifier = Modifier.weight(key.weight)
                ) {
                    onSwitchToSymbols()
                }
            } else {
                KeyButton(
                    text = key.primaryText,
                    height = keyHeight,
                    fontSize = 20.sp,
                    cornerRadius = keyCornerRadius,
                    strokeBorder = keyStrokeBorder,
                    colorScheme = colorScheme,
                    hapticEnabled = hapticEnabled,
                    hapticIntensity = hapticIntensity,
                    hapticDurationMs = hapticDurationMs,
                    soundEnabled = soundEnabled,
                    soundType = soundType,
                    soundVolume = soundVolume,
                    modifier = Modifier.weight(key.weight)
                ) {
                    onTextInput(key.primaryText)
                }
            }
        }
    }
}

@Composable
private fun BottomControlRow(
    modeLabel: String,
    currentLanguage: String = "ar",
    spaceLabel: String = "مسافة",
    commaLabel: String = "،",
    colorScheme: KeyboardColorScheme,
    keyHeight: androidx.compose.ui.unit.Dp,
    keyCornerRadius: androidx.compose.ui.unit.Dp = 6.dp,
    keyStrokeBorder: Boolean = false,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    soundType: String = "CLICK",
    soundVolume: Int = 50,
    hapticIntensity: String = "Medium",
    hapticDurationMs: Int = 20,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    autoTranslateOnEnter: Boolean = false,
    onToggleAutoTranslate: ((Boolean) -> Unit)? = null,
    onSwitchMode: () -> Unit,
    onSwitchLanguage: () -> Unit,
    onSwitchPreviousLanguage: (() -> Unit)? = null,
    onLongPressLanguage: (() -> Unit)? = null,
    onOpenClipboard: (() -> Unit)? = null,
    onOpenTranslate: (() -> Unit)? = null,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onLongPressEnter: (() -> Unit)? = null,
    onOpenEmoji: (() -> Unit)? = null,
    onMoveCursor: (Int) -> Unit,
    onTextInput: (String) -> Unit,
    spacebarLanguageSwitch: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        KeyButton(
            text = modeLabel,
            secondaryText = if (modeLabel.contains("123") || modeLabel.contains("١٢٣") || modeLabel.contains("Sym")) "رموز" else null,
            isSpecial = true,
            fontSize = 13.sp,
            secondaryFontSize = 8.sp,
            height = keyHeight,
            cornerRadius = keyCornerRadius,
            strokeBorder = keyStrokeBorder,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            soundType = soundType,
            soundVolume = soundVolume,
            hapticIntensity = hapticIntensity,
            hapticDurationMs = hapticDurationMs,
            modifier = Modifier.weight(1.05f),
            onLongClick = null
        ) {
            onSwitchMode()
        }

        KeyButton(
            text = commaLabel,
            secondaryText = "...",
            isSpecial = true,
            fontSize = 15.sp,
            secondaryFontSize = 8.sp,
            height = keyHeight,
            cornerRadius = keyCornerRadius,
            strokeBorder = keyStrokeBorder,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            soundType = soundType,
            soundVolume = soundVolume,
            hapticIntensity = hapticIntensity,
            hapticDurationMs = hapticDurationMs,
            modifier = Modifier.weight(0.75f)
        ) {
            onTextInput(commaLabel)
        }

        val isArabic = currentLanguage.startsWith("ar")
        val langToggleSecondary = if (isArabic) "EN" else "عربي"
        KeyButton(
            text = "🌐",
            secondaryText = langToggleSecondary,
            isSpecial = true,
            fontSize = 14.sp,
            secondaryFontSize = 9.sp,
            height = keyHeight,
            cornerRadius = keyCornerRadius,
            strokeBorder = keyStrokeBorder,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            soundType = soundType,
            soundVolume = soundVolume,
            hapticIntensity = hapticIntensity,
            hapticDurationMs = hapticDurationMs,
            modifier = Modifier.weight(0.85f),
            onLongClick = onLongPressLanguage
        ) {
            onSwitchLanguage()
        }

        KeyButton(
            text = "📋",
            secondaryText = "حافظة",
            isSpecial = true,
            fontSize = 14.sp,
            secondaryFontSize = 8.sp,
            height = keyHeight,
            cornerRadius = keyCornerRadius,
            strokeBorder = keyStrokeBorder,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            soundType = soundType,
            soundVolume = soundVolume,
            hapticIntensity = hapticIntensity,
            hapticDurationMs = hapticDurationMs,
            modifier = Modifier.weight(0.85f),
            onLongClick = onOpenClipboard
        ) {
            if (onOpenClipboard != null) onOpenClipboard()
        }

        SpaceBarKeyButton(
            modifier = Modifier.weight(3.6f),
            spaceLabel = spaceLabel,
            keyHeight = keyHeight,
            cornerRadius = keyCornerRadius,
            strokeBorder = keyStrokeBorder,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            soundType = soundType,
            soundVolume = soundVolume,
            hapticIntensity = hapticIntensity,
            hapticDurationMs = hapticDurationMs,
            spacebarLanguageSwitch = spacebarLanguageSwitch,
            onSpace = onSpace,
            onSwitchLanguage = onSwitchLanguage,
            onSwitchPreviousLanguage = onSwitchPreviousLanguage ?: onSwitchLanguage,
            onMoveCursor = onMoveCursor,
            onLongPress = onLongPressLanguage
        )

        KeyButton(
            text = ".",
            secondaryText = "'",
            fontSize = 18.sp,
            secondaryFontSize = 9.sp,
            height = keyHeight,
            cornerRadius = keyCornerRadius,
            strokeBorder = keyStrokeBorder,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            soundType = soundType,
            soundVolume = soundVolume,
            hapticIntensity = hapticIntensity,
            hapticDurationMs = hapticDurationMs,
            modifier = Modifier.weight(0.85f)
        ) {
            onTextInput(".")
        }

        EnterKeyButton(
            modifier = Modifier.weight(1.15f),
            keyHeight = keyHeight,
            actionIcon = actionIcon,
            colorScheme = colorScheme,
            cornerRadius = keyCornerRadius,
            strokeBorder = keyStrokeBorder,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            soundType = soundType,
            soundVolume = soundVolume,
            hapticIntensity = hapticIntensity,
            hapticDurationMs = hapticDurationMs,
            onEnter = onEnter,
            onLongPressEnter = onLongPressEnter
        )
    }
}

@Composable
private fun SpaceBarKeyButton(
    modifier: Modifier = Modifier,
    spaceLabel: String,
    keyHeight: androidx.compose.ui.unit.Dp,
    cornerRadius: androidx.compose.ui.unit.Dp = 6.dp,
    strokeBorder: Boolean = false,
    colorScheme: KeyboardColorScheme,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    soundType: String = "CLICK",
    soundVolume: Int = 50,
    hapticIntensity: String = "Medium",
    hapticDurationMs: Int = 20,
    spacebarLanguageSwitch: Boolean = true,
    onSpace: () -> Unit,
    onSwitchLanguage: () -> Unit,
    onSwitchPreviousLanguage: () -> Unit,
    onMoveCursor: (Int) -> Unit,
    onLongPress: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val view = LocalView.current
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    var isSpacePressed by remember { mutableStateOf(false) }

    val currentOnSpace by rememberUpdatedState(onSpace)
    val currentOnMoveCursor by rememberUpdatedState(onMoveCursor)
    val currentOnSwitchLang by rememberUpdatedState(onSwitchLanguage)
    val currentOnSwitchPrevLang by rememberUpdatedState(onSwitchPreviousLanguage)
    val currentOnLongPress by rememberUpdatedState(onLongPress)
    val currentSpacebarLanguageSwitch by rememberUpdatedState(spacebarLanguageSwitch)
    val currentHapticEnabled by rememberUpdatedState(hapticEnabled)
    val currentHapticIntensity by rememberUpdatedState(hapticIntensity)
    val currentHapticDurationMs by rememberUpdatedState(hapticDurationMs)
    val currentSoundEnabled by rememberUpdatedState(soundEnabled)
    val currentSoundType by rememberUpdatedState(soundType)
    val currentSoundVolume by rememberUpdatedState(soundVolume)

    val keyShape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val baseModifier = modifier
        .height(keyHeight)
        .padding(horizontal = 1.5.dp, vertical = 2.dp)
        .shadow(
            elevation = if (isSpacePressed) 0.5.dp else 1.2.dp,
            shape = keyShape,
            ambientColor = Color.Black.copy(alpha = 0.25f),
            spotColor = Color.Black.copy(alpha = 0.25f)
        )

    val styledModifier = if (strokeBorder) {
        baseModifier
            .border(0.8.dp, colorScheme.borderColor.copy(alpha = 0.35f), keyShape)
            .clip(keyShape)
            .background(if (isSpacePressed) colorScheme.accent.copy(alpha = 0.35f) else colorScheme.keyBackground)
    } else {
        baseModifier
            .clip(keyShape)
            .background(if (isSpacePressed) colorScheme.accent.copy(alpha = 0.35f) else colorScheme.keyBackground)
    }

    Box(
        modifier = styledModifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    isSpacePressed = true
                    if (currentHapticEnabled) HapticHelper.performKeyHaptic(context, view, currentHapticIntensity, currentHapticDurationMs)
                    if (currentSoundEnabled) HapticHelper.performKeySound(context, view, currentSoundType, currentSoundVolume)

                    var totalDragX = 0f
                    var accumulatedSteps = 0
                    val swipeThresholdPx = with(density) { 36.dp.toPx() }
                    var hasSwipedLanguage = false
                    var isLongPressed = false

                    val longPressJob = if (currentOnLongPress != null) {
                        coroutineScope.launch {
                            delay(400L)
                            if (kotlin.math.abs(totalDragX) < 18f && !hasSwipedLanguage) {
                                isLongPressed = true
                                if (currentHapticEnabled) HapticHelper.performKeyHaptic(context, view, "Strong")
                                currentOnLongPress?.invoke()
                            }
                        }
                    } else null

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) {
                            change.consume()
                            break
                        }
                        val dragAmountX = change.position.x - down.position.x
                        totalDragX = dragAmountX

                        if (kotlin.math.abs(dragAmountX) > 18f) {
                            longPressJob?.cancel()
                        }

                        if (currentSpacebarLanguageSwitch && !hasSwipedLanguage && kotlin.math.abs(dragAmountX) >= swipeThresholdPx) {
                            hasSwipedLanguage = true
                            longPressJob?.cancel()
                            if (currentHapticEnabled) HapticHelper.performKeyHaptic(context, view, "Heavy")
                            if (currentSoundEnabled) HapticHelper.performKeySound(context, view, currentSoundType, currentSoundVolume)
                            if (dragAmountX < 0) {
                                currentOnSwitchLang()
                            } else {
                                currentOnSwitchPrevLang()
                            }
                        } else if (!hasSwipedLanguage && !isLongPressed) {
                            val step = (dragAmountX / 24f).toInt()
                            if (step != accumulatedSteps) {
                                val diff = step - accumulatedSteps
                                currentOnMoveCursor(diff)
                                accumulatedSteps = step
                                if (currentHapticEnabled) HapticHelper.performKeyHaptic(context, view, "Light")
                            }
                        }
                    }

                    longPressJob?.cancel()
                    isSpacePressed = false

                    if (!hasSwipedLanguage && !isLongPressed && kotlin.math.abs(totalDragX) < 20f && accumulatedSteps == 0) {
                        currentOnSpace()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "◄",
                color = colorScheme.keyText.copy(alpha = 0.45f),
                fontSize = 10.sp
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = spaceLabel,
                color = colorScheme.keyText.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "►",
                color = colorScheme.keyText.copy(alpha = 0.45f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun CentralKeyPreviewOverlay(
    previewState: State<KeyPreviewState?>,
    containerCoordsProvider: () -> LayoutCoordinates?,
    colorScheme: KeyboardColorScheme
) {
    val preview = previewState.value ?: return
    if (!preview.visible) return

    val density = LocalDensity.current
    val previewWidthDp = (preview.keyWidth / density.density).dp.coerceIn(52.dp, 66.dp)
    val previewHeightDp = 58.dp
    val previewWidthPx = with(density) { previewWidthDp.toPx() }
    val previewHeightPx = with(density) { previewHeightDp.toPx() }

    val rootWidthPx = containerCoordsProvider()?.size?.width?.toFloat() ?: 1080f
    val rawX = preview.centerX - (previewWidthPx / 2f)
    val clampedX = rawX.coerceIn(6f, (rootWidthPx - previewWidthPx - 6f).coerceAtLeast(6f)).roundToInt()
    val targetY = (preview.topY - previewHeightPx - with(density) { 8.dp.toPx() }).roundToInt()

    Box(
        modifier = Modifier
            .offset { IntOffset(clampedX, targetY) }
            .size(previewWidthDp, previewHeightDp)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.keyBackground)
            .border(1.5.dp, colorScheme.accent, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = preview.text,
            color = colorScheme.keyText,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
