package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.ViewSidebar
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ime.InputConnectionDispatcher
import com.example.core.layout.KeyboardLayoutType
import com.example.core.theme.KeyboardMode
import com.example.core.theme.KeyboardPreferences
import com.example.core.theme.LocalNovaColors
import com.example.core.theme.NovaTheme
import com.example.core.theme.NovaThemePalette
import com.example.core.theme.OneHandedSide
import com.example.core.theme.getColorsForPalette
import com.example.data.db.ClipboardClip
import com.example.data.db.SmartClipboardManager
import com.example.ui.keyboard.KeyboardRootView
import com.example.ui.theme.AmoledDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NOVAKeyboardTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NOVAKeyboardTheme {
                NovaAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaAppScreen() {
    val context = LocalContext.current
    val prefs = remember { KeyboardPreferences(context) }
    val proPrefs = remember { (context.applicationContext as? KeyboardProApp)?.preferences ?: com.example.data.pref.KeyboardPreferences(context) }
    val clipboardManager = remember { SmartClipboardManager(context) }
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    BackHandler(enabled = selectedTab != 0) {
        selectedTab = 0
    }
    var currentTheme by remember { mutableStateOf(prefs.currentTheme) }
    var currentMode by remember { mutableStateOf(prefs.keyboardMode) }
    var isHapticEnabled by remember { mutableStateOf(prefs.hapticFeedback) }
    var isAudioEnabled by remember { mutableStateOf(prefs.audioFeedback) }
    var isGlowEnabled by remember { mutableStateOf(prefs.neonGlowEffect) }
    var isHarakatEnabled by remember { mutableStateOf(prefs.harakatShortcutRow) }
    var keyHeight by remember { mutableFloatStateOf(prefs.keyHeightDp.toFloat()) }
    var oneHandedSide by remember { mutableStateOf(prefs.oneHandedSide) }
    var oneHandedScale by remember { mutableFloatStateOf(prefs.oneHandedScale) }

    // Live Sandbox Text Field State
    var sandboxText by remember { mutableStateOf("مرحباً بك في NOVA Keyboard! لوحة المفاتيح السيبرانية فائقة السرعة ⚡") }
    var showEmbeddedKeyboard by remember { mutableStateOf(true) }

    // Dispatcher for the in-app interactive sandbox
    val sandboxDispatcher = remember {
        InputConnectionDispatcher(
            inputConnectionProvider = { null },
            onCustomCommitFallback = { textToAppend ->
                sandboxText += textToAppend
            },
            onCustomDeleteFallback = {
                if (sandboxText.isNotEmpty()) {
                    sandboxText = sandboxText.dropLast(1)
                }
            }
        )
    }

    val colors = remember(currentTheme) { getColorsForPalette(currentTheme) }

    CompositionLocalProvider(LocalNovaColors provides colors) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.linearGradient(listOf(CyberCyan, CyberPink))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("N", color = AmoledDark, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "NOVA KEYBOARD",
                                    color = colors.accentPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Cyber-AMOLED Input Engine",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                val all = NovaThemePalette.values()
                                val next = all[(currentTheme.ordinal + 1) % all.size]
                                currentTheme = next
                                prefs.currentTheme = next
                            }
                        ) {
                            Icon(Icons.Default.ColorLens, contentDescription = "Themes", tint = colors.accentPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.background,
                        titleContentColor = colors.accentPrimary
                    )
                )
            },
            containerColor = colors.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Navigation Tabs
                val tabTitles = listOf("تجربة اللوحة", "المظاهر", "الأوضاع", "الحافظة", "الإعدادات")
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = colors.surface,
                    contentColor = colors.accentPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = colors.accentPrimary
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) colors.accentPrimary else TextMuted
                                )
                            }
                        )
                    }
                }

                // Tab Content
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> SandboxTab(
                            sandboxText = sandboxText,
                            onTextChange = { sandboxText = it },
                            showKeyboard = showEmbeddedKeyboard,
                            onToggleKeyboard = { showEmbeddedKeyboard = !showEmbeddedKeyboard },
                            dispatcher = sandboxDispatcher,
                            proPrefs = proPrefs,
                            onEnableIme = {
                                context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                            },
                            onSelectIme = {
                                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.showInputMethodPicker()
                            }
                        )
                        1 -> ThemesTab(
                            selectedTheme = currentTheme,
                            proPrefs = proPrefs,
                            onThemeSelected = {
                                currentTheme = it
                                prefs.currentTheme = it
                            }
                        )
                        2 -> ModesTab(
                            currentMode = currentMode,
                            oneHandedSide = oneHandedSide,
                            oneHandedScale = oneHandedScale,
                            onModeSelected = {
                                currentMode = it
                                prefs.keyboardMode = it
                            },
                            onSideChange = {
                                oneHandedSide = it
                                prefs.oneHandedSide = it
                            },
                            onScaleChange = {
                                oneHandedScale = it
                                prefs.oneHandedScale = it
                            }
                        )
                        3 -> ClipboardTab(clipboardManager = clipboardManager)
                        4 -> SettingsTab(
                            isHaptic = isHapticEnabled,
                            isAudio = isAudioEnabled,
                            isGlow = isGlowEnabled,
                            isHarakat = isHarakatEnabled,
                            keyHeight = keyHeight,
                            onHapticChange = {
                                isHapticEnabled = it
                                prefs.hapticFeedback = it
                            },
                            onAudioChange = {
                                isAudioEnabled = it
                                prefs.audioFeedback = it
                            },
                            onGlowChange = {
                                isGlowEnabled = it
                                prefs.neonGlowEffect = it
                            },
                            onHarakatChange = {
                                isHarakatEnabled = it
                                prefs.harakatShortcutRow = it
                            },
                            onKeyHeightChange = {
                                keyHeight = it
                                prefs.keyHeightDp = it.toInt()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SandboxTab(
    sandboxText: String,
    onTextChange: (String) -> Unit,
    showKeyboard: Boolean,
    onToggleKeyboard: () -> Unit,
    dispatcher: InputConnectionDispatcher,
    proPrefs: com.example.data.pref.KeyboardPreferences,
    onEnableIme: () -> Unit,
    onSelectIme: () -> Unit
) {
    val colors = NovaTheme.colors
    var activeEngine by remember { mutableStateOf("MOHAMMED") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Top Sandbox Input Card & System Enable Prompts
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // IME Activation Quick Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEnableIme,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.accentPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.accentPrimary)
                ) {
                    Text("1. تفعيل اللوحة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSelectIme,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentPrimary)
                ) {
                    Text("2. تعيين كافتراضية", color = AmoledDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Real Live Sandbox Field
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.accentPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ حقل الاختبار المباشر (Sandbox)",
                            color = colors.accentPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        Row {
                            Text(
                                text = "مسح",
                                color = colors.accentSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clickable { onTextChange("") }
                                    .padding(horizontal = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = sandboxText,
                        onValueChange = onTextChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        placeholder = { Text("اكتب هنا أو استخدم لوحة المفاتيح بالأسفل...", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.accentPrimary,
                            unfocusedBorderColor = colors.keyBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = colors.keyBackground,
                            unfocusedContainerColor = colors.keyBackground
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Quick Sample Text Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        QuickSampleChip("✨ نص تجريبي عربي") {
                            onTextChange("مرحباً بك في NOVA Keyboard! كتابة فائقة السرعة مع التشكيل الكامل (فَتْحَةٌ وضَمَّة).")
                        }
                        QuickSampleChip("🚀 English Cyberpunk") {
                            onTextChange("NOVA Keyboard: 120Hz sub-15ms touch latency AMOLED engine.")
                        }
                        QuickSampleChip("🔒 OTP: 849201") {
                            onTextChange("رمز التحقق OTP: 849201 صالح لمدة 15 دقيقة.")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Switcher between Mohammed v.1 and NOVA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeEngine == "MOHAMMED") colors.accentPrimary else colors.surface)
                        .clickable { activeEngine = "MOHAMMED" }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✨ كيبورد محمد v.1 (شامل)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeEngine == "MOHAMMED") AmoledDark else colors.keyText
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeEngine == "NOVA") colors.accentPrimary else colors.surface)
                        .clickable { activeEngine = "NOVA" }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡ NOVA السيبراني (سريع)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeEngine == "NOVA") AmoledDark else colors.keyText
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Toggle Interactive Keyboard Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surface)
                    .clickable { onToggleKeyboard() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showKeyboard) "إخفاء لوحة المفاتيح التفاعلية" else "عرض لوحة المفاتيح التفاعلية",
                    color = colors.keyText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (showKeyboard) "▲" else "▼",
                    color = colors.accentPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Live Embedded Interactive Keyboard
        AnimatedVisibility(visible = showKeyboard) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp)
                    .background(colors.background)
                    .border(1.dp, colors.accentPrimary.copy(alpha = 0.3f))
            ) {
                if (activeEngine == "MOHAMMED") {
                    val currentThemeName by proPrefs.themeState.collectAsState()
                    val colorScheme = com.example.ime.theme.KeyboardThemes.getTheme(currentThemeName, proPrefs)
                    val currentLang by proPrefs.languageState.collectAsState()

                    com.example.ime.ui.KeyboardScreen(
                        colorScheme = colorScheme,
                        currentLanguage = currentLang,
                        imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_DONE,
                        isIncognito = false,
                        keyboardHeight = proPrefs.keyboardHeight,
                        showNumberRow = proPrefs.showNumberRow,
                        hapticEnabled = proPrefs.hapticFeedback != "Off",
                        soundEnabled = false,
                        oneHandedMode = proPrefs.oneHandedMode,
                        suggestions = listOf("السلام عليكم", "شكراً جزيلاً", "أهلاً وسهلاً", "إن شاء الله"),
                        clipboardList = emptyList(),
                        isVoiceListening = false,
                        voiceStatusText = "",
                        voicePartialText = "",
                        onTextInput = { onTextChange(sandboxText + it) },
                        onDelete = {
                            if (sandboxText.isNotEmpty()) {
                                onTextChange(sandboxText.dropLast(1))
                            }
                        },
                        onDeleteWord = {
                            val trimmed = sandboxText.trimEnd()
                            val lastSpace = trimmed.lastIndexOfAny(charArrayOf(' ', '\n', '\t'))
                            onTextChange(if (lastSpace >= 0) trimmed.substring(0, lastSpace + 1) else "")
                        },
                        onDeleteAll = { onTextChange("") },
                        onEnter = { onTextChange(sandboxText + "\n") },
                        onSpace = { onTextChange(sandboxText + " ") },
                        onSwitchLanguage = {
                            proPrefs.currentLanguage = if (proPrefs.currentLanguage == "ar") "en" else "ar"
                        },
                        onSelectLanguage = { proPrefs.currentLanguage = it },
                        onMoveCursor = {},
                        onSelectSuggestion = { onTextChange(sandboxText + "$it ") },
                        onTogglePinClip = { _, _ -> },
                        onDeleteClip = {},
                        onClearUnpinnedClips = {},
                        onStartVoice = {},
                        onStopVoice = {},
                        onSelectAll = {},
                        onCut = {},
                        onCopy = {},
                        onPaste = {},
                        onUndo = {},
                        onRedo = {},
                        onOpenSettings = {},
                        onToggleOneHanded = { proPrefs.oneHandedMode = it },
                        onApplyAiText = { onTextChange(it) },
                        onTranslateNow = { src, tgt ->
                            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                val res = com.example.engine.TranslationEngine.translateAsync(sandboxText, src, tgt)
                                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                    onTextChange(res)
                                }
                            }
                        },
                        onHideKeyboard = onToggleKeyboard
                    )
                } else {
                    KeyboardRootView(
                        dispatcher = dispatcher,
                        onDismissRequest = onToggleKeyboard
                    )
                }
            }
        }
    }
}

