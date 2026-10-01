package com.example.data.repository

import com.example.data.local.dao.UserWordDao
import com.example.data.local.entity.UserWordEntity
import kotlinx.coroutines.flow.Flow

class UserWordRepository(private val userWordDao: UserWordDao) {
    val allWords: Flow<List<UserWordEntity>> = userWordDao.getAllWords()

    suspend fun getMatchingWords(prefix: String): List<String> = userWordDao.getMatchingWords(prefix)

    suspend fun learnWord(word: String) {
        if (word.isBlank() || word.length < 2) return
        val existing = userWordDao.findWord(word)
        if (existing != null) {
            userWordDao.incrementFrequency(word)
        } else {
            userWordDao.insertWord(UserWordEntity(word = word))
        }
    }

    suspend fun deleteById(id: Long) = userWordDao.deleteById(id)

    suspend fun clearAll() = userWordDao.clearAll()
}
