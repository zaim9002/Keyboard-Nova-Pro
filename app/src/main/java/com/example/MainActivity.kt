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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
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
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
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
            MohamedKeyboardMainScreen()
        }
    }
}

// Brand Colors
private val BrandGold = Color(0xFFFFD700)
private val BrandPrimary = Color(0xFF00E5FF)
private val BrandSecondary = Color(0xFF1E88E5)
private val DarkBg = Color(0xFF0D111A)
private val DarkSurface = Color(0xFF151C28)
private val DarkCard = Color(0xFF1E2838)
private val TextWhite = Color(0xFFF0F4F8)
private val TextMuted = Color(0xFF94A3B8)
private val SuccessGreen = Color(0xFF10B981)
private val AlertAmber = Color(0xFFF59E0B)

data class NavTabItem(
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MohamedKeyboardMainScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as? KeyboardProApp
    val prefs = remember { app?.preferences ?: KeyboardPreferences(context) }

    var selectedTab by remember { mutableIntStateOf(0) }
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    BackHandler(enabled = true) {
        if (selectedTab != 0) {
            selectedTab = 0
        } else {
            val now = System.currentTimeMillis()
            if (now - lastBackPressTime < 2000L) {
                (context as? ComponentActivity)?.finish()
            } else {
                lastBackPressTime = now
                android.widget.Toast.makeText(context, "اضغط مرة أخرى للخروج من التطبيق", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    val currentThemeName by prefs.themeState.collectAsState()
    val colorScheme = KeyboardThemes.getTheme(currentThemeName, prefs)

    // State for live sandbox text
    var sandboxText by remember {
        mutableStateOf("مرحباً بك في كيبورد محمد! لوحة المفاتيح الذكية الشاملة مع الترجمة الفورية والتشكيل العربي ⚡")
    }
    var showEmbeddedKeyboard by remember { mutableStateOf(true) }

    // Live detection of IME enablement and default status
    var isEnabledInSystem by remember { mutableStateOf(false) }
    var isDefaultInSystem by remember { mutableStateOf(false) }

    val checkImeStatus = {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            val enabledMethods = imm?.enabledInputMethodList ?: emptyList()
            isEnabledInSystem = enabledMethods.any { it.packageName == context.packageName }

            val currentDefault = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.DEFAULT_INPUT_METHOD
            ) ?: ""
            isDefaultInSystem = currentDefault.contains(context.packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(selectedTab) {
        checkImeStatus()
    }

    val tabs = listOf(
        NavTabItem("تجربة الكيبورد", Icons.Default.Keyboard),
        NavTabItem("المظاهر والثيمات", Icons.Default.Palette),
        NavTabItem("الحافظة والملاحظات", Icons.Default.ContentCopy),
        NavTabItem("الذكاء والترجمة", Icons.Default.Translate),
        NavTabItem("الإعدادات والتخصيص", Icons.Default.Settings)
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(BrandSecondary, BrandPrimary)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "م",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "كيبورد محمد",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = if (isDefaultInSystem) "لوحة المفاتيح الافتراضية النشطة ✓" else "لوحة المفاتيح الذكية الشاملة",
                                    color = if (isDefaultInSystem) SuccessGreen else TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { selectedTab = 1 }) {
                            Icon(
                                Icons.Default.Palette,
                                contentDescription = "المظاهر",
                                tint = BrandPrimary
                            )
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
                // Horizontal Scrollable Tab Bar
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurface,
                    contentColor = BrandPrimary,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = BrandPrimary,
                                height = 3.dp
                            )
                        }
                    },
                    divider = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFF1E2838))
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, tabItem ->
                        val isSelected = selectedTab == index
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = tabItem.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isSelected) BrandPrimary else TextMuted
                                    )
                                    Text(
                                        text = tabItem.title,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) BrandPrimary else TextMuted
                                    )
                                }
                            }
                        )
                    }
                }

                // Tab Content Area
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> MohamedSetupAndSandboxTab(
                            sandboxText = sandboxText,
                            onTextChange = { sandboxText = it },
                            showKeyboard = showEmbeddedKeyboard,
                            onToggleKeyboard = { showEmbeddedKeyboard = !showEmbeddedKeyboard },
                            isEnabledInSystem = isEnabledInSystem,
                            isDefaultInSystem = isDefaultInSystem,
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
                        1 -> MohamedThemesCatalogTab(
                            prefs = prefs,
                            onThemeChanged = { themeName ->
                                prefs.theme = themeName
                            }
                        )
                        2 -> MohamedSmartClipboardTab(
                            onInsertToSandbox = { text ->
                                sandboxText += text
                                selectedTab = 0
                            }
                        )
                        3 -> MohamedAiAndTranslateTab(
                            sandboxText = sandboxText,
                            onApplyText = { text ->
                                sandboxText = text
                                selectedTab = 0
                            }
                        )
                        4 -> MohamedComprehensiveSettingsTab(prefs = prefs)
                    }
                }
            }
        }
    }
}

