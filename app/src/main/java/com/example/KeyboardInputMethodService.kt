package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.example.data.local.entity.ClipboardEntity
import com.example.data.local.entity.UserWordEntity
import com.example.data.pref.KeyboardPreferences
import com.example.data.repository.ClipboardRepository
import com.example.data.repository.ShortcutRepository
import com.example.data.repository.UserWordRepository
import com.example.engine.AiService
import com.example.engine.DecorationEngine
import com.example.engine.SuggestionEngine
import com.example.engine.TranslationEngine
import com.example.ime.ComposeInputMethodService
import com.example.ime.theme.KeyboardThemes
import com.example.ime.ui.KeyboardScreen
import com.example.ime.util.HapticHelper
import com.example.voice.VoiceInputActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KeyboardInputMethodService : ComposeInputMethodService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var prefs: KeyboardPreferences
    private lateinit var clipboardRepo: ClipboardRepository
    private lateinit var userWordRepo: UserWordRepository
    private lateinit var shortcutRepo: ShortcutRepository
    private lateinit var suggestionEngine: SuggestionEngine

    private var currentImeOptions: Int = EditorInfo.IME_ACTION_DONE
    private var currentInputType: Int = android.text.InputType.TYPE_CLASS_TEXT

    // State flows for UI binding
    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    private val _currentTypedWord = MutableStateFlow<String?>(null)
    private val _currentDraftText = MutableStateFlow("")
    private val _isVoiceListening = MutableStateFlow(false)
    private val _voiceStatusText = MutableStateFlow("")
    private val _voicePartialText = MutableStateFlow("")
    private var activeBackHandler: (() -> Boolean)? = null

    private var clipChangedListener: ClipboardManager.OnPrimaryClipChangedListener? = null

    override fun onCreate() {
        super.onCreate()
        instance = this

        val app = application as? KeyboardProApp
        prefs = app?.preferences ?: KeyboardPreferences(this)
        val db = app?.database ?: com.example.data.local.AppDatabase.getDatabase(this)

        clipboardRepo = ClipboardRepository(db.clipboardDao())
        userWordRepo = UserWordRepository(db.userWordDao())
        shortcutRepo = ShortcutRepository(db.shortcutDao())
        suggestionEngine = SuggestionEngine(userWordRepo, shortcutRepo)

        setupClipboardListener()
    }

    private fun setupClipboardListener() {
        try {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
                try {
                    val clip = cm?.primaryClip
                    if (clip != null && clip.itemCount > 0) {
                        val text = clip.getItemAt(0)?.coerceToText(this)?.toString()?.trim()
                        if (!text.isNullOrBlank()) {
                            serviceScope.launch(Dispatchers.IO) {
                                clipboardRepo.insertOrUpdate(text)
                            }
                        }
                    }
                } catch (e: Throwable) {
                    Log.w(TAG, "Error processing clipboard change: ${e.message}")
                }
            }
            cm?.addPrimaryClipChangedListener(clipChangedListener)
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to register clipboard listener: ${e.message}")
        }
    }

    override fun onCreateInputView(): View {
        return ComposeView(this).apply {
            keyboardRootView = this
            setContent {
                val currentThemeName by prefs.themeState.collectAsState()
                val colorScheme = KeyboardThemes.getTheme(currentThemeName, prefs)

                val currentLang by prefs.languageState.collectAsState()
                val keyboardHeight by prefs.heightState.collectAsState()
                val showNumberRow by prefs.numberRowState.collectAsState()
                val hapticSetting by prefs.hapticState.collectAsState()
                val isIncognito by prefs.incognitoState.collectAsState()
                val showSuggestions by prefs.suggestionsState.collectAsState()
                val arabicNumerals by prefs.arabicNumeralsState.collectAsState()
                val autoTranslateOnEnter by prefs.autoTranslateState.collectAsState()
                val spacebarLanguageSwitch by prefs.spacebarLanguageSwitchState.collectAsState()
                val oneHandedMode by prefs.oneHandedState.collectAsState()
                val showKeyPreview by prefs.keyPreviewState.collectAsState()
                val bottomChinPadding by prefs.bottomChinState.collectAsState()
                val showToolbarUndoRedo by prefs.toolbarUndoRedoState.collectAsState()
                val heightPercent by prefs.heightPercentState.collectAsState()
                val widthPercent by prefs.widthPercentState.collectAsState()
                val keyFontSizeSp by prefs.keyFontSizeSpState.collectAsState()
                val secondaryFontSizeSp by prefs.secondaryFontSizeSpState.collectAsState()
                val keyCornerRadiusDp by prefs.keyCornerRadiusState.collectAsState()
                val keyStrokeBorderEnabled by prefs.keyStrokeBorderState.collectAsState()
                val showArrowRow by prefs.arrowRowState.collectAsState()

                val suggestions by _suggestions.collectAsState()
                val typedWord by _currentTypedWord.collectAsState()
                val draftText by _currentDraftText.collectAsState()
                val isVoiceListening by _isVoiceListening.collectAsState()
                val voiceStatusText by _voiceStatusText.collectAsState()
                val voicePartialText by _voicePartialText.collectAsState()

                val clips by clipboardRepo.allClips.collectAsState(initial = emptyList())
                val userWords by userWordRepo.allWords.collectAsState(initial = emptyList())

                KeyboardScreen(
                    colorScheme = colorScheme,
                    currentLanguage = currentLang,
                    imeOptions = currentImeOptions,
                    isIncognito = isIncognito,
                    keyboardHeight = keyboardHeight,
                    showNumberRow = showNumberRow,
                    hapticEnabled = hapticSetting != "Off",
                    soundEnabled = prefs.soundFeedback != "Off",
                    oneHandedMode = oneHandedMode,
                    suggestions = suggestions,
                    clipboardList = clips,
                    isVoiceListening = isVoiceListening,
                    voiceStatusText = voiceStatusText,
                    voicePartialText = voicePartialText,
                    showSuggestions = showSuggestions,
                    arabicNumerals = arabicNumerals,
                    autoTranslateOnEnter = autoTranslateOnEnter,
                    spacebarLanguageSwitch = spacebarLanguageSwitch,
                    inputType = currentInputType,
                    currentTypedWord = typedWord,
                    currentDraftText = draftText,
                    userWords = userWords,
                    showKeyPreview = showKeyPreview,
                    bottomChinPadding = bottomChinPadding,
                    showToolbarUndoRedo = showToolbarUndoRedo,
                    heightPercent = heightPercent,
                    widthPercent = widthPercent,
                    keyFontSizeSp = keyFontSizeSp,
                    secondaryFontSizeSp = secondaryFontSizeSp,
                    keyCornerRadiusDp = keyCornerRadiusDp,
                    keyStrokeBorderEnabled = keyStrokeBorderEnabled,
                    showArrowRow = showArrowRow,
                    soundType = prefs.soundFeedback,
                    soundVolume = prefs.soundVolume,
                    hapticIntensity = prefs.hapticFeedback,
                    hapticDurationMs = prefs.hapticDurationMs,
                    onTextInput = { text -> handleTextInput(text) },
                    onDelete = { handleDelete() },
                    onDeleteWord = { handleDeleteWord() },
                    onDeleteAll = { handleDeleteAll() },
                    onEnter = { handleEnter(autoTranslateOnEnter) },
                    onLongPressEnter = { handleLongPressEnter() },
                    onSpace = { handleSpace() },
                    onSwitchLanguage = { switchLanguage() },
                    onSelectLanguage = { lang -> selectLanguage(lang) },
                    onMoveCursor = { offset -> handleMoveCursor(offset) },
                    onSelectSuggestion = { suggestion -> handleSelectSuggestion(suggestion) },
                    onTogglePinClip = { id, isPinned -> toggleClipPin(id, isPinned) },
                    onDeleteClip = { id -> deleteClip(id) },
                    onClearUnpinnedClips = { clearUnpinnedClips() },
                    onStartVoice = { startVoiceInput() },
                    onStopVoice = { stopVoiceInput() },
                    onSelectAll = { performSelectAll() },
                    onCut = { performCut() },
                    onCopy = { performCopy() },
                    onPaste = { performPaste() },
                    onUndo = { performUndo() },
                    onRedo = { performRedo() },
                    onOpenSettings = { openSettings() },
                    onToggleOneHanded = { mode -> prefs.oneHandedMode = mode },
                    onToggleAutoTranslate = { enabled -> prefs.autoTranslateOnEnter = enabled },
                    onChangeKeyboardHeight = { height -> prefs.keyboardHeight = height },
                    onApplyAiText = { text -> handleApplyAiText(text) },
                    onAddWordToDictionary = { word -> addWordToDict(word) },
                    onDeleteUserWord = { id -> deleteWordFromDict(id) },
                    onTranslateNow = { source, target -> performManualTranslate(source, target) },
                    onLaunchVoiceActivity = { startVoiceInput() },
                    onChangeKeyboardHeightPercent = { p -> prefs.keyboardHeightPercent = p },
                    onChangeKeyboardWidthPercent = { p -> prefs.keyboardWidthPercent = p },
                    onChangeKeyFontSize = { s -> prefs.keyFontSizeSp = s },
                    onChangeSecondaryFontSize = { s -> prefs.secondaryFontSizeSp = s },
                    onHideKeyboard = { requestHideSelf(0) },
                    onSwitchIme = { switchIme() },
                    onRegisterBackHandler = { handler -> activeBackHandler = handler }
                )
            }
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        currentImeOptions = attribute?.imeOptions ?: EditorInfo.IME_ACTION_DONE
        currentInputType = attribute?.inputType ?: android.text.InputType.TYPE_CLASS_TEXT
        refreshEditorState()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        currentImeOptions = info?.imeOptions ?: EditorInfo.IME_ACTION_DONE
        currentInputType = info?.inputType ?: android.text.InputType.TYPE_CLASS_TEXT
        refreshEditorState()
    }

    private fun refreshEditorState() {
        val ic = currentInputConnection ?: return
        serviceScope.launch(Dispatchers.Default) {
            try {
                val textBefore = ic.getTextBeforeCursor(100, 0)?.toString() ?: ""
                _currentDraftText.value = textBefore

                val word = extractLastWord(textBefore)
                _currentTypedWord.value = word

                if (word.isNotBlank()) {
                    val lang = prefs.currentLanguage
                    val list = suggestionEngine.getSuggestions(word, null, lang == "ar")
                    _suggestions.value = list
                } else {
                    _suggestions.value = emptyList()
                }
            } catch (e: Throwable) {
                Log.w(TAG, "refreshEditorState error: ${e.message}")
            }
        }
    }

    private fun extractLastWord(text: String): String {
        if (text.isEmpty()) return ""
        val lastSpace = text.lastIndexOfAny(charArrayOf(' ', '\n', '\t', '.', ',', '!', '؟', '،', ';', ':'))
        return if (lastSpace >= 0 && lastSpace < text.length - 1) {
            text.substring(lastSpace + 1)
        } else if (lastSpace < 0) {
            text
        } else {
            ""
        }
    }

    private fun handleTextInput(text: String) {
        val ic = currentInputConnection ?: return
        ic.commitText(text, 1)
        refreshEditorState()
    }

    private fun handleSpace() {
        val ic = currentInputConnection ?: return
        val before = ic.getTextBeforeCursor(50, 0)?.toString() ?: ""
        val word = extractLastWord(before).trim()

        if (word.length >= 2 && !prefs.isIncognito) {
            serviceScope.launch(Dispatchers.IO) {
                userWordRepo.learnWord(word)
            }
        }

        ic.commitText(" ", 1)
        refreshEditorState()
    }

    private fun handleDelete() {
        val ic = currentInputConnection ?: return
        val selectedText = ic.getSelectedText(0)
        if (!selectedText.isNullOrEmpty()) {
            ic.commitText("", 1)
        } else {
            ic.deleteSurroundingText(1, 0)
        }
        refreshEditorState()
    }

    private fun handleDeleteWord() {
        val ic = currentInputConnection ?: return
        val before = ic.getTextBeforeCursor(60, 0)?.toString() ?: ""
        if (before.isEmpty()) return

        var i = before.length - 1
        // Skip trailing spaces
        while (i >= 0 && before[i].isWhitespace()) {
            i--
        }
        // Count characters in the last word
        var charsToDelete = (before.length - 1 - i)
        while (i >= 0 && !before[i].isWhitespace()) {
            charsToDelete++
            i--
        }
        if (charsToDelete > 0) {
            ic.deleteSurroundingText(charsToDelete, 0)
        } else {
            ic.deleteSurroundingText(1, 0)
        }
        refreshEditorState()
    }

    private fun handleDeleteAll() {
        val ic = currentInputConnection ?: return
        try {
            ic.performContextMenuAction(android.R.id.selectAll)
            ic.commitText("", 1)
        } catch (e: Throwable) {
            ic.deleteSurroundingText(1000, 1000)
        }
        refreshEditorState()
    }

    private fun handleEnter(autoTranslateOnEnter: Boolean) {
        val ic = currentInputConnection ?: return

        if (autoTranslateOnEnter) {
            val textBefore = ic.getTextBeforeCursor(500, 0)?.toString() ?: ""
            val clean = textBefore.trim()
            if (clean.isNotEmpty()) {
                serviceScope.launch(Dispatchers.IO) {
                    val translated = TranslationEngine.translateAsync(clean, "ar", "en")
                    withContext(Dispatchers.Main) {
                        ic.deleteSurroundingText(textBefore.length, 0)
                        ic.commitText(translated, 1)
                        performImeEnterAction()
                    }
                }
                return
            }
        }

        performImeEnterAction()
    }

    private fun performImeEnterAction() {
        val ic = currentInputConnection ?: return
        val action = currentImeOptions and EditorInfo.IME_MASK_ACTION
        if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
            ic.performEditorAction(action)
        } else {
            val eventTime = SystemClock.uptimeMillis()
            ic.sendKeyEvent(KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0))
            ic.sendKeyEvent(KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0))
        }
        refreshEditorState()
    }

    private fun handleMoveCursor(offset: Int) {
        val ic = currentInputConnection ?: return
        try {
            val extracted = ic.getExtractedText(ExtractedTextRequest(), 0)
            if (extracted != null) {
                val currentPos = extracted.selectionStart
                val newPos = (currentPos + offset).coerceIn(0, extracted.text.length)
                ic.setSelection(newPos, newPos)
            } else {
                if (offset < 0) {
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT))
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT))
                } else {
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT))
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT))
                }
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Cursor movement error: ${e.message}")
        }
    }

    private fun handleSelectSuggestion(suggestion: String) {
        val ic = currentInputConnection ?: return
        val before = ic.getTextBeforeCursor(60, 0)?.toString() ?: ""
        val lastWord = extractLastWord(before)

        if (lastWord.isNotEmpty()) {
            ic.deleteSurroundingText(lastWord.length, 0)
        }
        ic.commitText("$suggestion ", 1)
        refreshEditorState()
    }

    private fun handleApplyAiText(text: String) {
        val ic = currentInputConnection ?: return
        val before = ic.getTextBeforeCursor(1000, 0)?.toString() ?: ""
        if (before.isNotEmpty()) {
            ic.deleteSurroundingText(before.length, 0)
        }
        ic.commitText(text, 1)
        refreshEditorState()
    }

    private fun performManualTranslate(sourceLang: String, targetLang: String) {
        val ic = currentInputConnection ?: return
        val before = ic.getTextBeforeCursor(500, 0)?.toString() ?: ""
        val clean = before.trim()
        if (clean.isEmpty()) return

        serviceScope.launch(Dispatchers.IO) {
            val translated = TranslationEngine.translateAsync(clean, sourceLang, targetLang)
            withContext(Dispatchers.Main) {
                ic.deleteSurroundingText(before.length, 0)
                ic.commitText(translated, 1)
                refreshEditorState()
            }
        }
    }

    private fun handleLongPressEnter() {
        val ic = currentInputConnection ?: return
        val before = ic.getTextBeforeCursor(1000, 0)?.toString() ?: ""
        val clean = before.trim()
        if (clean.isEmpty()) return

        val isArabic = clean.any { it in '\u0600'..'\u06FF' }
        val sourceLang = if (isArabic) "ar" else "en"
        val targetLang = if (isArabic) "en" else "ar"

        serviceScope.launch(Dispatchers.IO) {
            val translated = TranslationEngine.translateAsync(clean, sourceLang, targetLang)
            withContext(Dispatchers.Main) {
                ic.deleteSurroundingText(before.length, 0)
                ic.commitText(translated, 1)
                refreshEditorState()
            }
        }
    }

    private fun switchLanguage() {
        val current = prefs.currentLanguage
        val next = if (current == "ar") "en" else "ar"
        prefs.currentLanguage = next
        refreshEditorState()
    }

    private fun selectLanguage(langCode: String) {
        prefs.currentLanguage = langCode
        refreshEditorState()
    }

    private fun switchIme() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                switchToNextInputMethod(false)
            } else {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                @Suppress("DEPRECATION")
                imm?.switchToNextInputMethod(window?.window?.attributes?.token, false)
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Error switching IME: ${e.message}")
        }
    }

    private fun toggleClipPin(id: Long, isPinned: Boolean) {
        serviceScope.launch(Dispatchers.IO) {
            clipboardRepo.togglePin(id, isPinned)
        }
    }

    private fun deleteClip(id: Long) {
        serviceScope.launch(Dispatchers.IO) {
            clipboardRepo.deleteById(id)
        }
    }

    private fun clearUnpinnedClips() {
        serviceScope.launch(Dispatchers.IO) {
            clipboardRepo.clearUnpinned()
        }
    }

    private fun addWordToDict(word: String) {
        serviceScope.launch(Dispatchers.IO) {
            userWordRepo.learnWord(word)
        }
    }

    private fun deleteWordFromDict(id: Long) {
        serviceScope.launch(Dispatchers.IO) {
            userWordRepo.deleteById(id)
        }
    }

    private fun startVoiceInput() {
        try {
            val intent = Intent(this, VoiceInputActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            _isVoiceListening.value = true
            _voiceStatusText.value = "جاري فتح التعرف على الصوت..."
        } catch (e: Throwable) {
            Log.e(TAG, "Cannot start VoiceInputActivity: ${e.message}")
            _isVoiceListening.value = false
            _voiceStatusText.value = "تعذر تشغيل الصوت"
        }
    }

    private fun stopVoiceInput() {
        _isVoiceListening.value = false
        _voiceStatusText.value = ""
        _voicePartialText.value = ""
    }

    private fun performSelectAll() {
        currentInputConnection?.performContextMenuAction(android.R.id.selectAll)
    }

    private fun performCut() {
        currentInputConnection?.performContextMenuAction(android.R.id.cut)
    }

    private fun performCopy() {
        currentInputConnection?.performContextMenuAction(android.R.id.copy)
    }

    private fun performPaste() {
        currentInputConnection?.performContextMenuAction(android.R.id.paste)
    }

    private fun performUndo() {
        currentInputConnection?.performContextMenuAction(android.R.id.undo)
    }

    private fun performRedo() {
        currentInputConnection?.performContextMenuAction(android.R.id.redo)
    }

    private fun openSettings() {
        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Throwable) {
            Log.e(TAG, "Cannot start MainActivity: ${e.message}")
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (activeBackHandler?.invoke() == true) {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (activeBackHandler != null) {
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        clipChangedListener?.let {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            cm?.removePrimaryClipChangedListener(it)
        }
        serviceJob.cancel()
        if (instance == this) {
            instance = null
        }
    }

    companion object {
        private const val TAG = "KeyboardIME"

        @Volatile
        var instance: KeyboardInputMethodService? = null
            private set

        fun commitVoiceText(text: String) {
            instance?.let { service ->
                service._isVoiceListening.value = false
                service._voiceStatusText.value = ""
                service._voicePartialText.value = ""
                service.currentInputConnection?.commitText(text, 1)
                service.refreshEditorState()
            }
        }

        fun onVoiceError(errorMsg: String) {
            instance?.let { service ->
                service._isVoiceListening.value = false
                service._voiceStatusText.value = errorMsg
                service._voicePartialText.value = ""
            }
        }
    }
}
