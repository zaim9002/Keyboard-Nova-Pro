package com.example.language.download

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.language.cache.LanguageCache
import com.example.language.model.LanguageInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.ByteArrayOutputStream

sealed class DownloadResult {
    data class Success(val langId: String) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
}

class LanguageDownloadManager(
    private val context: Context,
    private val cache: LanguageCache
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _downloadProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Float>> = _downloadProgress.asStateFlow()

    private val _activeDownloads = MutableStateFlow<Set<String>>(emptySet())
    val activeDownloads: StateFlow<Set<String>> = _activeDownloads.asStateFlow()

    private val activeJobs = mutableMapOf<String, Job>()

    fun isOnline(): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val network = cm?.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } catch (e: Throwable) {
            true
        }
    }

    fun downloadLanguagePack(
        language: LanguageInfo,
        onProgress: ((Float) -> Unit)? = null,
        onComplete: ((DownloadResult) -> Unit)? = null
    ) {
        val langId = language.id
        if (cache.isInstalled(langId) && language.status != com.example.language.model.LanguagePackStatus.UPDATE_AVAILABLE) {
            onComplete?.invoke(DownloadResult.Success(langId))
            return
        }

        if (activeJobs[langId]?.isActive == true) {
            return
        }

        if (!isOnline()) {
            onComplete?.invoke(DownloadResult.Error("لا يوجد اتصال بالإنترنت."))
            return
        }

        val job = scope.launch {
            try {
                _activeDownloads.value = _activeDownloads.value + langId
                updateProgress(langId, 0.05f)
                onProgress?.invoke(0.05f)

                val totalSteps = 20
                val byteBuffer = ByteArrayOutputStream()

                for (step in 1..totalSteps) {
                    delay(60)
                    val fraction = step.toFloat() / totalSteps.toFloat()
                    updateProgress(langId, fraction)
                    withContext(Dispatchers.Main) {
                        onProgress?.invoke(fraction)
                    }
                    val block = ByteArray(128) { (it % 255).toByte() }
                    byteBuffer.write(block)
                }

                val packBytes = byteBuffer.toByteArray()
                if (packBytes.isNotEmpty()) {
                    cache.savePackData(langId, packBytes)
                    cache.markInstalled(langId, language.version)
                    withContext(Dispatchers.Main) {
                        onComplete?.invoke(DownloadResult.Success(langId))
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onComplete?.invoke(DownloadResult.Error("فشل تحميل حزمة اللغة."))
                    }
                }
            } catch (e: CancellationException) {
                // Cancelled safely
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    onComplete?.invoke(DownloadResult.Error("خطأ في التحميل: ${e.message ?: "غير معروف"}"))
                }
            } finally {
                _activeDownloads.value = _activeDownloads.value - langId
                _downloadProgress.value = _downloadProgress.value - langId
                activeJobs.remove(langId)
            }
        }
        activeJobs[langId] = job
    }

    fun cancelDownload(langId: String) {
        activeJobs[langId]?.cancel()
        activeJobs.remove(langId)
        _activeDownloads.value = _activeDownloads.value - langId
        _downloadProgress.value = _downloadProgress.value - langId
    }

    private fun updateProgress(langId: String, progress: Float) {
        val current = _downloadProgress.value.toMutableMap()
        current[langId] = progress
        _downloadProgress.value = current
    }
}
