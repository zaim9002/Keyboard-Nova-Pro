package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.ShortcutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShortcutDao {
    @Query("SELECT * FROM shortcuts ORDER BY trigger ASC")
    fun getAllShortcuts(): Flow<List<ShortcutEntity>>

    @Query("SELECT * FROM shortcuts WHERE trigger = :trigger LIMIT 1")
    suspend fun findByTrigger(trigger: String): ShortcutEntity?

    @Query("SELECT * FROM shortcuts WHERE trigger LIKE '%' || :query || '%' OR replacement LIKE '%' || :query || '%'")
    fun searchShortcuts(query: String): Flow<List<ShortcutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortcut(shortcut: ShortcutEntity): Long

    @Update
    suspend fun updateShortcut(shortcut: ShortcutEntity)

    @Delete
    suspend fun deleteShortcut(shortcut: ShortcutEntity)

    @Query("DELETE FROM shortcuts WHERE id = :id")
    suspend fun deleteById(id: Long)
}
