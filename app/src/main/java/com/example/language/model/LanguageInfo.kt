package com.example.language.model

enum class LanguagePackStatus {
    INSTALLED,
    NOT_INSTALLED,
    DOWNLOADING,
    UPDATE_AVAILABLE
}

enum class LayoutFamily {
    ARABIC,
    QWERTY,
    AZERTY,
    QWERTZ,
    CYRILLIC,
    GREEK,
    HEBREW,
    PERSIAN,
    URDU,
    DEVANAGARI,
    KOREAN_HANGUL,
    JAPANESE_KANA,
    CHINESE_PINYIN,
    THAI,
    BENGALI,
    TAMIL,
    VIETNAMESE
}

data class LanguageInfo(
    val id: String,
    val nameArabic: String,
    val nativeName: String,
    val flag: String,
    val sizeMb: Double,
    val isBuiltIn: Boolean = false,
    val status: LanguagePackStatus = LanguagePackStatus.NOT_INSTALLED,
    val downloadProgress: Float = 0f,
    val version: Int = 1,
    val layoutFamily: LayoutFamily = LayoutFamily.QWERTY,
    val isRtl: Boolean = false
)
