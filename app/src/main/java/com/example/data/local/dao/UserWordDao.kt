package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.UserWordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserWordDao {
    @Query("SELECT * FROM user_words ORDER BY frequency DESC, word ASC")
    fun getAllWords(): Flow<List<UserWordEntity>>

    @Query("SELECT word FROM user_words WHERE word LIKE :prefix || '%' ORDER BY frequency DESC LIMIT 5")
    suspend fun getMatchingWords(prefix: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: UserWordEntity): Long

    @Query("UPDATE user_words SET frequency = frequency + 1 WHERE word = :word")
    suspend fun incrementFrequency(word: String)

    @Query("SELECT * FROM user_words WHERE word = :word LIMIT 1")
    suspend fun findWord(word: String): UserWordEntity?

    @Delete
    suspend fun deleteWord(word: UserWordEntity)

    @Query("DELETE FROM user_words WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM user_words")
    suspend fun clearAll()
}
