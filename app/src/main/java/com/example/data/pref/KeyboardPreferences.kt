package com.example.data.pref

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KeyboardPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _themeState = MutableStateFlow(theme)
    val themeState: StateFlow<String> = _themeState.asStateFlow()

    private val _languageState = MutableStateFlow(currentLanguage)
    val languageState: StateFlow<String> = _languageState.asStateFlow()

    private val _hapticState = MutableStateFlow(hapticFeedback)
    val hapticState: StateFlow<String> = _hapticState.asStateFlow()

    private val _heightState = MutableStateFlow(keyboardHeight)
    val heightState: StateFlow<String> = _heightState.asStateFlow()

    private val _numberRowState = MutableStateFlow(showNumberRow)
    val numberRowState: StateFlow<Boolean> = _numberRowState.asStateFlow()

    private val _incognitoState = MutableStateFlow(isIncognito)
    val incognitoState: StateFlow<Boolean> = _incognitoState.asStateFlow()

    private val _suggestionsState = MutableStateFlow(showSuggestions)
    val suggestionsState: StateFlow<Boolean> = _suggestionsState.asStateFlow()

    private val _autoCorrectState = MutableStateFlow(autoCorrectEnabled)
    val autoCorrectState: StateFlow<Boolean> = _autoCorrectState.asStateFlow()

    private val _arabicNumeralsState = MutableStateFlow(arabicNumerals)
    val arabicNumeralsState: StateFlow<Boolean> = _arabicNumeralsState.asStateFlow()

    private val _autoTranslateState = MutableStateFlow(autoTranslateOnEnter)
    val autoTranslateState: StateFlow<Boolean> = _autoTranslateState.asStateFlow()
    val autoTranslateOnEnterState: StateFlow<Boolean> = _autoTranslateState.asStateFlow()

    private val _spacebarLanguageSwitchState = MutableStateFlow(spacebarLanguageSwitchEnabled)
    val spacebarLanguageSwitchState: StateFlow<Boolean> = _spacebarLanguageSwitchState.asStateFlow()

    private val _oneHandedState = MutableStateFlow(oneHandedMode)
    val oneHandedState: StateFlow<String> = _oneHandedState.asStateFlow()

    private val _customKeyColorState = MutableStateFlow(customKeyColor)
    val customKeyColorState: StateFlow<String> = _customKeyColorState.asStateFlow()

    private val _keyPreviewState = MutableStateFlow(showKeyPreview)
    val keyPreviewState: StateFlow<Boolean> = _keyPreviewState.asStateFlow()

    private val _bottomChinState = MutableStateFlow(bottomChinPadding)
    val bottomChinState: StateFlow<String> = _bottomChinState.asStateFlow()
    val bottomChinPaddingState: StateFlow<String> = _bottomChinState.asStateFlow()

    private val _toolbarUndoRedoState = MutableStateFlow(showToolbarUndoRedo)
    val toolbarUndoRedoState: StateFlow<Boolean> = _toolbarUndoRedoState.asStateFlow()

    private val _heightPercentState = MutableStateFlow(keyboardHeightPercent)
    val heightPercentState: StateFlow<Int> = _heightPercentState.asStateFlow()

    private val _widthPercentState = MutableStateFlow(keyboardWidthPercent)
    val widthPercentState: StateFlow<Int> = _widthPercentState.asStateFlow()

    private val _keyFontSizeSpState = MutableStateFlow(keyFontSizeSp)
    val keyFontSizeSpState: StateFlow<Int> = _keyFontSizeSpState.asStateFlow()

    private val _secondaryFontSizeSpState = MutableStateFlow(secondaryFontSizeSp)
    val secondaryFontSizeSpState: StateFlow<Int> = _secondaryFontSizeSpState.asStateFlow()

    private val _keyCornerRadiusState = MutableStateFlow(keyCornerRadiusDp)
    val keyCornerRadiusState: StateFlow<Int> = _keyCornerRadiusState.asStateFlow()

    private val _keyStrokeBorderState = MutableStateFlow(keyStrokeBorderEnabled)
    val keyStrokeBorderState: StateFlow<Boolean> = _keyStrokeBorderState.asStateFlow()

    private val _arrowRowState = MutableStateFlow(showArrowRow)
    val arrowRowState: StateFlow<Boolean> = _arrowRowState.asStateFlow()

    var keyboardHeightPercent: Int
        get() = prefs.getInt(KEY_HEIGHT_PERCENT, 100)
        set(value) {
            prefs.edit().putInt(KEY_HEIGHT_PERCENT, value).apply()
            _heightPercentState.value = value
        }

    var keyboardWidthPercent: Int
        get() = prefs.getInt(KEY_WIDTH_PERCENT, 100)
        set(value) {
            prefs.edit().putInt(KEY_WIDTH_PERCENT, value).apply()
            _widthPercentState.value = value
        }

    var keyboardWidthAlign: String
        get() = prefs.getString(KEY_WIDTH_ALIGN, "CENTER") ?: "CENTER"
        set(value) = prefs.edit().putString(KEY_WIDTH_ALIGN, value).apply()

    var keyFontSizeSp: Int
        get() = prefs.getInt(KEY_KEY_FONT_SIZE_SP, 19)
        set(value) {
            prefs.edit().putInt(KEY_KEY_FONT_SIZE_SP, value).apply()
            _keyFontSizeSpState.value = value
        }

    var secondaryFontSizeSp: Int
        get() = prefs.getInt(KEY_SECONDARY_FONT_SIZE_SP, 9)
        set(value) {
            prefs.edit().putInt(KEY_SECONDARY_FONT_SIZE_SP, value).apply()
            _secondaryFontSizeSpState.value = value
        }

    var keyCornerRadiusDp: Int
        get() = prefs.getInt(KEY_KEY_CORNER_RADIUS, 7)
        set(value) {
            prefs.edit().putInt(KEY_KEY_CORNER_RADIUS, value).apply()
            _keyCornerRadiusState.value = value
        }

    var keyStrokeBorderEnabled: Boolean
        get() = prefs.getBoolean(KEY_KEY_STROKE_BORDER, false)
        set(value) {
            prefs.edit().putBoolean(KEY_KEY_STROKE_BORDER, value).apply()
            _keyStrokeBorderState.value = value
        }

    var keyGapDp: Int
        get() = prefs.getInt(KEY_KEY_GAP_DP, 2)
        set(value) = prefs.edit().putInt(KEY_KEY_GAP_DP, value).apply()

    var keySoundType: String
        get() = prefs.getString(KEY_SOUND_TYPE, "CLICK") ?: "CLICK"
        set(value) = prefs.edit().putString(KEY_SOUND_TYPE, value).apply()

    var keySoundVolume: Int
        get() = prefs.getInt(KEY_SOUND_VOLUME, 50)
        set(value) = prefs.edit().putInt(KEY_SOUND_VOLUME, value).apply()

    var hapticDurationMs: Int
        get() = prefs.getInt(KEY_HAPTIC_DURATION_MS, 20)
        set(value) = prefs.edit().putInt(KEY_HAPTIC_DURATION_MS, value).apply()

    var showArrowRow: Boolean
        get() = prefs.getBoolean(KEY_SHOW_ARROW_ROW, false)
        set(value) {
            prefs.edit().putBoolean(KEY_SHOW_ARROW_ROW, value).apply()
            _arrowRowState.value = value
        }

    var showBottomRowSymbols: Boolean
        get() = prefs.getBoolean(KEY_BOTTOM_ROW_SYMBOLS, true)
        set(value) = prefs.edit().putBoolean(KEY_BOTTOM_ROW_SYMBOLS, value).apply()

    var clipboardRetentionDays: Int
        get() = prefs.getInt(KEY_CLIPBOARD_RETENTION, 30)
        set(value) = prefs.edit().putInt(KEY_CLIPBOARD_RETENTION, value).apply()

    var clipboardAutoClean: Boolean
        get() = prefs.getBoolean(KEY_CLIPBOARD_AUTO_CLEAN, false)
        set(value) = prefs.edit().putBoolean(KEY_CLIPBOARD_AUTO_CLEAN, value).apply()

    var showKeyPreview: Boolean
        get() = prefs.getBoolean(KEY_SHOW_KEY_PREVIEW, true)
        set(value) {
            prefs.edit().putBoolean(KEY_SHOW_KEY_PREVIEW, value).apply()
            _keyPreviewState.value = value
        }

    var bottomChinPadding: String
        get() = prefs.getString(KEY_BOTTOM_CHIN, "AUTO") ?: "AUTO"
        set(value) {
            prefs.edit().putString(KEY_BOTTOM_CHIN, value).apply()
            _bottomChinState.value = value
        }

    var showToolbarUndoRedo: Boolean
        get() = prefs.getBoolean(KEY_TOOLBAR_UNDO_REDO, true)
        set(value) {
            prefs.edit().putBoolean(KEY_TOOLBAR_UNDO_REDO, value).apply()
            _toolbarUndoRedoState.value = value
        }

    var customKeyColor: String
        get() = prefs.getString(KEY_CUSTOM_KEY_COLOR, "default") ?: "default"
        set(value) {
            prefs.edit().putString(KEY_CUSTOM_KEY_COLOR, value).apply()
            _customKeyColorState.value = value
        }

    var theme: String
        get() = prefs.getString(KEY_THEME, "Midnight") ?: "Midnight"
        set(value) {
            prefs.edit().putString(KEY_THEME, value).apply()
            _themeState.value = value
        }

    var currentLanguage: String
        get() = prefs.getString(KEY_CURRENT_LANG, "ar") ?: "ar"
        set(value) {
            prefs.edit().putString(KEY_CURRENT_LANG, value).apply()
            _languageState.value = value
        }

    var enabledLanguages: Set<String>
        get() = prefs.getStringSet(KEY_ENABLED_LANGS, setOf("ar", "en")) ?: setOf("ar", "en")
        set(value) {
            prefs.edit().putStringSet(KEY_ENABLED_LANGS, value).apply()
        }

    var keyboardHeight: String
        get() = prefs.getString(KEY_HEIGHT, "Medium") ?: "Medium"
        set(value) {
            prefs.edit().putString(KEY_HEIGHT, value).apply()
            _heightState.value = value
        }

    var fontSize: String
        get() = prefs.getString(KEY_FONT_SIZE, "Medium") ?: "Medium"
        set(value) = prefs.edit().putString(KEY_FONT_SIZE, value).apply()

    var hapticFeedback: String
        get() = prefs.getString(KEY_HAPTIC, "Light") ?: "Light"
        set(value) {
            prefs.edit().putString(KEY_HAPTIC, value).apply()
            _hapticState.value = value
        }

    var keySound: String
        get() = prefs.getString(KEY_SOUND, "Off") ?: "Off"
        set(value) = prefs.edit().putString(KEY_SOUND, value).apply()

    var soundFeedback: String
        get() = prefs.getString(KEY_SOUND_TYPE, keySound) ?: keySound
        set(value) {
            prefs.edit().putString(KEY_SOUND_TYPE, value).putString(KEY_SOUND, value).apply()
        }

    var soundVolume: Int
        get() = prefs.getInt(KEY_SOUND_VOLUME, 50)
        set(value) = prefs.edit().putInt(KEY_SOUND_VOLUME, value).apply()

    var showNumberRow: Boolean
        get() = prefs.getBoolean(KEY_NUMBER_ROW, true)
        set(value) {
            prefs.edit().putBoolean(KEY_NUMBER_ROW, value).apply()
            _numberRowState.value = value
        }

    var doubleSpacePeriod: Boolean
        get() = prefs.getBoolean(KEY_DOUBLE_SPACE, true)
        set(value) = prefs.edit().putBoolean(KEY_DOUBLE_SPACE, value).apply()

    var autoCapitalization: Boolean
        get() = prefs.getBoolean(KEY_AUTO_CAPS, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_CAPS, value).apply()

    var autoCorrectEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTOCORRECT_ENABLED, true)
        set(value) {
            prefs.edit().putBoolean(KEY_AUTOCORRECT_ENABLED, value).apply()
            _autoCorrectState.value = value
        }

    var autoCorrectStrength: String
        get() = prefs.getString(KEY_AUTOCORRECT, "Medium") ?: "Medium"
        set(value) = prefs.edit().putString(KEY_AUTOCORRECT, value).apply()

    var aiTone: String
        get() = prefs.getString(KEY_AI_TONE, "Formal") ?: "Formal"
        set(value) = prefs.edit().putString(KEY_AI_TONE, value).apply()

    var geminiApiKey: String
        get() = prefs.getString(KEY_GEMINI_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_GEMINI_KEY, value).apply()

    var spacebarSwipe: String
        get() = prefs.getString(KEY_SPACE_SWIPE, "SWITCH_LANG") ?: "SWITCH_LANG"
        set(value) = prefs.edit().putString(KEY_SPACE_SWIPE, value).apply()

    var spacebarLanguageSwitchEnabled: Boolean
        get() = prefs.getBoolean(KEY_SPACEBAR_LANG_SWITCH, true)
        set(value) {
            prefs.edit().putBoolean(KEY_SPACEBAR_LANG_SWITCH, value).apply()
            _spacebarLanguageSwitchState.value = value
        }

    var isIncognito: Boolean
        get() = prefs.getBoolean(KEY_INCOGNITO, false)
        set(value) {
            prefs.edit().putBoolean(KEY_INCOGNITO, value).apply()
            _incognitoState.value = value
        }

    var isGamingMode: Boolean
        get() = prefs.getBoolean(KEY_GAMING, false)
        set(value) = prefs.edit().putBoolean(KEY_GAMING, value).apply()

    var oneHandedMode: String
        get() = prefs.getString(KEY_ONE_HANDED, "OFF") ?: "OFF"
        set(value) {
            prefs.edit().putString(KEY_ONE_HANDED, value).apply()
            _oneHandedState.value = value
        }

    private val cachedWordCount = java.util.concurrent.atomic.AtomicLong(prefs.getLong(KEY_WORD_COUNT, 0L))
    private val pendingCountDelta = java.util.concurrent.atomic.AtomicInteger(0)

    var wordsTypedCount: Long
        get() = cachedWordCount.get()
        set(value) {
            cachedWordCount.set(value)
            prefs.edit().putLong(KEY_WORD_COUNT, value).apply()
        }

    fun incrementWordCount() {
        if (!isIncognito) {
            cachedWordCount.incrementAndGet()
            if (pendingCountDelta.incrementAndGet() >= 20) {
                flushWordCount()
            }
        }
    }

    fun flushWordCount() {
        if (pendingCountDelta.getAndSet(0) > 0) {
            prefs.edit().putLong(KEY_WORD_COUNT, cachedWordCount.get()).apply()
        }
    }

    var showSuggestions: Boolean
        get() = prefs.getBoolean(KEY_SHOW_SUGGESTIONS, true)
        set(value) {
            prefs.edit().putBoolean(KEY_SHOW_SUGGESTIONS, value).apply()
            _suggestionsState.value = value
        }

    var arabicNumerals: Boolean
        get() = prefs.getBoolean(KEY_ARABIC_NUMERALS, true)
        set(value) {
            prefs.edit().putBoolean(KEY_ARABIC_NUMERALS, value).apply()
            _arabicNumeralsState.value = value
        }

    var autoTranslateOnEnter: Boolean
        get() = prefs.getBoolean(KEY_AUTO_TRANSLATE_ENTER, false)
        set(value) {
            prefs.edit().putBoolean(KEY_AUTO_TRANSLATE_ENTER, value).apply()
            _autoTranslateState.value = value
        }

    var translateSourceLang: String
        get() = prefs.getString(KEY_TRANSLATE_SOURCE, "ar") ?: "ar"
        set(value) = prefs.edit().putString(KEY_TRANSLATE_SOURCE, value).apply()

    var translateTargetLang: String
        get() = prefs.getString(KEY_TRANSLATE_TARGET, "en") ?: "en"
        set(value) = prefs.edit().putString(KEY_TRANSLATE_TARGET, value).apply()

    var sourceTranslateLang: String
        get() = translateSourceLang
        set(value) { translateSourceLang = value }

    var targetTranslateLang: String
        get() = translateTargetLang
        set(value) { translateTargetLang = value }

    companion object {
        private const val PREFS_NAME = "keyboard_pro_prefs"
        private const val KEY_THEME = "theme"
        private const val KEY_CURRENT_LANG = "current_lang"
        private const val KEY_ENABLED_LANGS = "enabled_langs"
        private const val KEY_HEIGHT = "keyboard_height"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_HAPTIC = "haptic_feedback"
        private const val KEY_SOUND = "key_sound"
        private const val KEY_NUMBER_ROW = "show_number_row"
        private const val KEY_DOUBLE_SPACE = "double_space_period"
        private const val KEY_AUTO_CAPS = "auto_capitalization"
        private const val KEY_AUTOCORRECT = "autocorrect_strength"
        private const val KEY_AUTOCORRECT_ENABLED = "autocorrect_enabled"
        private const val KEY_AI_TONE = "ai_tone_selected"
        private const val KEY_GEMINI_KEY = "gemini_api_key_custom"
        private const val KEY_SPACE_SWIPE = "spacebar_swipe"
        private const val KEY_SPACEBAR_LANG_SWITCH = "spacebar_lang_switch"
        private const val KEY_INCOGNITO = "is_incognito"
        private const val KEY_GAMING = "is_gaming_mode"
        private const val KEY_ONE_HANDED = "one_handed_mode"
        private const val KEY_WORD_COUNT = "words_typed_count"
        private const val KEY_SHOW_SUGGESTIONS = "show_suggestions"
        private const val KEY_ARABIC_NUMERALS = "arabic_numerals"
        private const val KEY_AUTO_TRANSLATE_ENTER = "auto_translate_on_enter"
        private const val KEY_TRANSLATE_SOURCE = "translate_source_lang"
        private const val KEY_TRANSLATE_TARGET = "translate_target_lang"
        private const val KEY_CUSTOM_KEY_COLOR = "custom_key_color"
        private const val KEY_CUSTOM_THEME_BG = "custom_theme_bg"
        private const val KEY_CUSTOM_THEME_KEY_BG = "custom_theme_key_bg"
        private const val KEY_CUSTOM_THEME_KEY_TEXT = "custom_theme_key_text"
        private const val KEY_CUSTOM_THEME_SPECIAL_BG = "custom_theme_special_bg"
        private const val KEY_CUSTOM_THEME_SPECIAL_TEXT = "custom_theme_special_text"
        private const val KEY_CUSTOM_THEME_ACCENT = "custom_theme_accent"
        private const val KEY_CUSTOM_THEME_SUGGESTION_BG = "custom_theme_suggestion_bg"
        private const val KEY_CUSTOM_THEME_SUGGESTION_TEXT = "custom_theme_suggestion_text"
        private const val KEY_EMOJI_STYLE = "emoji_style_version"
        private const val KEY_RECENT_EMOJIS = "recent_emojis_list"
        private const val KEY_SHOW_KEY_PREVIEW = "show_key_preview"
        private const val KEY_BOTTOM_CHIN = "bottom_chin_padding"
        private const val KEY_TOOLBAR_UNDO_REDO = "show_toolbar_undo_redo"
        private const val KEY_HEIGHT_PERCENT = "keyboard_height_percent"
        private const val KEY_WIDTH_PERCENT = "keyboard_width_percent"
        private const val KEY_WIDTH_ALIGN = "keyboard_width_align"
        private const val KEY_KEY_FONT_SIZE_SP = "key_font_size_sp"
        private const val KEY_SECONDARY_FONT_SIZE_SP = "secondary_font_size_sp"
        private const val KEY_KEY_CORNER_RADIUS = "key_corner_radius_dp"
        private const val KEY_KEY_STROKE_BORDER = "key_stroke_border_enabled"
        private const val KEY_KEY_GAP_DP = "key_gap_dp"
        private const val KEY_SOUND_TYPE = "key_sound_type"
        private const val KEY_SOUND_VOLUME = "key_sound_volume"
        private const val KEY_HAPTIC_DURATION_MS = "haptic_duration_ms"
        private const val KEY_SHOW_ARROW_ROW = "show_arrow_row"
        private const val KEY_BOTTOM_ROW_SYMBOLS = "bottom_row_symbols"
        private const val KEY_CLIPBOARD_RETENTION = "clipboard_retention_days"
        private const val KEY_CLIPBOARD_AUTO_CLEAN = "clipboard_auto_clean"
    }

    var emojiStyle: String
        get() = prefs.getString(KEY_EMOJI_STYLE, "iOS 27") ?: "iOS 27"
        set(value) = prefs.edit().putString(KEY_EMOJI_STYLE, value).apply()

    fun getRecentEmojis(): List<String> {
        val raw = prefs.getString(KEY_RECENT_EMOJIS, "") ?: ""
        if (raw.isBlank()) return emptyList()
        return raw.split(",").filter { it.isNotBlank() }
    }

    fun addRecentEmoji(emoji: String) {
        if (emoji.isBlank()) return
        val current = getRecentEmojis().toMutableList()
        current.remove(emoji)
        current.add(0, emoji)
        val limited = current.take(30)
        prefs.edit().putString(KEY_RECENT_EMOJIS, limited.joinToString(",")).apply()
    }

    var customThemeBg: String
        get() = prefs.getString(KEY_CUSTOM_THEME_BG, "#121824") ?: "#121824"
        set(value) = prefs.edit().putString(KEY_CUSTOM_THEME_BG, value).apply()

    var customThemeKeyBg: String
        get() = prefs.getString(KEY_CUSTOM_THEME_KEY_BG, "#1F293D") ?: "#1F293D"
        set(value) = prefs.edit().putString(KEY_CUSTOM_THEME_KEY_BG, value).apply()

    var customThemeKeyText: String
        get() = prefs.getString(KEY_CUSTOM_THEME_KEY_TEXT, "#FFFFFF") ?: "#FFFFFF"
        set(value) = prefs.edit().putString(KEY_CUSTOM_THEME_KEY_TEXT, value).apply()

    var customThemeSpecialBg: String
        get() = prefs.getString(KEY_CUSTOM_THEME_SPECIAL_BG, "#162032") ?: "#162032"
        set(value) = prefs.edit().putString(KEY_CUSTOM_THEME_SPECIAL_BG, value).apply()

    var customThemeSpecialText: String
        get() = prefs.getString(KEY_CUSTOM_THEME_SPECIAL_TEXT, "#60A5FA") ?: "#60A5FA"
        set(value) = prefs.edit().putString(KEY_CUSTOM_THEME_SPECIAL_TEXT, value).apply()

    var customThemeAccent: String
        get() = prefs.getString(KEY_CUSTOM_THEME_ACCENT, "#38BDF8") ?: "#38BDF8"
        set(value) = prefs.edit().putString(KEY_CUSTOM_THEME_ACCENT, value).apply()

    var customThemeSuggestionBg: String
        get() = prefs.getString(KEY_CUSTOM_THEME_SUGGESTION_BG, "#121824") ?: "#121824"
        set(value) = prefs.edit().putString(KEY_CUSTOM_THEME_SUGGESTION_BG, value).apply()

    var customThemeSuggestionText: String
        get() = prefs.getString(KEY_CUSTOM_THEME_SUGGESTION_TEXT, "#E2E8F0") ?: "#E2E8F0"
        set(value) = prefs.edit().putString(KEY_CUSTOM_THEME_SUGGESTION_TEXT, value).apply()

    fun saveCustomTheme(
        bg: String,
        keyBg: String,
        keyText: String,
        specialBg: String,
        specialText: String,
        accent: String,
        suggestionBg: String,
        suggestionText: String
    ) {
        prefs.edit()
            .putString(KEY_CUSTOM_THEME_BG, bg)
            .putString(KEY_CUSTOM_THEME_KEY_BG, keyBg)
            .putString(KEY_CUSTOM_THEME_KEY_TEXT, keyText)
            .putString(KEY_CUSTOM_THEME_SPECIAL_BG, specialBg)
            .putString(KEY_CUSTOM_THEME_SPECIAL_TEXT, specialText)
            .putString(KEY_CUSTOM_THEME_ACCENT, accent)
            .putString(KEY_CUSTOM_THEME_SUGGESTION_BG, suggestionBg)
            .putString(KEY_CUSTOM_THEME_SUGGESTION_TEXT, suggestionText)
            .putString(KEY_THEME, "Custom")
            .apply()
        _themeState.value = "Custom"
    }

    fun getCustomColorScheme(): com.example.ime.theme.KeyboardColorScheme {
        fun parseColor(hex: String, defaultColor: androidx.compose.ui.graphics.Color): androidx.compose.ui.graphics.Color {
            return try {
                androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(hex))
            } catch (e: Throwable) {
                defaultColor
            }
        }
        val accentColor = parseColor(customThemeAccent, androidx.compose.ui.graphics.Color(0xFF38BDF8))
        return com.example.ime.theme.KeyboardColorScheme(
            name = "Custom",
            isDark = true,
            background = parseColor(customThemeBg, androidx.compose.ui.graphics.Color(0xFF121824)),
            keyBackground = parseColor(customThemeKeyBg, androidx.compose.ui.graphics.Color(0xFF1F293D)),
            keyText = parseColor(customThemeKeyText, androidx.compose.ui.graphics.Color(0xFFFFFFFF)),
            specialKeyBackground = parseColor(customThemeSpecialBg, androidx.compose.ui.graphics.Color(0xFF162032)),
            specialKeyText = parseColor(customThemeSpecialText, androidx.compose.ui.graphics.Color(0xFF60A5FA)),
            accent = accentColor,
            suggestionBar = parseColor(customThemeSuggestionBg, androidx.compose.ui.graphics.Color(0xFF121824)),
            suggestionText = parseColor(customThemeSuggestionText, androidx.compose.ui.graphics.Color(0xFFE2E8F0)),
            borderColor = accentColor.copy(alpha = 0.35f)
        )
    }
}
