package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.pref.KeyboardPreferences
import com.example.language.LanguageManager

class KeyboardProApp : Application() {
    @Volatile
    private var _database: AppDatabase? = null
    val database: AppDatabase
        get() = _database ?: synchronized(this) {
            _database ?: AppDatabase.getDatabase(this).also { _database = it }
        }

    @Volatile
    private var _preferences: KeyboardPreferences? = null
    val preferences: KeyboardPreferences
        get() = _preferences ?: synchronized(this) {
            _preferences ?: KeyboardPreferences(this).also { _preferences = it }
        }

    @Volatile
    private var _languageManager: LanguageManager? = null
    val languageManager: LanguageManager
        get() = _languageManager ?: synchronized(this) {
            _languageManager ?: LanguageManager(this, preferences).also { _languageManager = it }
        }

    override fun onCreate() {
        super.onCreate()
        instance = this
        _database = AppDatabase.getDatabase(this)
        _preferences = KeyboardPreferences(this)
        _languageManager = LanguageManager(this, preferences)
    }

    companion object {
        @Volatile
        lateinit var instance: KeyboardProApp
            private set
    }
}
