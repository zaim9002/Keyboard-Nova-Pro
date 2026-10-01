package com.example.data.db

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SmartClipboardManager(private val context: Context) {

    private val db = NovaDatabase.getInstance(context)
    private val dao = db.clipboardDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        // Clean up expired sensitive clips on init
        scope.launch {
            dao.clearExpiredSensitive(System.currentTimeMillis())
        }
    }

    fun getAllClips(): Flow<List<ClipboardClip>> = dao.getAllClips()

    fun searchClips(query: String): Flow<List<ClipboardClip>> = dao.searchClips(query)

    suspend fun addClip(text: String, isPinned: Boolean = false): Long {
        if (text.isBlank()) return -1L

        val isSensitive = detectSensitive(text)
        val category = detectCategory(text, isSensitive)
        val expiresAt = if (isSensitive) {
            System.currentTimeMillis() + 15 * 60 * 1000L // Auto-clear after 15 mins
        } else null

        val clip = ClipboardClip(
            text = text.trim(),
            timestamp = System.currentTimeMillis(),
            isPinned = isPinned,
            category = category,
            isSensitive = isSensitive,
            expiresAt = expiresAt
        )
        return dao.insertClip(clip)
    }

    suspend fun togglePin(id: Long, currentPinned: Boolean) {
        dao.setPinned(id, !currentPinned)
    }

    suspend fun deleteClip(id: Long) {
        dao.deleteClipById(id)
    }

    suspend fun clearUnpinned() {
        dao.clearAllUnpinned()
    }

    suspend fun purgeExpired() {
        dao.clearExpiredSensitive(System.currentTimeMillis())
    }

    private fun detectSensitive(text: String): Boolean {
        val trimmed = text.trim()
        // 4-8 digit OTP code
        if (trimmed.matches(Regex("^\\b\\d{4,8}\\b$"))) return true
        // Credit card pattern (13-19 digits, possibly with spaces or hyphens)
        if (trimmed.matches(Regex("^(?:\\d[ -]*?){13,19}$"))) return true
        // Keywords hinting at secrets or passwords
        val lower = trimmed.lowercase()
        if (lower.startsWith("otp:") || lower.startsWith("code:") || lower.contains("password=") || lower.contains("token=")) {
            return true
        }
        return false
    }

    private fun detectCategory(text: String, isSensitive: Boolean): String {
        if (isSensitive) return ClipboardClip.CATEGORY_SENSITIVE
        val trimmed = text.trim()
        val urlPattern = Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", Pattern.CASE_INSENSITIVE)
        if (urlPattern.matcher(trimmed).matches() || trimmed.startsWith("www.")) {
            return ClipboardClip.CATEGORY_LINK
        }
        if (trimmed.contains("{") && trimmed.contains("}") ||
            trimmed.contains("fun ") ||
            trimmed.contains("class ") ||
            trimmed.contains("SELECT ") ||
            trimmed.contains("val ") ||
            trimmed.contains("def ") ||
            trimmed.contains("const ")
        ) {
            return ClipboardClip.CATEGORY_CODE
        }
        if (trimmed.lines().size > 2) {
            return ClipboardClip.CATEGORY_NOTE
        }
        return ClipboardClip.CATEGORY_TEXT
    }
}
