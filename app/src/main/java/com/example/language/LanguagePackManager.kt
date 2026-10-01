package com.example.language

import com.example.language.cache.LanguageCache
import com.example.language.download.DownloadResult
import com.example.language.download.LanguageDownloadManager
import com.example.language.model.LanguageInfo
import com.example.language.repository.LanguageRepository

class LanguagePackManager(
    private val repository: LanguageRepository,
    private val cache: LanguageCache,
    private val downloadManager: LanguageDownloadManager
) {
    fun install(
        language: LanguageInfo,
        onProgress: ((Float) -> Unit)? = null,
        onComplete: ((DownloadResult) -> Unit)? = null
    ) {
        downloadManager.downloadLanguagePack(language, onProgress, onComplete)
    }

    fun delete(langId: String): Boolean {
        if (langId == "ar") {
            return false
        }
        cache.markUninstalled(langId)
        return true
    }

    fun isInstalled(langId: String): Boolean {
        return cache.isInstalled(langId)
    }

    fun cancel(langId: String) {
        downloadManager.cancelDownload(langId)
    }
}
