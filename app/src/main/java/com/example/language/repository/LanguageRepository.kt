package com.example.language.repository

import com.example.language.cache.LanguageCache
import com.example.language.model.LanguageInfo
import com.example.language.model.LanguagePackStatus
import com.example.language.model.LayoutFamily
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LanguageRepository(private val cache: LanguageCache) {

    companion object {
        val masterCatalog: List<LanguageInfo> = listOf(
            LanguageInfo("ar", "العربية (الأساسية)", "العربية", "🇸🇦", 0.0, isBuiltIn = true, layoutFamily = LayoutFamily.ARABIC, isRtl = true),
            LanguageInfo("ar-sa", "العربية (السعودية)", "العربية (السعودية)", "🇸🇦", 0.0, isBuiltIn = true, layoutFamily = LayoutFamily.ARABIC, isRtl = true),
            LanguageInfo("ar-eg", "العربية (مصر)", "العربية (مصر)", "🇪🇬", 0.0, isBuiltIn = true, layoutFamily = LayoutFamily.ARABIC, isRtl = true),
            LanguageInfo("ar-ae", "العربية (الإمارات)", "العربية (الإمارات)", "🇦🇪", 0.0, isBuiltIn = true, layoutFamily = LayoutFamily.ARABIC, isRtl = true),
            LanguageInfo("ar-iq", "العربية (العراق)", "العربية (العراق)", "🇮🇶", 0.0, isBuiltIn = true, layoutFamily = LayoutFamily.ARABIC, isRtl = true),
            LanguageInfo("ar-sy", "العربية (سوريا)", "العربية (سوريا)", "🇸🇾", 0.0, isBuiltIn = true, layoutFamily = LayoutFamily.ARABIC, isRtl = true),
            LanguageInfo("ar-dz", "العربية (الجزائر)", "العربية (الجزائر)", "🇩🇿", 0.0, isBuiltIn = true, layoutFamily = LayoutFamily.ARABIC, isRtl = true),
            LanguageInfo("ar-ma", "العربية (المغرب)", "العربية (المغرب)", "🇲🇦", 0.0, isBuiltIn = true, layoutFamily = LayoutFamily.ARABIC, isRtl = true),
            LanguageInfo("en", "الإنجليزية (US)", "English", "🇺🇸", 0.0, isBuiltIn = true, layoutFamily = LayoutFamily.QWERTY),
            LanguageInfo("en-gb", "الإنجليزية (UK)", "English (UK)", "🇬🇧", 0.0, isBuiltIn = true, layoutFamily = LayoutFamily.QWERTY),
            LanguageInfo("fr", "الفرنسية", "Français", "🇫🇷", 1.5, layoutFamily = LayoutFamily.AZERTY),
            LanguageInfo("es", "الإسبانية", "Español", "🇪🇸", 1.6, layoutFamily = LayoutFamily.QWERTY),
            LanguageInfo("de", "الألمانية", "Deutsch", "🇩🇪", 1.7, layoutFamily = LayoutFamily.QWERTZ),
            LanguageInfo("tr", "التركية", "Türkçe", "🇹🇷", 1.2, layoutFamily = LayoutFamily.QWERTY),
            LanguageInfo("ru", "الروسية", "Русский", "🇷🇺", 2.1, layoutFamily = LayoutFamily.CYRILLIC),
            LanguageInfo("it", "الإيطالية", "Italiano", "🇮🇹", 1.3, layoutFamily = LayoutFamily.QWERTY),
            LanguageInfo("pt", "البرتغالية", "Português", "🇧🇷", 1.4, layoutFamily = LayoutFamily.QWERTY),
            LanguageInfo("ur", "الأوردو", "اردو", "🇵🇰", 1.9, layoutFamily = LayoutFamily.URDU, isRtl = true),
            LanguageInfo("fa", "الفارسية", "فارسی", "🇮🇷", 1.7, layoutFamily = LayoutFamily.PERSIAN, isRtl = true),
            LanguageInfo("id", "الإندونيسية", "Bahasa Indonesia", "🇮🇩", 1.2, layoutFamily = LayoutFamily.QWERTY),
            LanguageInfo("zh", "الصينية", "中文", "🇨🇳", 3.5, layoutFamily = LayoutFamily.CHINESE_PINYIN),
            LanguageInfo("ja", "اليابانية", "日本語", "🇯🇵", 3.2, layoutFamily = LayoutFamily.JAPANESE_KANA),
            LanguageInfo("ko", "الكورية", "한국어", "🇰🇷", 2.8, layoutFamily = LayoutFamily.KOREAN_HANGUL),
            LanguageInfo("hi", "الهندية", "हिन्दी", "🇮🇳", 2.0, layoutFamily = LayoutFamily.DEVANAGARI)
        )
    }

    fun getAllLanguages(): Flow<List<LanguageInfo>> {
        return cache.installedLanguages.map { installedSet ->
            masterCatalog.map { lang ->
                val isInstalled = lang.isBuiltIn || lang.id.startsWith("ar") || lang.id.startsWith("en") || installedSet.contains(lang.id)
                val installedVer = cache.getInstalledVersion(lang.id)
                val status = when {
                    isInstalled && lang.version > installedVer && installedVer > 0 -> LanguagePackStatus.UPDATE_AVAILABLE
                    isInstalled -> LanguagePackStatus.INSTALLED
                    else -> LanguagePackStatus.NOT_INSTALLED
                }
                lang.copy(status = status)
            }
        }
    }

    fun getLanguageById(id: String): LanguageInfo? {
        val lang = masterCatalog.find { it.id == id } ?: return null
        val isInstalled = lang.isBuiltIn || lang.id.startsWith("ar") || lang.id.startsWith("en") || cache.isInstalled(id)
        val status = if (isInstalled) LanguagePackStatus.INSTALLED else LanguagePackStatus.NOT_INSTALLED
        return lang.copy(status = status)
    }

    fun getInstalledLanguagesSync(): List<LanguageInfo> {
        val installedSet = cache.installedLanguages.value
        return masterCatalog.filter { it.isBuiltIn || it.id.startsWith("ar") || it.id.startsWith("en") || installedSet.contains(it.id) }
    }

    fun getEnabledLanguages(enabledIds: Set<String>): List<LanguageInfo> {
        val set = if (enabledIds.isEmpty()) setOf("ar", "en") else enabledIds
        return masterCatalog.filter { set.contains(it.id) || it.id == "ar" }
    }
}
