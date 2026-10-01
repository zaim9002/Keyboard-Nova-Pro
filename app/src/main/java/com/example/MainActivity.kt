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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.pref.KeyboardPreferences
import com.example.engine.TranslationEngine
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.theme.KeyboardThemes
import com.example.ime.ui.KeyboardScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MohamedKeyboardApp()
        }
    }
}

// Brand Colors
private val BrandGold = Color(0xFFFFD700)
private val BrandPrimary = Color(0xFF1E88E5)
private val BrandAccent = Color(0xFF00E5FF)
private val DarkBg = Color(0xFF121214)
private val DarkSurface = Color(0xFF1C1C20)
private val DarkCard = Color(0xFF24242A)
private val TextWhite = Color(0xFFF0F0F5)
private val TextMuted = Color(0xFF9E9EA8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MohamedKeyboardApp() {
    val context = LocalContext.current
    val app = context.applicationContext as? KeyboardProApp
    val prefs = remember { app?.preferences ?: KeyboardPreferences(context) }
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    BackHandler(enabled = selectedTab != 0) {
        selectedTab = 0
    }

    val currentThemeName by prefs.themeState.collectAsState()
    val colorScheme = KeyboardThemes.getTheme(currentThemeName, prefs)

    // Live Sandbox Text State
    var sandboxText by remember { mutableStateOf("مرحباً بك في كيبورد محمد! لوحة المفاتيح الشاملة بكل الميزات والترجمة الفورية ⚡") }
    var showEmbeddedKeyboard by remember { mutableStateOf(true) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(BrandPrimary, BrandAccent)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("م", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "كيبورد محمد",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "لوحة المفاتيح الذكية الشاملة",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                selectedTab = 1 // Switch to Themes
                            }
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = "المظاهر", tint = BrandAccent)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBg,
                        titleContentColor = TextWhite
                    )
                )
            },
            containerColor = DarkBg
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Navigation Tabs
                val tabTitles = listOf("تجربة الكيبورد", "المظاهر والثيمات", "الحافظة", "الذكاء والترجمة", "الإعدادات")
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurface,
                    contentColor = BrandAccent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = BrandAccent
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
                                    color = if (selectedTab == index) BrandAccent else TextMuted
                                )
                            }
                        )
                    }
                }

                // Tab Content
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> MohamedSandboxTab(
                            sandboxText = sandboxText,
                            onTextChange = { sandboxText = it },
                            showKeyboard = showEmbeddedKeyboard,
                            onToggleKeyboard = { showEmbeddedKeyboard = !showEmbeddedKeyboard },
                            prefs = prefs,
                            colorScheme = colorScheme,
                            onEnableIme = {
                                try {
                                    context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    })
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            onSelectIme = {
                                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.showInputMethodPicker()
                            }
                        )
                        1 -> MohamedThemesTab(
                            prefs = prefs,
                            onThemeChanged = { themeName ->
                                prefs.theme = themeName
                            }
                        )
                        2 -> MohamedClipboardTab(
                            onInsertToSandbox = { text ->
                                sandboxText += text
                                selectedTab = 0
                            }
                        )
                        3 -> MohamedAiTranslateTab(
                            sandboxText = sandboxText,
                            onApplyText = { text ->
                                sandboxText = text
                                selectedTab = 0
                            }
                        )
                        4 -> MohamedSettingsTab(prefs = prefs)
                    }
                }
            }
        }
    }
}

