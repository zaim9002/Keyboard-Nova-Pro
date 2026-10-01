package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clipboard_items")
data class ClipboardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val isPinned: Boolean = false,
    val folder: String = "عام",
    val timestamp: Long = System.currentTimeMillis()
)
