package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing an item in the local Smart Clipboard.
 * Stored 100% locally on-device for total privacy.
 */
@Entity(tableName = "clipboard_clips")
data class ClipboardClip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val category: String = CATEGORY_TEXT,
    val isSensitive: Boolean = false,
    val expiresAt: Long? = null
) {
    companion object {
        const val CATEGORY_TEXT = "TEXT"
        const val CATEGORY_LINK = "LINK"
        const val CATEGORY_CODE = "CODE"
        const val CATEGORY_NOTE = "NOTE"
        const val CATEGORY_SENSITIVE = "SENSITIVE"
    }
}
