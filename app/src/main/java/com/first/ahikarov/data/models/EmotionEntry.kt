package com.first.ahikarov.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emotion_entries")
data class EmotionEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val content: String
)