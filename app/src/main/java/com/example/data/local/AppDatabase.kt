package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ClipboardDao
import com.example.data.local.dao.ShortcutDao
import com.example.data.local.dao.UserWordDao
import com.example.data.local.entity.ClipboardEntity
import com.example.data.local.entity.ShortcutEntity
import com.example.data.local.entity.UserWordEntity

@Database(
    entities = [
        ClipboardEntity::class,
        ShortcutEntity::class,
        UserWordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clipboardDao(): ClipboardDao
    abstract fun shortcutDao(): ShortcutDao
    abstract fun userWordDao(): UserWordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "keyboard_pro_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            try {
                                populateDefaults(db)
                            } catch (e: Exception) {
                                android.util.Log.e("AppDatabase", "Error seeding defaults", e)
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun populateDefaults(db: SupportSQLiteDatabase) {
            val now = System.currentTimeMillis()
            db.execSQL("INSERT OR IGNORE INTO shortcuts (trigger, replacement, timestamp) VALUES ('سلام', 'السلام عليكم ورحمة الله وبركاته', $now)")
            db.execSQL("INSERT OR IGNORE INTO shortcuts (trigger, replacement, timestamp) VALUES ('شكرا', 'شكراً جزيلاً لك وبارك الله فيك', $now)")
            db.execSQL("INSERT OR IGNORE INTO shortcuts (trigger, replacement, timestamp) VALUES ('brb', 'Be right back!', $now)")
            db.execSQL("INSERT OR IGNORE INTO shortcuts (trigger, replacement, timestamp) VALUES ('omw', 'On my way!', $now)")
            db.execSQL("INSERT OR IGNORE INTO shortcuts (trigger, replacement, timestamp) VALUES ('صلى', 'صلى الله عليه وسلم', $now)")
            db.execSQL("INSERT OR IGNORE INTO shortcuts (trigger, replacement, timestamp) VALUES ('جزاك', 'جزاك الله خيراً', $now)")
            db.execSQL("INSERT OR IGNORE INTO clipboard_items (text, timestamp, isPinned, folder) VALUES ('مرحباً بك في Nova Keyboard - كيبورد محمد v.1!', $now, 1, 'عام')")
            db.execSQL("INSERT OR IGNORE INTO clipboard_items (text, timestamp, isPinned, folder) VALUES ('سبحان الله وبحمده سبحان الله العظيم', $now, 1, 'عام')")
        }
    }
}
