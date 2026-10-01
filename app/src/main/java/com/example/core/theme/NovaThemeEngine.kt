package com.example.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class NovaThemePalette(val titleAr: String, val titleEn: String) {
    CYBERPUNK_NEON("سايبر نيون", "Cyberpunk Neon"),
    EMERALD_MATRIX("إيميرالد ماتريكس", "Emerald Matrix"),
    MIDNIGHT_AMOLED("أسود سوبر أموليد", "Midnight AMOLED"),
    CRIMSON_CYBER("قرمزي إلكتروني", "Crimson Cyber")
}

data class NovaKeyboardColors(
    val background: Color,
    val surface: Color,
    val keyBackground: Color,
    val keyAccentBackground: Color,
    val keySpecialBackground: Color,
    val keyPressedBackground: Color,
    val keyBorder: Color,
    val keyBorderActive: Color,
    val keyText: Color,
    val keyTextSecondary: Color,
    val keyAccentText: Color,
    val accentPrimary: Color,
    val accentSecondary: Color,
    val neonGlow: Color,
    val toolbarBackground: Color,
    val predictionBarBackground: Color,
    val spacebarGradient: Brush
)

val CyberpunkNeonColors = NovaKeyboardColors(
    background = Color(0xFF06090F),
    surface = Color(0xFF0D121D),
    keyBackground = Color(0xFF141A26),
    keyAccentBackground = Color(0xFF00E5FF),
    keySpecialBackground = Color(0xFF1C2436),
    keyPressedBackground = Color(0x3300E5FF),
    keyBorder = Color(0xFF263248),
    keyBorderActive = Color(0xFF00E5FF),
    keyText = Color(0xFFF1F5F9),
    keyTextSecondary = Color(0xFF94A3B8),
    keyAccentText = Color(0xFF06090F),
    accentPrimary = Color(0xFF00E5FF),
    accentSecondary = Color(0xFFFF0055),
    neonGlow = Color(0xFF00E5FF),
    toolbarBackground = Color(0xFF090D15),
    predictionBarBackground = Color(0xFF0F1624),
    spacebarGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF00E5FF), Color(0xFFB55FE6), Color(0xFFFF0055))
    )
)

val EmeraldMatrixColors = NovaKeyboardColors(
    background = Color(0xFF030E08),
    surface = Color(0xFF061A10),
    keyBackground = Color(0xFF0D281B),
    keyAccentBackground = Color(0xFF10B981),
    keySpecialBackground = Color(0xFF133625),
    keyPressedBackground = Color(0x3310B981),
    keyBorder = Color(0xFF1F4D36),
    keyBorderActive = Color(0xFF34D399),
    keyText = Color(0xFFECFDF5),
    keyTextSecondary = Color(0xFF6EE7B7),
    keyAccentText = Color(0xFF030E08),
    accentPrimary = Color(0xFF10B981),
    accentSecondary = Color(0xFF34D399),
    neonGlow = Color(0xFF10B981),
    toolbarBackground = Color(0xFF04120B),
    predictionBarBackground = Color(0xFF0A2217),
    spacebarGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF059669), Color(0xFF10B981), Color(0xFF34D399))
    )
)

val MidnightAmoledColors = NovaKeyboardColors(
    background = Color(0xFF000000),
    surface = Color(0xFF0A0A0A),
    keyBackground = Color(0xFF171717),
    keyAccentBackground = Color(0xFF3B82F6),
    keySpecialBackground = Color(0xFF222222),
    keyPressedBackground = Color(0x333B82F6),
    keyBorder = Color(0xFF2E2E2E),
    keyBorderActive = Color(0xFF60A5FA),
    keyText = Color(0xFFFFFFFF),
    keyTextSecondary = Color(0xFFA3A3A3),
    keyAccentText = Color(0xFF000000),
    accentPrimary = Color(0xFF3B82F6),
    accentSecondary = Color(0xFF8B5CF6),
    neonGlow = Color(0xFF3B82F6),
    toolbarBackground = Color(0xFF000000),
    predictionBarBackground = Color(0xFF0E0E0E),
    spacebarGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6), Color(0xFF60A5FA))
    )
)

val CrimsonCyberColors = NovaKeyboardColors(
    background = Color(0xFF0E0407),
    surface = Color(0xFF18070D),
    keyBackground = Color(0xFF290C16),
    keyAccentBackground = Color(0xFFFF1744),
    keySpecialBackground = Color(0xFF38101E),
    keyPressedBackground = Color(0x33FF1744),
    keyBorder = Color(0xFF50162B),
    keyBorderActive = Color(0xFFFF5252),
    keyText = Color(0xFFFFF1F2),
    keyTextSecondary = Color(0xFFFDA4AF),
    keyAccentText = Color(0xFF0E0407),
    accentPrimary = Color(0xFFFF1744),
    accentSecondary = Color(0xFFFFD700),
    neonGlow = Color(0xFFFF1744),
    toolbarBackground = Color(0xFF100509),
    predictionBarBackground = Color(0xFF1E0911),
    spacebarGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF1744), Color(0xFFFF5252), Color(0xFFFFD700))
    )
)

object NovaTheme {
    val colors: NovaKeyboardColors
        @Composable
        @ReadOnlyComposable
        get() = LocalNovaColors.current
}

val LocalNovaColors = staticCompositionLocalOf { CyberpunkNeonColors }

fun getColorsForPalette(palette: NovaThemePalette): NovaKeyboardColors {
    return when (palette) {
        NovaThemePalette.CYBERPUNK_NEON -> CyberpunkNeonColors
        NovaThemePalette.EMERALD_MATRIX -> EmeraldMatrixColors
        NovaThemePalette.MIDNIGHT_AMOLED -> MidnightAmoledColors
        NovaThemePalette.CRIMSON_CYBER -> CrimsonCyberColors
    }
}