@Composable
fun MohamedSetupAndSandboxTab(
    sandboxText: String,
    onTextChange: (String) -> Unit,
    showKeyboard: Boolean,
    onToggleKeyboard: () -> Unit,
    isEnabledInSystem: Boolean,
    isDefaultInSystem: Boolean,
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
        // Upper Controls and Text Field
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Activation / Setup Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🚀 إعداد كيبورد محمد في النظام",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEnabledInSystem) "✓ مفعل في قائمة اللوحات" else "⚠️ غير مفعل بعد",
                            color = if (isEnabledInSystem) SuccessGreen else AlertAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = if (isDefaultInSystem) "✓ اللوحة الافتراضية حالياً" else "⚠️ غير معين كافتراضي",
                            color = if (isDefaultInSystem) SuccessGreen else AlertAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onEnableIme,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandPrimary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary)
                        ) {
                            Text("1. تفعيل الكيبورد", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onSelectIme,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Text("2. تعيين كافتراضي", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Interactive Live Typing Sandbox
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ حقل التجربة والكتابة المباشرة",
                            color = BrandPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        Text(
                            text = "مسح الكل",
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { onTextChange("") }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sandbox Display Box (Custom Interactive Field to prevent dual system keyboard popping)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCard)
                            .border(1.dp, Color(0xFF2E3A4E), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (sandboxText.isEmpty()) {
                            Text(
                                text = "اضغط على الأزرار في لوحة المفاتيح بالأسفل للتجربة...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        } else {
                            Text(
                                text = sandboxText,
                                color = TextWhite,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Quick Helper Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        QuickSampleChip("✨ تشكيل عربي") {
                            onTextChange("كِيبُورْدُ مُحَمَّد: تَرْجَمَةٌ فَوْرِيَّةٌ وَسُرْعَةٌ فَائِقَةٌ.")
                        }
                        QuickSampleChip("🌐 English Test") {
                            onTextChange("Hello! Mohamed Keyboard ultra-fast zero-latency engine.")
                        }
                        QuickSampleChip("🔤 جميع الحروف") {
                            onTextChange("ض ص ث ق ف غ ع ه خ ح ج ش س ي ب ل ا ت ن م ك ط ظ ط ذ د ز ر و ة ى ث")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pro Tip Note
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BrandGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "💡 ميزة الضغط المطول: اضغط مطولاً على زر (↵ Enter) للترجمة الفورية للنص المكتوب تلقائياً!",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Toggle Interactive Keyboard Preview
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurface)
                    .clickable { onToggleKeyboard() }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showKeyboard) "إخفاء اللوحة التفاعلية" else "عرض اللوحة التفاعلية",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (showKeyboard) "▲" else "▼",
                    color = BrandPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Live Embedded Interactive Mohamed Keyboard
        AnimatedVisibility(visible = showKeyboard) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp)
                    .background(colorScheme.background)
                    .border(1.dp, BrandPrimary.copy(alpha = 0.3f))
            ) {
                KeyboardScreen(
                    colorScheme = colorScheme,
                    currentLanguage = currentLang,
                    imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_DONE,
                    isIncognito = false,
                    keyboardHeight = prefs.keyboardHeight,
                    showNumberRow = prefs.showNumberRow,
                    hapticEnabled = prefs.hapticFeedback != "Off",
                    soundEnabled = prefs.soundFeedback != "Off",
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
fun MohamedThemesCatalogTab(
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "🎨 باقة المظاهر والثيمات الحصرية",
                color = BrandPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "اختر مظهرك المفضل، يتم تطبيق الثيم فورياً وحفظه تلقائياً.",
                color = TextMuted,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(6.dp))
        }

        items(allThemes) { (themeName, paletteColors) ->
            val isSelected = currentTheme == themeName
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onThemeChanged(themeName) }
                    .border(
                        width = if (isSelected) 2.dp else 0.5.dp,
                        color = if (isSelected) BrandPrimary else Color(0xFF2E3A4E),
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
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            paletteColors.forEach { c ->
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(c)
                                        .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                )
                            }
                        }
                        Spacer(Modifier.width(14.dp))
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
                            contentDescription = "تم الاختيار",
                            tint = BrandPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MohamedSmartClipboardTab(onInsertToSandbox: (String) -> Unit) {
    val sampleClips = listOf(
        "السلام عليكم ورحمة الله وبركاته",
        "شكراً جزيلاً لك على تواصلك الكريم وبارك الله فيك!",
        "zaim9002@gmail.com",
        "تمت كتابة هذا النص بواسطة كيبورد محمد الذكي ⚡",
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
            color = BrandPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "إمكانية إدراج النصوص المنسوخة بلمسة واحدة مباشرة في أي تطبيق.",
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
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = clip,
                        color = TextWhite,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(10.dp))
                    Button(
                        onClick = { onInsertToSandbox(clip) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("إدراج", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MohamedAiAndTranslateTab(
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
            text = "🌐 محرك الترجمة الفورية والذكاء الاصطناعي",
            color = BrandPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "ترجمة سريعة ودقيقة بين اللغة العربية وأكثر من 40 لغة عالمية.",
            color = TextMuted,
            fontSize = 12.sp
        )

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("النص المطلوب ترجمته أو تشكيله", color = TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = Color(0xFF2E3A4E),
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            ),
            shape = RoundedCornerShape(10.dp)
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
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isLoading) "جاري المعالجة..." else "ترجمة فورية (عربي ⇄ English)",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        if (outputText.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("النتيجة المترجمة:", color = BrandPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(outputText, color = TextWhite, fontSize = 14.sp)
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { onApplyText(outputText) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("تطبيق النص في حقل التجربة", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MohamedComprehensiveSettingsTab(prefs: KeyboardPreferences) {
    var heightPercent by remember { mutableIntStateOf(prefs.keyboardHeightPercent) }
    var showNumbers by remember { mutableStateOf(prefs.showNumberRow) }
    var haptic by remember { mutableStateOf(prefs.hapticFeedback != "Off") }
    var hapticLevel by remember { mutableStateOf(prefs.hapticFeedback) }
    var sound by remember { mutableStateOf(prefs.soundFeedback != "Off") }
    var soundProfile by remember { mutableStateOf(prefs.soundFeedback) }
    var autoTranslate by remember { mutableStateOf(prefs.autoTranslateOnEnter) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "⚙️ الإعدادات والتخصيص الشامل",
            color = BrandPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        // Setting 1: Dedicated Number Row
        SettingToggleCard(
            title = "صف الأرقام المستقل",
            subtitle = "إظهار صف مستقل للأرقام فوق الحروف",
            checked = showNumbers,
            onCheckedChange = {
                showNumbers = it
                prefs.showNumberRow = it
            }
        )

        // Setting 2: Auto-translate on Enter
        SettingToggleCard(
            title = "ترجمة تلقائية عند الضغط على Enter",
            subtitle = "ترجمة النص مباشرة إلى الإنجليزية عند النقر",
            checked = autoTranslate,
            onCheckedChange = {
                autoTranslate = it
                prefs.autoTranslateOnEnter = it
            }
        )

        // Setting 3: Haptic Feedback
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("الاهتزاز عند اللمس (Haptic)", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("تفعيل الاهتزاز الخفيف عند الضغط على المفاتيح", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = haptic,
                        onCheckedChange = {
                            haptic = it
                            val newLevel = if (it) "Medium" else "Off"
                            hapticLevel = newLevel
                            prefs.hapticFeedback = newLevel
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BrandPrimary,
                            checkedTrackColor = BrandSecondary.copy(alpha = 0.5f),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DarkCard
                        )
                    )
                }

                if (haptic) {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Light" to "خفيف", "Medium" to "متوسط", "Heavy" to "قوي").forEach { (lvl, label) ->
                            val isSelected = hapticLevel == lvl
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) BrandPrimary else DarkCard)
                                    .clickable {
                                        hapticLevel = lvl
                                        prefs.hapticFeedback = lvl
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else TextWhite
                                )
                            }
                        }
                    }
                }
            }
        }

        // Setting 4: Keypress Sounds
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("أصوات النقر", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("تفعيل الصوت التفاعلي عند النقر", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = sound,
                        onCheckedChange = {
                            sound = it
                            val newProfile = if (it) "CLICK" else "Off"
                            soundProfile = newProfile
                            prefs.soundFeedback = newProfile
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BrandPrimary,
                            checkedTrackColor = BrandSecondary.copy(alpha = 0.5f),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DarkCard
                        )
                    )
                }

                if (sound) {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "CLICK" to "كلاسيكي",
                            "TYPEWRITER" to "آلة كاتبة",
                            "WATER" to "مائي",
                            "CHERRY_MX" to "ميكانيكي"
                        ).forEach { (prof, label) ->
                            val isSelected = soundProfile == prof
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) BrandPrimary else DarkCard)
                                    .clickable {
                                        soundProfile = prof
                                        prefs.soundFeedback = prof
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else TextWhite
                                )
                            }
                        }
                    }
                }
            }
        }

        // Setting 5: Keyboard Height Slider
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
                    Text("$heightPercent%", color = BrandPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = heightPercent.toFloat(),
                    onValueChange = {
                        heightPercent = it.toInt()
                        prefs.keyboardHeightPercent = it.toInt()
                    },
                    valueRange = 70f..140f,
                    colors = SliderDefaults.colors(
                        thumbColor = BrandPrimary,
                        activeTrackColor = BrandPrimary,
                        inactiveTrackColor = DarkCard
                    )
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
            .border(0.5.dp, Color(0xFF2E3A4E), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text = label, color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
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
                    checkedThumbColor = BrandPrimary,
                    checkedTrackColor = BrandSecondary.copy(alpha = 0.5f),
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = DarkCard
                )
            )
        }
    }
}