@Composable
fun MohamedSandboxTab(
    sandboxText: String,
    onTextChange: (String) -> Unit,
    showKeyboard: Boolean,
    onToggleKeyboard: () -> Unit,
    prefs: KeyboardPreferences,
    colorScheme: KeyboardColorScheme,
    onEnableIme: () -> Unit,
    onSelectIme: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val currentLang by prefs.languageState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Top Sandbox Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // IME Activation Actions
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandAccent),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandAccent)
                ) {
                    Text("1. تفعيل الكيبورد", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSelectIme,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAccent)
                ) {
                    Text("2. تعيين كافتراضي", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Real Live Sandbox Field
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ حقل التجربة المباشرة (كيبورد محمد)",
                            color = BrandAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        Text(
                            text = "مسح",
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { onTextChange("") }
                                .padding(horizontal = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = sandboxText,
                        onValueChange = onTextChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        placeholder = { Text("اكتب هنا للتجربة أو استخدم لوحة المفاتيح بالأسفل...", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandAccent,
                            unfocusedBorderColor = Color(0xFF33333E),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkCard,
                            unfocusedContainerColor = DarkCard
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Quick Sample Texts
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        QuickSampleChip("✨ تجربة التشكيل العربي") {
                            onTextChange("كيبورد محمد: سُرْعَةٌ فَائِقَةٌ وَتَرْجَمَةٌ فَوْرِيَّةٌ مَعَ كَافَّةِ الْحُرُوفِ.")
                        }
                        QuickSampleChip("🌐 تجربة الترجمة الفورية") {
                            onTextChange("Peace and blessings upon you! Welcome to Mohamed Keyboard.")
                        }
                        QuickSampleChip("🔤 جميع الحروف العربية") {
                            onTextChange("ض ص ث ق ف غ ع ه خ ح ج ش س ي ب ل ا ت ن م ك ط ظ ط ذ د ز ر و ة ى ث")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Information Box on Enter Long Press Feature
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Translate,
                        contentDescription = "ترجمة",
                        tint = BrandAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "💡 ميزة الضغط المطول: اضغط مطولاً على زر الإدخال (↵ Enter) للترجمة الفورية للنص المكتوب تلقائياً!",
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Toggle Interactive Keyboard Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurface)
                    .clickable { onToggleKeyboard() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showKeyboard) "إخفاء لوحة المفاتيح التفاعلية" else "عرض لوحة المفاتيح التفاعلية",
                    color = TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (showKeyboard) "▲" else "▼",
                    color = BrandAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Live Embedded Mohamed Keyboard
        AnimatedVisibility(visible = showKeyboard) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp)
                    .background(colorScheme.background)
                    .border(1.dp, BrandAccent.copy(alpha = 0.3f))
            ) {
                KeyboardScreen(
                    colorScheme = colorScheme,
                    currentLanguage = currentLang,
                    imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_DONE,
                    isIncognito = false,
                    keyboardHeight = prefs.keyboardHeight,
                    showNumberRow = prefs.showNumberRow,
                    hapticEnabled = prefs.hapticFeedback != "Off",
                    soundEnabled = false,
                    oneHandedMode = prefs.oneHandedMode,
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
                    onLongPressEnter = {
                        // Instant translation on long-press Enter
                        val clean = sandboxText.trim()
                        if (clean.isNotEmpty()) {
                            val isArabic = clean.any { it in '\u0600'..'\u06FF' }
                            val src = if (isArabic) "ar" else "en"
                            val tgt = if (isArabic) "en" else "ar"
                            scope.launch(Dispatchers.IO) {
                                val translated = TranslationEngine.translateAsync(clean, src, tgt)
                                withContext(Dispatchers.Main) {
                                    onTextChange(translated)
                                }
                            }
                        }
                    },
                    onSpace = { onTextChange(sandboxText + " ") },
                    onSwitchLanguage = {
                        prefs.currentLanguage = if (prefs.currentLanguage == "ar") "en" else "ar"
                    },
                    onSelectLanguage = { prefs.currentLanguage = it },
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
                    onToggleOneHanded = { prefs.oneHandedMode = it },
                    onApplyAiText = { onTextChange(it) },
                    onTranslateNow = { src, tgt ->
                        scope.launch(Dispatchers.IO) {
                            val res = TranslationEngine.translateAsync(sandboxText, src, tgt)
                            withContext(Dispatchers.Main) {
                                onTextChange(res)
                            }
                        }
                    },
                    onHideKeyboard = onToggleKeyboard
                )
            }
        }
    }
}

@Composable
fun QuickSampleChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(DarkCard)
            .border(0.5.dp, Color(0xFF33333E), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, color = TextMuted, fontSize = 11.sp)
    }
}

