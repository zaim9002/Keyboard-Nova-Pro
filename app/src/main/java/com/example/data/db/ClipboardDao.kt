package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipboardDao {

    @Query("SELECT * FROM clipboard_clips ORDER BY isPinned DESC, timestamp DESC")
    fun getAllClips(): Flow<List<ClipboardClip>>

    @Query("SELECT * FROM clipboard_clips WHERE isPinned = 1 ORDER BY timestamp DESC")
    fun getPinnedClips(): Flow<List<ClipboardClip>>

    @Query("SELECT * FROM clipboard_clips WHERE text LIKE '%' || :query || '%' ORDER BY isPinned DESC, timestamp DESC")
    fun searchClips(query: String): Flow<List<ClipboardClip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClip(clip: ClipboardClip): Long

    @Delete
    suspend fun deleteClip(clip: ClipboardClip)

    @Query("DELETE FROM clipboard_clips WHERE id = :id")
    suspend fun deleteClipById(id: Long)

    @Query("UPDATE clipboard_clips SET isPinned = :isPinned WHERE id = :id")
    suspend fun setPinned(id: Long, isPinned: Boolean)

    @Query("DELETE FROM clipboard_clips WHERE isSensitive = 1 AND expiresAt IS NOT NULL AND expiresAt < :currentTime")
    suspend fun clearExpiredSensitive(currentTime: Long): Int

    @Query("DELETE FROM clipboard_clips WHERE isPinned = 0")
    suspend fun clearAllUnpinned()
}
