package com.example.data.repository

import com.example.data.local.dao.ShortcutDao
import com.example.data.local.entity.ShortcutEntity
import kotlinx.coroutines.flow.Flow

class ShortcutRepository(private val shortcutDao: ShortcutDao) {
    val allShortcuts: Flow<List<ShortcutEntity>> = shortcutDao.getAllShortcuts()

    suspend fun findByTrigger(trigger: String): ShortcutEntity? = shortcutDao.findByTrigger(trigger)

    fun searchShortcuts(query: String): Flow<List<ShortcutEntity>> = shortcutDao.searchShortcuts(query)

    suspend fun insertOrUpdate(trigger: String, replacement: String) {
        val existing = shortcutDao.findByTrigger(trigger)
        if (existing != null) {
            shortcutDao.updateShortcut(existing.copy(replacement = replacement))
        } else {
            shortcutDao.insertShortcut(ShortcutEntity(trigger = trigger, replacement = replacement))
        }
    }

    suspend fun deleteById(id: Long) = shortcutDao.deleteById(id)
}
