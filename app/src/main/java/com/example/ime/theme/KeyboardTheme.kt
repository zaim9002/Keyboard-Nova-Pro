package com.example.ime.theme

import androidx.compose.ui.graphics.Color
import com.example.data.pref.KeyboardPreferences

data class KeyboardColorScheme(
    val name: String,
    val isDark: Boolean,
    val background: Color,
    val keyBackground: Color,
    val keyText: Color,
    val specialKeyBackground: Color,
    val specialKeyText: Color,
    val accent: Color,
    val suggestionBar: Color,
    val suggestionText: Color,
    val borderColor: Color = Color.Transparent,
    val backgroundImageUri: String? = null,
    val category: String = "عام"
)

object KeyboardThemes {
    // === 1. Classic & Colors ===
    val Asasi = KeyboardColorScheme(
        name = "أساسي",
        isDark = false,
        background = Color(0xFFD8DCE1),
        keyBackground = Color(0xFFFFFFFF),
        keyText = Color(0xFF1C1C1E),
        specialKeyBackground = Color(0xFFE5E8ED),
        specialKeyText = Color(0xFF3A3A3C),
        accent = Color(0xFF2563EB),
        suggestionBar = Color(0xFFD8DCE1),
        suggestionText = Color(0xFF222222),
        borderColor = Color(0x1F000000),
        category = "كلاسيكي"
    )

    val GhazalBanat = KeyboardColorScheme(
        name = "غزل بنات",
        isDark = false,
        background = Color(0xFFFDF2F8),
        keyBackground = Color(0xFFFCE7F3),
        keyText = Color(0xFF831843),
        specialKeyBackground = Color(0xFFFBCFE8),
        specialKeyText = Color(0xFF9D174D),
        accent = Color(0xFFF472B6),
        suggestionBar = Color(0xFFFDF2F8),
        suggestionText = Color(0xFF9D174D),
        borderColor = Color(0x33F472B6),
        category = "ألوان"
    )

    val Wardi = KeyboardColorScheme(
        name = "وردي",
        isDark = false,
        background = Color(0xFFFFF1F2),
        keyBackground = Color(0xFFFFE4E6),
        keyText = Color(0xFF9F1239),
        specialKeyBackground = Color(0xFFFECDD3),
        specialKeyText = Color(0xFFBE123C),
        accent = Color(0xFFFB7185),
        suggestionBar = Color(0xFFFFF1F2),
        suggestionText = Color(0xFF9F1239),
        borderColor = Color(0x33FB7185),
        category = "Pink"
    )

    val Morjani = KeyboardColorScheme(
        name = "مرجاني",
        isDark = false,
        background = Color(0xFFFFF7ED),
        keyBackground = Color(0xFFFFEDD5),
        keyText = Color(0xFF9A3412),
        specialKeyBackground = Color(0xFFFED7AA),
        specialKeyText = Color(0xFFC2410C),
        accent = Color(0xFFFB923C),
        suggestionBar = Color(0xFFFFF7ED),
        suggestionText = Color(0xFF9A3412),
        borderColor = Color(0x33FB923C),
        category = "ألوان"
    )

    val Lavender = KeyboardColorScheme(
        name = "لافندر",
        isDark = false,
        background = Color(0xFFFAF5FF),
        keyBackground = Color(0xFFF3E8FF),
        keyText = Color(0xFF581C87),
        specialKeyBackground = Color(0xFFE9D5FF),
        specialKeyText = Color(0xFF6B21A8),
        accent = Color(0xFFC084FC),
        suggestionBar = Color(0xFFFAF5FF),
        suggestionText = Color(0xFF581C87),
        borderColor = Color(0x33C084FC),
        category = "Purple"
    )

    // === 2. Dark, Black, AMOLED ===
    val DarkClassic = KeyboardColorScheme(
        name = "Dark Classic",
        isDark = true,
        background = Color(0xFF1E1E24),
        keyBackground = Color(0xFF2B2B36),
        keyText = Color(0xFFF3F4F6),
        specialKeyBackground = Color(0xFF373747),
        specialKeyText = Color(0xFFE5E7EB),
        accent = Color(0xFF60A5FA),
        suggestionBar = Color(0xFF1E1E24),
        suggestionText = Color(0xFFF3F4F6),
        borderColor = Color(0x22FFFFFF),
        category = "Dark"
    )