@Composable
fun MohamedThemesTab(
    prefs: KeyboardPreferences,
    onThemeChanged: (String) -> Unit
) {
    val currentTheme by prefs.themeState.collectAsState()
    val allThemes = listOf(
        "غزل بنات" to listOf(Color(0xFFFFB6C1), Color(0xFFFF69B4), Color(0xFF87CEEB)),
        "وردي" to listOf(Color(0xFFFFC0CB), Color(0xFFFF1493), Color(0xFFFFF0F5)),
        "مرجاني" to listOf(Color(0xFFFF7F50), Color(0xFFFF4500), Color(0xFFFFD700)),
        "لافندر" to listOf(Color(0xFFE6E6FA), Color(0xFF9370DB), Color(0xFF4B0082)),
        "AMOLED Pitch Black" to listOf(Color(0xFF000000), Color(0xFF1E88E5), Color(0xFFFFFFFF)),
        "الذهب الملكي" to listOf(Color(0xFF121214), Color(0xFFFFD700), Color(0xFFFFE082)),
        "الزمرد الأخضر" to listOf(Color(0xFF0D2818), Color(0xFF2EC4B6), Color(0xFFE71D36)),
        "رمال الصحراء" to listOf(Color(0xFFD4A373), Color(0xFFCCD5AE), Color(0xFFFAEDCD)),
        "المحيط العميق" to listOf(Color(0xFF03045E), Color(0xFF0077B6), Color(0xFF90E0EF)),
        "غروب البنفسج" to listOf(Color(0xFF240046), Color(0xFF7B2CBF), Color(0xFFFF9E00)),
        "النسيم المنعش" to listOf(Color(0xFF1B4332), Color(0xFF52B788), Color(0xFFD8F3DC)),
        "الأصفر النيون" to listOf(Color(0xFF0F0F0F), Color(0xFFFFE600), Color(0xFFFFFFFF)),
        "الأحمر القرمزي" to listOf(Color(0xFF1A0A0A), Color(0xFFFF1744), Color(0xFFFFFFFF)),
        "ألياف الكربون" to listOf(Color(0xFF151518), Color(0xFF42424E), Color(0xFF00E5FF)),
        "الكوبالت الداكن" to listOf(Color(0xFF0A1128), Color(0xFF001F54), Color(0xFF034078))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🎨 اختر مظهر كيبورد محمد",
            color = BrandAccent,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "تتوفر ثيمات مصممة بألوان جذابة ومطابقة للمظهر المطلوب.",
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(4.dp))

        allThemes.forEach { (themeName, colors) ->
            val isSelected = currentTheme == themeName
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onThemeChanged(themeName)
                    }
                    .border(
                        width = if (isSelected) 2.dp else 0.5.dp,
                        color = if (isSelected) BrandAccent else Color(0xFF33333E),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) DarkCard else DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Color preview dots
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            colors.forEach { c ->
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(c)
                                        .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = themeName,
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }

                    if (isSelected) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "محدد",
                            tint = BrandAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MohamedClipboardTab(onInsertToSandbox: (String) -> Unit) {
    val sampleClips = listOf(
        "السلام عليكم ورحمة الله وبركاته",
        "شكراً جزيلاً لك على تواصلك الكريم!",
        "zaim9002@gmail.com",
        "تمت الترجمة بنجاح عبر كيبورد محمد.",
        "https://aistudio.google.com"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "📋 الحافظة الذكية والملاحظات السريعة",
            color = BrandAccent,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "يتم حفظ النصوص المنسوخة تلقائياً، مع إمكانية التثبيت والإدراج بنقرة واحدة.",
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(6.dp))

        sampleClips.forEach { clip ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = clip,
                        color = TextWhite,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onInsertToSandbox(clip) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("إدراج", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun MohamedAiTranslateTab(
    sandboxText: String,
    onApplyText: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf(sandboxText) }
    var outputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🌐 محرك الترجمة والذكاء الاصطناعي",
            color = BrandAccent,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "ترجمة فورية لأكثر من 40 لغة، وضغط مطول على زر Enter للترجمة التلقائية السريعة.",
            color = TextMuted,
            fontSize = 12.sp
        )

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("النص المراد ترجمته", color = TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandAccent,
                unfocusedBorderColor = Color(0xFF33333E),
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        isLoading = true
                        val isArabic = inputText.any { it in '\u0600'..'\u06FF' }
                        val src = if (isArabic) "ar" else "en"
                        val tgt = if (isArabic) "en" else "ar"
                        scope.launch(Dispatchers.IO) {
                            val res = TranslationEngine.translateAsync(inputText, src, tgt)
                            withContext(Dispatchers.Main) {
                                outputText = res
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = BrandAccent)
            ) {
                Text(if (isLoading) "جاري الترجمة..." else "ترجمة الآن (عربي ⇄ English)", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        if (outputText.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("النتيجة المترجمة:", color = BrandAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(outputText, color = TextWhite, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { onApplyText(outputText) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("تطبيق النص في حقل التجربة", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MohamedSettingsTab(prefs: KeyboardPreferences) {
    var heightPercent by remember { mutableIntStateOf(prefs.keyboardHeightPercent) }
    var showNumbers by remember { mutableStateOf(prefs.showNumberRow) }
    var haptic by remember { mutableStateOf(prefs.hapticFeedback != "Off") }
    var sound by remember { mutableStateOf(prefs.soundFeedback != "Off") }
    var autoTranslate by remember { mutableStateOf(prefs.autoTranslateOnEnter) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "⚙️ إعدادات كيبورد محمد",
            color = BrandAccent,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        // Setting Item: Number Row
        SettingToggleCard(
            title = "صف الأرقام المستقل",
            subtitle = "إظهار صف مستقل للأرقام فوق الحروف",
            checked = showNumbers,
            onCheckedChange = {
                showNumbers = it
                prefs.showNumberRow = it
            }
        )

        // Setting Item: Auto-translate on Enter
        SettingToggleCard(
            title = "ترجمة تلقائية عند الضغط على Enter",
            subtitle = "ترجمة النص مباشرة إلى الإنجليزية عند الضغط",
            checked = autoTranslate,
            onCheckedChange = {
                autoTranslate = it
                prefs.autoTranslateOnEnter = it
            }
        )

        // Setting Item: Haptic Feedback
        SettingToggleCard(
            title = "الاهتزاز عند اللمس (Haptic)",
            subtitle = "تفعيل الاهتزاز الخفيف عند الضغط على المفاتيح",
            checked = haptic,
            onCheckedChange = {
                haptic = it
                prefs.hapticFeedback = if (it) "Medium" else "Off"
            }
        )

        // Setting Item: Sound Feedback
        SettingToggleCard(
            title = "أصوات النقر",
            subtitle = "تفعيل الصوت التفاعلي عند النقر",
            checked = sound,
            onCheckedChange = {
                sound = it
                prefs.soundFeedback = if (it) "CLICK" else "Off"
            }
        )

        // Setting Item: Keyboard Height Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ارتفاع لوحة المفاتيح", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("$heightPercent%", color = BrandAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = heightPercent.toFloat(),
                    onValueChange = {
                        heightPercent = it.toInt()
                        prefs.keyboardHeightPercent = it.toInt()
                    },
                    valueRange = 70f..140f,
                    colors = SliderDefaults.colors(
                        thumbColor = BrandAccent,
                        activeTrackColor = BrandAccent,
                        inactiveTrackColor = DarkCard
                    )
                )
            }
        }
    }
}

@Composable
fun SettingToggleCard(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, color = TextMuted, fontSize = 11.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BrandAccent,
                    checkedTrackColor = BrandPrimary.copy(alpha = 0.5f),
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = DarkCard
                )
            )
        }
    }
}