@Composable
fun QuickSampleChip(label: String, onClick: () -> Unit) {
    val colors = NovaTheme.colors
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(colors.keyBackground)
            .border(0.5.dp, colors.keyBorder, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, color = colors.keyTextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun ThemesTab(
    selectedTheme: NovaThemePalette,
    proPrefs: com.example.data.pref.KeyboardPreferences,
    onThemeSelected: (NovaThemePalette) -> Unit
) {
    val colors = NovaTheme.colors
    val currentProTheme by proPrefs.themeState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "🎨 مظاهر كيبورد محمد v.1 (18+ مظهراً)",
            color = colors.accentPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        // Mohammed v.1 Themes Grid
        com.example.ime.theme.KeyboardThemes.allThemes.forEach { scheme ->
            val isSelected = currentProTheme.equals(scheme.name, ignoreCase = true)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) scheme.accent else colors.keyBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        proPrefs.theme = scheme.name
                    },
                colors = CardDefaults.cardColors(containerColor = scheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = scheme.name,
                            color = scheme.keyText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "تصنيف: ${scheme.category}",
                            color = scheme.keyText.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(22.dp).clip(CircleShape).background(scheme.accent))
                        Box(modifier = Modifier.size(22.dp).clip(CircleShape).background(scheme.keyBackground))
                        Box(modifier = Modifier.size(22.dp).clip(CircleShape).background(scheme.specialKeyBackground))
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = scheme.accent)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "⚡ مظاهر NOVA Cyberpunk AMOLED",
            color = colors.accentSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        NovaThemePalette.values().forEach { palette ->
            val isSelected = selectedTheme == palette
            val paletteColors = getColorsForPalette(palette)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) paletteColors.accentPrimary else colors.keyBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onThemeSelected(palette) },
                colors = CardDefaults.cardColors(containerColor = paletteColors.background)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = palette.titleAr,
                            color = paletteColors.keyText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = palette.titleEn,
                            color = paletteColors.keyTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(22.dp).clip(CircleShape).background(paletteColors.accentPrimary))
                        Box(modifier = Modifier.size(22.dp).clip(CircleShape).background(paletteColors.accentSecondary))
                        Box(modifier = Modifier.size(22.dp).clip(CircleShape).background(paletteColors.keyBackground))
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = paletteColors.accentPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModesTab(
    currentMode: KeyboardMode,
    oneHandedSide: OneHandedSide,
    oneHandedScale: Float,
    onModeSelected: (KeyboardMode) -> Unit,
    onSideChange: (OneHandedSide) -> Unit,
    onScaleChange: (Float) -> Unit
) {
    val colors = NovaTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "أوضاع الشاشات الذكية والأرغونوميا",
            color = colors.accentPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        KeyboardMode.values().forEach { mode ->
            val isSelected = currentMode == mode
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) colors.accentPrimary else colors.keyBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onModeSelected(mode) },
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = mode.titleAr,
                            color = colors.keyText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = mode.titleEn,
                            color = colors.keyTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colors.accentPrimary)
                    }
                }
            }
        }

        // Additional mode controls for One-Handed
        if (currentMode == KeyboardMode.ONE_HANDED) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("إعدادات وضع اليد الواحدة", color = colors.accentPrimary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onSideChange(OneHandedSide.RIGHT) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (oneHandedSide == OneHandedSide.RIGHT) colors.accentPrimary.copy(alpha = 0.2f) else Color.Transparent,
                                contentColor = colors.accentPrimary
                            )
                        ) {
                            Text("جهة اليمين (Right)")
                        }

                        OutlinedButton(
                            onClick = { onSideChange(OneHandedSide.LEFT) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (oneHandedSide == OneHandedSide.LEFT) colors.accentPrimary.copy(alpha = 0.2f) else Color.Transparent,
                                contentColor = colors.accentPrimary
                            )
                        ) {
                            Text("جهة اليسار (Left)")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("نسبة العرض: ${(oneHandedScale * 100).toInt()}%", color = colors.keyTextSecondary, fontSize = 12.sp)
                    Slider(
                        value = oneHandedScale,
                        onValueChange = onScaleChange,
                        valueRange = 0.65f..0.85f,
                        colors = SliderDefaults.colors(thumbColor = colors.accentPrimary, activeTrackColor = colors.accentPrimary)
                    )
                }
            }
        }
    }
}