    val PureBlack = KeyboardColorScheme(
        name = "Black",
        isDark = true,
        background = Color(0xFF121212),
        keyBackground = Color(0xFF1F1F1F),
        keyText = Color(0xFFEEEEEE),
        specialKeyBackground = Color(0xFF2C2C2C),
        specialKeyText = Color(0xFFB0B0B0),
        accent = Color(0xFF38BDF8),
        suggestionBar = Color(0xFF121212),
        suggestionText = Color(0xFFEEEEEE),
        borderColor = Color(0x1AFFFFFF),
        category = "Black"
    )

    val AmoledPitch = KeyboardColorScheme(
        name = "AMOLED",
        isDark = true,
        background = Color(0xFF000000),
        keyBackground = Color(0xFF0D0D0D),
        keyText = Color(0xFFFFFFFF),
        specialKeyBackground = Color(0xFF1A1A1A),
        specialKeyText = Color(0xFF00E5FF),
        accent = Color(0xFF00E5FF),
        suggestionBar = Color(0xFF000000),
        suggestionText = Color(0xFFFFFFFF),
        borderColor = Color(0x3300E5FF),
        category = "AMOLED"
    )

    // === 3. Neon & Cyberpunk & Gaming ===
    val CyberpunkTheme = KeyboardColorScheme(
        name = "Cyberpunk",
        isDark = true,
        background = Color(0xFF0F051D),
        keyBackground = Color(0xFF1D0C38),
        keyText = Color(0xFF00FFE0),
        specialKeyBackground = Color(0xFF2E0854),
        specialKeyText = Color(0xFFFF007F),
        accent = Color(0xFFFF007F),
        suggestionBar = Color(0xFF0F051D),
        suggestionText = Color(0xFF00FFE0),
        borderColor = Color(0x8800FFE0),
        category = "Cyberpunk"
    )

    val NeonNight = KeyboardColorScheme(
        name = "Neon",
        isDark = true,
        background = Color(0xFF0A0E17),
        keyBackground = Color(0xFF121A2A),
        keyText = Color(0xFF38EF7D),
        specialKeyBackground = Color(0xFF1B283D),
        specialKeyText = Color(0xFF11998E),
        accent = Color(0xFF38EF7D),
        suggestionBar = Color(0xFF0A0E17),
        suggestionText = Color(0xFF38EF7D),
        borderColor = Color(0x6638EF7D),
        category = "Neon"
    )

    val GamingRgb = KeyboardColorScheme(
        name = "Gaming",
        isDark = true,
        background = Color(0xFF101216),
        keyBackground = Color(0xFF1C2028),
        keyText = Color(0xFFF43F5E),
        specialKeyBackground = Color(0xFF262C38),
        specialKeyText = Color(0xFF8B5CF6),
        accent = Color(0xFF8B5CF6),
        suggestionBar = Color(0xFF101216),
        suggestionText = Color(0xFFF43F5E),
        borderColor = Color(0x558B5CF6),
        category = "Gaming"
    )

    // === 4. Blue, Purple, Red, Green, Gold ===
    val RoyalBlue = KeyboardColorScheme(
        name = "Blue",
        isDark = true,
        background = Color(0xFF0B192C),
        keyBackground = Color(0xFF1E3E62),
        keyText = Color(0xFFF1F6F9),
        specialKeyBackground = Color(0xFF2C5584),
        specialKeyText = Color(0xFF60A5FA),
        accent = Color(0xFF38BDF8),
        suggestionBar = Color(0xFF0B192C),
        suggestionText = Color(0xFFF1F6F9),
        borderColor = Color(0x4438BDF8),
        category = "Blue"
    )

    val DeepPurple = KeyboardColorScheme(
        name = "Purple",
        isDark = true,
        background = Color(0xFF200E3A),
        keyBackground = Color(0xFF3B186D),
        keyText = Color(0xFFF5EEFD),
        specialKeyBackground = Color(0xFF532299),
        specialKeyText = Color(0xFFD8B4FE),
        accent = Color(0xFFA855F7),
        suggestionBar = Color(0xFF200E3A),
        suggestionText = Color(0xFFF5EEFD),
        borderColor = Color(0x44A855F7),
        category = "Purple"
    )

    val CrimsonRed = KeyboardColorScheme(
        name = "Red",
        isDark = true,
        background = Color(0xFF2B0A0D),
        keyBackground = Color(0xFF4A1017),
        keyText = Color(0xFFFEE2E2),
        specialKeyBackground = Color(0xFF691520),
        specialKeyText = Color(0xFFF87171),
        accent = Color(0xFFEF4444),
        suggestionBar = Color(0xFF2B0A0D),
        suggestionText = Color(0xFFFEE2E2),
        borderColor = Color(0x44EF4444),
        category = "Red"
    )

