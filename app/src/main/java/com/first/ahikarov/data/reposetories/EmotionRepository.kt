package com.first.ahikarov.data.reposetories

import android.app.Application
import androidx.lifecycle.LiveData
import com.first.ahikarov.data.local_db.AppDatabase
import com.first.ahikarov.data.local_db.EmotionDao
import com.first.ahikarov.data.models.EmotionEntry

class EmotionRepository(application: Application) {

    private val emotionDao: EmotionDao
    val allEntries: LiveData<List<EmotionEntry>>

    init {
        // קבלת הדאטה-בייס וה-DAO בצורה מסודרת
        val db = AppDatabase.getDatabase(application)
        emotionDao = db.emotionDao()
        allEntries = emotionDao.getAllEntries()
    }

    // הוספה
    suspend fun addEntry(entry: EmotionEntry) {
        emotionDao.addEntry(entry)
    }

    // מחיקה
    suspend fun deleteEntry(entry: EmotionEntry) {
        emotionDao.deleteEntry(entry)
    }

    // בדיקה אם קיים (לפי תאריך)
    suspend fun countEntriesForDate(date: String): Int {
        return emotionDao.countEntriesForDate(date)
    }
}