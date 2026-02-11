package com.first.ahikarov.data.reposetories

import androidx.lifecycle.LiveData
import com.first.ahikarov.data.local_db.EmotionDao
import com.first.ahikarov.data.models.EmotionEntry
import javax.inject.Inject


class EmotionRepository @Inject constructor(
    private val emotionDao: EmotionDao
) {


    val allEntries: LiveData<List<EmotionEntry>> = emotionDao.getAllEntries()

    // הוספה
    suspend fun addEntry(entry: EmotionEntry) {
        emotionDao.addEntry(entry)
    }

    // מחיקה
    suspend fun deleteEntry(entry: EmotionEntry) {
        emotionDao.deleteEntry(entry)
    }

    // בדיקה לפי תאריך
    suspend fun countEntriesForDate(date: String): Int {
        return emotionDao.countEntriesForDate(date)
    }
}