package com.example.language

import android.content.Context
import com.example.data.pref.KeyboardPreferences
import com.example.language.cache.LanguageCache
import com.example.language.download.LanguageDownloadManager
import com.example.language.layout.KeyboardLayoutData
import com.example.language.layout.KeyboardLayoutManager
import com.example.language.model.LanguageInfo
import com.example.language.model.LayoutFamily
import com.example.language.repository.LanguageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LanguageManager(
    private val context: Context,
    val preferences: KeyboardPreferences
) {
    val cache = LanguageCache(context)
    val downloadManager = LanguageDownloadManager(context, cache)
    val repository = LanguageRepository(cache)
    val packManager = LanguagePackManager(repository, cache, downloadManager)

    private val _currentLanguage = MutableStateFlow(preferences.currentLanguage)
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun switchLanguage(targetLangId: String) {
        val langExists = repository.getLanguageById(targetLangId) != null
        val safeLang = if (langExists) targetLangId else "ar"
        preferences.currentLanguage = safeLang
        _currentLanguage.value = safeLang
    }

    fun cycleNextLanguage(): String {
        val enabled = preferences.enabledLanguages.toList()
        val effectiveList = if (enabled.isEmpty()) listOf("ar", "en") else enabled
        val currentIndex = effectiveList.indexOf(preferences.currentLanguage)
        val nextIndex = if (currentIndex >= 0 && currentIndex < effectiveList.size - 1) {
            currentIndex + 1
        } else {
            0
        }
        val nextLang = effectiveList[nextIndex]
        switchLanguage(nextLang)
        return nextLang
    }

    fun cyclePreviousLanguage(): String {
        val enabled = preferences.enabledLanguages.toList()
        val effectiveList = if (enabled.isEmpty()) listOf("ar", "en") else enabled
        val currentIndex = effectiveList.indexOf(preferences.currentLanguage)
        val prevIndex = if (currentIndex > 0) {
            currentIndex - 1
        } else {
            effectiveList.size - 1
        }
        val prevLang = effectiveList[prevIndex]
        switchLanguage(prevLang)
        return prevLang
    }

    fun getCurrentLayout(): KeyboardLayoutData {
        val langId = preferences.currentLanguage
        val langInfo = repository.getLanguageById(langId)
        val family = langInfo?.layoutFamily ?: if (langId.startsWith("ar")) LayoutFamily.ARABIC else LayoutFamily.QWERTY
        return KeyboardLayoutManager.getLayout(langId, family)
    }

    fun getLanguageInfo(langId: String): LanguageInfo? {
        return repository.getLanguageById(langId)
    }

    fun isArabic(): Boolean {
        return preferences.currentLanguage.startsWith("ar")
    }

    fun isRtl(): Boolean {
        return repository.getLanguageById(preferences.currentLanguage)?.isRtl ?: (preferences.currentLanguage.startsWith("ar"))
    }
}
