package com.first.ahikarov

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emotion_entries")
data class EmotionEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Room ייתן ערך אוטומטי
    val date: String,
    val content: String
)