@Composable
fun ClipboardTab(
    clipboardManager: SmartClipboardManager
) {
    val colors = NovaTheme.colors
    val scope = rememberCoroutineScope()
    val allClips by clipboardManager.getAllClips().collectAsState(initial = emptyList())
    var newClipText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "الحافظة الذكية المشفرة محلياً (Smart Clipboard)",
            color = colors.accentPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        // Add custom clip card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("إضافة نص جديد للحافظة", color = colors.keyText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = newClipText,
                    onValueChange = { newClipText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("أدخل نصاً لحفظه مشفراً...", color = TextMuted, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentPrimary,
                        unfocusedBorderColor = colors.keyBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = colors.keyBackground,
                        unfocusedContainerColor = colors.keyBackground
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (newClipText.isNotBlank()) {
                            scope.launch {
                                clipboardManager.addClip(newClipText)
                                newClipText = ""
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentPrimary)
                ) {
                    Text("حفظ في الحافظة", color = AmoledDark, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Privacy banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surface)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Security, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "جميع العناصر مخزنة محلياً في قاعدة بيانات Room دون أي نقل عبر الإنترنت. العناصر الحساسة (كلمات المرور/OTP) تُحذف تلقائياً بعد 15 دقيقة.",
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        // Saved clips list
        Text("العناصر المحفوظة (${allClips.size})", color = colors.accentSecondary, fontWeight = FontWeight.Bold)

        allClips.forEach { clip ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (clip.isSensitive) "•••••••• (عنصر سري: OTP/كلمة مرور)" else clip.text,
                            color = colors.keyText,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "تصنيف: ${clip.category} • ${if (clip.isPinned) "📌 مثبت" else "عادي"}",
                            color = colors.keyTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Row {
                        IconButton(onClick = {
                            scope.launch { clipboardManager.togglePin(clip.id, clip.isPinned) }
                        }) {
                            Icon(
                                Icons.Default.PushPin,
                                contentDescription = "Pin",
                                tint = if (clip.isPinned) colors.accentSecondary else colors.keyTextSecondary
                            )
                        }

                        IconButton(onClick = {
                            scope.launch { clipboardManager.deleteClip(clip.id) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = colors.keyTextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsTab(
    isHaptic: Boolean,
    isAudio: Boolean,
    isGlow: Boolean,
    isHarakat: Boolean,
    keyHeight: Float,
    onHapticChange: (Boolean) -> Unit,
    onAudioChange: (Boolean) -> Unit,
    onGlowChange: (Boolean) -> Unit,
    onHarakatChange: (Boolean) -> Unit,
    onKeyHeightChange: (Float) -> Unit
) {
    val colors = NovaTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("تفضيلات واستجابة اللوحة (Preferences & Haptics)", color = colors.accentPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)

        SettingToggleRow(
            title = "الاهتزاز اللمسي (Haptic Feedback)",
            subtitle = "اهتزاز فائق النعومة عند الضغط على المفاتيح",
            icon = Icons.Default.Vibration,
            checked = isHaptic,
            onCheckedChange = onHapticChange
        )

        SettingToggleRow(
            title = "أصوات النقر (Audio Clicks)",
            subtitle = "إصدار صوت خفيف عند كل ضغطة",
            icon = Icons.Default.VolumeUp,
            checked = isAudio,
            onCheckedChange = onAudioChange
        )

        SettingToggleRow(
            title = "توهج النيون السيبراني (Neon Glow)",
            subtitle = "تأثير إضاءة نيون حول حدود المفاتيح النشطة",
            icon = Icons.Default.ColorLens,
            checked = isGlow,
            onCheckedChange = onGlowChange
        )

        SettingToggleRow(
            title = "شريط الحركات والتشكيل السريع (Harakat Row)",
            subtitle = "عرض شريط الحركات العربية التلقائي فوق المفاتيح",
            icon = Icons.Default.Keyboard,
            checked = isHarakat,
            onCheckedChange = onHarakatChange
        )

        // Key Height adjustment slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("ارتفاع المفاتيح: ${keyHeight.toInt()} dp", color = colors.keyText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("تعديل ارتفاع المفاتيح ليناسب راحة يدك", color = colors.keyTextSecondary, fontSize = 11.sp)
                Slider(
                    value = keyHeight,
                    onValueChange = onKeyHeightChange,
                    valueRange = 44f..64f,
                    colors = SliderDefaults.colors(thumbColor = colors.accentPrimary, activeTrackColor = colors.accentPrimary)
                )
            }
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = NovaTheme.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = colors.accentPrimary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, color = colors.keyText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(subtitle, color = colors.keyTextSecondary, fontSize = 11.sp)
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colors.accentPrimary,
                    checkedTrackColor = colors.accentPrimary.copy(alpha = 0.3f),
                    uncheckedThumbColor = colors.keyTextSecondary,
                    uncheckedTrackColor = colors.keyBackground
                )
            )
        }
    }
}