    val EmeraldGreen = KeyboardColorScheme(
        name = "Green",
        isDark = true,
        background = Color(0xFF062817),
        keyBackground = Color(0xFF0D472B),
        keyText = Color(0xFFD1FAE5),
        specialKeyBackground = Color(0xFF13633C),
        specialKeyText = Color(0xFF6EE7B7),
        accent = Color(0xFF10B981),
        suggestionBar = Color(0xFF062817),
        suggestionText = Color(0xFFD1FAE5),
        borderColor = Color(0x4410B981),
        category = "Green"
    )

    val LuxuryGold = KeyboardColorScheme(
        name = "Gold",
        isDark = true,
        background = Color(0xFF1C1608),
        keyBackground = Color(0xFF33290E),
        keyText = Color(0xFFFEF3C7),
        specialKeyBackground = Color(0xFF4D3E15),
        specialKeyText = Color(0xFFFCD34D),
        accent = Color(0xFFF59E0B),
        suggestionBar = Color(0xFF1C1608),
        suggestionText = Color(0xFFFEF3C7),
        borderColor = Color(0x66F59E0B),
        category = "Luxury"
    )

    val MinimalWhite = KeyboardColorScheme(
        name = "Minimal",
        isDark = false,
        background = Color(0xFFF8FAFC),
        keyBackground = Color(0xFFFFFFFF),
        keyText = Color(0xFF0F172A),
        specialKeyBackground = Color(0xFFF1F5F9),
        specialKeyText = Color(0xFF475569),
        accent = Color(0xFF0EA5E9),
        suggestionBar = Color(0xFFF8FAFC),
        suggestionText = Color(0xFF0F172A),
        borderColor = Color(0x22E2E8F0),
        category = "Minimal"
    )

    val FrostedGlass = KeyboardColorScheme(
        name = "Glass",
        isDark = true,
        background = Color(0xFF1E293B),
        keyBackground = Color(0xFF334155),
        keyText = Color(0xFFF8FAFC),
        specialKeyBackground = Color(0xFF475569),
        specialKeyText = Color(0xFF94A3B8),
        accent = Color(0xFF38BDF8),
        suggestionBar = Color(0xFF1E293B),
        suggestionText = Color(0xFFF8FAFC),
        borderColor = Color(0x44FFFFFF),
        category = "Glass"
    )

    val SunsetGradient = KeyboardColorScheme(
        name = "Gradient",
        isDark = true,
        background = Color(0xFF2A0845),
        keyBackground = Color(0xFF491369),
        keyText = Color(0xFFFFD1DC),
        specialKeyBackground = Color(0xFF641B92),
        specialKeyText = Color(0xFFFF9E80),
        accent = Color(0xFFFF6B6B),
        suggestionBar = Color(0xFF2A0845),
        suggestionText = Color(0xFFFFD1DC),
        borderColor = Color(0x44FF6B6B),
        category = "Gradient"
    )

    val MaterialYou = KeyboardColorScheme(
        name = "Material",
        isDark = false,
        background = Color(0xFFEDE7F6),
        keyBackground = Color(0xFFFFFFFF),
        keyText = Color(0xFF311B92),
        specialKeyBackground = Color(0xFFD1C4E9),
        specialKeyText = Color(0xFF512DA8),
        accent = Color(0xFF673AB7),
        suggestionBar = Color(0xFFEDE7F6),
        suggestionText = Color(0xFF311B92),
        borderColor = Color(0x33673AB7),
        category = "Material"
    )

    val allThemes = listOf(
        Asasi, DarkClassic, PureBlack, AmoledPitch,
        RoyalBlue, DeepPurple, CrimsonRed, EmeraldGreen, LuxuryGold,
        GhazalBanat, Wardi, Morjani, Lavender,
        CyberpunkTheme, NeonNight, GamingRgb,
        MinimalWhite, FrostedGlass, SunsetGradient, MaterialYou
    )

    fun getTheme(name: String, prefs: KeyboardPreferences? = null): KeyboardColorScheme {
        if (name.equals("Custom", ignoreCase = true) && prefs != null) {
            return prefs.getCustomColorScheme()
        }
        return allThemes.find { it.name.equals(name, ignoreCase = true) } ?: AmoledPitch
    }

    fun getColorScheme(name: String): KeyboardColorScheme {
        return getTheme(name)
    }
}
