package com.example.language.cache

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class LanguageCache(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _installedLanguages = MutableStateFlow<Set<String>>(loadInstalledLanguages())
    val installedLanguages: StateFlow<Set<String>> = _installedLanguages.asStateFlow()

    private fun loadInstalledLanguages(): Set<String> {
        val saved = prefs.getStringSet(KEY_INSTALLED_LANGS, null)
        return if (saved.isNullOrEmpty()) {
            val defaults = setOf("ar", "en")
            prefs.edit().putStringSet(KEY_INSTALLED_LANGS, defaults).apply()
            defaults
        } else {
            saved + "ar"
        }
    }

    fun isInstalled(langId: String): Boolean {
        if (langId == "ar") return true
        return _installedLanguages.value.contains(langId)
    }

    fun markInstalled(langId: String, version: Int = 1) {
        val current = _installedLanguages.value.toMutableSet()
        current.add(langId)
        prefs.edit()
            .putStringSet(KEY_INSTALLED_LANGS, current)
            .putInt(KEY_LANG_VERSION_PREFIX + langId, version)
            .apply()
        _installedLanguages.value = current
    }

    fun markUninstalled(langId: String) {
        if (langId == "ar") return
        val current = _installedLanguages.value.toMutableSet()
        current.remove(langId)
        prefs.edit()
            .putStringSet(KEY_INSTALLED_LANGS, current)
            .remove(KEY_LANG_VERSION_PREFIX + langId)
            .apply()
        _installedLanguages.value = current
        try {
            val packFile = File(context.filesDir, "lang_pack_$langId.dat")
            if (packFile.exists()) packFile.delete()
        } catch (e: Throwable) {}
    }

    fun getInstalledVersion(langId: String): Int {
        if (langId == "ar" || langId == "en") return 1
        return prefs.getInt(KEY_LANG_VERSION_PREFIX + langId, 0)
    }

    fun savePackData(langId: String, data: ByteArray) {
        try {
            val file = File(context.filesDir, "lang_pack_$langId.dat")
            file.writeBytes(data)
        } catch (e: Throwable) {}
    }

    fun hasPackData(langId: String): Boolean {
        if (langId == "ar" || langId == "en") return true
        val file = File(context.filesDir, "lang_pack_$langId.dat")
        return file.exists() && file.length() > 0
    }

    companion object {
        private const val PREFS_NAME = "keyboard_language_cache"
        private const val KEY_INSTALLED_LANGS = "installed_languages_set"
        private const val KEY_LANG_VERSION_PREFIX = "lang_ver_"
    }
}
