package com.example.data.repository

import com.example.data.local.dao.ClipboardDao
import com.example.data.local.entity.ClipboardEntity
import kotlinx.coroutines.flow.Flow

class ClipboardRepository(private val clipboardDao: ClipboardDao) {
    val allClips: Flow<List<ClipboardEntity>> = clipboardDao.getAllClips()

    fun searchClips(query: String): Flow<List<ClipboardEntity>> = clipboardDao.searchClips(query)

    fun getClipsByFolder(folder: String): Flow<List<ClipboardEntity>> = clipboardDao.getClipsByFolder(folder)

    suspend fun insertOrUpdate(text: String, folder: String = "عام", isPinned: Boolean = false) {
        if (text.isBlank()) return
        val existing = clipboardDao.getClipByText(text)
        if (existing != null) {
            clipboardDao.updateClip(existing.copy(timestamp = System.currentTimeMillis()))
        } else {
            clipboardDao.insertClip(
                ClipboardEntity(
                    text = text,
                    isPinned = isPinned,
                    folder = folder,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun togglePin(id: Long, isPinned: Boolean) = clipboardDao.togglePin(id, isPinned)

    suspend fun deleteById(id: Long) = clipboardDao.deleteById(id)

    suspend fun clearUnpinned() = clipboardDao.clearUnpinned()

    suspend fun clearAll() = clipboardDao.clearAll()
}
