package com.example.core.theme

import android.content.Context
import android.content.SharedPreferences

enum class KeyboardMode(val titleAr: String, val titleEn: String) {
    FULL("الوضع الكامل", "Full Screen"),
    ONE_HANDED("وضع اليد الواحدة", "One-Handed"),
    FLOATING("الوضع العائم", "Floating Window"),
    SPLIT("وضع التابلت المقسم", "Split Tablet")
}

enum class OneHandedSide {
    LEFT, RIGHT
}

class KeyboardPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("nova_keyboard_prefs", Context.MODE_PRIVATE)

    var currentTheme: NovaThemePalette
        get() = NovaThemePalette.valueOf(
            prefs.getString("theme", NovaThemePalette.CYBERPUNK_NEON.name) ?: NovaThemePalette.CYBERPUNK_NEON.name
        )
        set(value) = prefs.edit().putString("theme", value.name).apply()

    var keyboardMode: KeyboardMode
        get() = KeyboardMode.valueOf(
            prefs.getString("mode", KeyboardMode.FULL.name) ?: KeyboardMode.FULL.name
        )
        set(value) = prefs.edit().putString("mode", value.name).apply()

    var oneHandedSide: OneHandedSide
        get() = OneHandedSide.valueOf(
            prefs.getString("one_handed_side", OneHandedSide.RIGHT.name) ?: OneHandedSide.RIGHT.name
        )
        set(value) = prefs.edit().putString("one_handed_side", value.name).apply()

    var oneHandedScale: Float
        get() = prefs.getFloat("one_handed_scale", 0.78f)
        set(value) = prefs.edit().putFloat("one_handed_scale", value).apply()

    var floatingAlpha: Float
        get() = prefs.getFloat("floating_alpha", 0.95f)
        set(value) = prefs.edit().putFloat("floating_alpha", value).apply()

    var floatingScale: Float
        get() = prefs.getFloat("floating_scale", 0.85f)
        set(value) = prefs.edit().putFloat("floating_scale", value).apply()

    var hapticFeedback: Boolean
        get() = prefs.getBoolean("haptic_feedback", true)
        set(value) = prefs.edit().putBoolean("haptic_feedback", value).apply()

    var audioFeedback: Boolean
        get() = prefs.getBoolean("audio_feedback", false)
        set(value) = prefs.edit().putBoolean("audio_feedback", value).apply()

    var neonGlowEffect: Boolean
        get() = prefs.getBoolean("neon_glow", true)
        set(value) = prefs.edit().putBoolean("neon_glow", value).apply()

    var keyHeightDp: Int
        get() = prefs.getInt("key_height_dp", 50)
        set(value) = prefs.edit().putInt("key_height_dp", value).apply()

    var harakatShortcutRow: Boolean
        get() = prefs.getBoolean("harakat_shortcut_row", true)
        set(value) = prefs.edit().putBoolean("harakat_shortcut_row", value).apply()
}
