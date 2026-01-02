package com.first.ahikarov

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EmotionJournalViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData שמגיע ישירות מ-Room
    private val dao = AppDatabase.getDatabase(application).emotionDao()
    val entries: LiveData<List<EmotionEntry>> = dao.getAllEntries()

    // הוספת רשומה חדשה
    fun addEntry(entry: EmotionEntry) {
        viewModelScope.launch {
            dao.addEntry(entry)
        }
    }
    fun hasEntryForDate(date: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val exists = dao.countEntriesForDate(date) > 0
            callback(exists)
        }
    }

    // מחיקת רשומה
    fun removeEntry(entry: EmotionEntry) {
        viewModelScope.launch {
            dao.deleteEntry(entry)
        }
    }

    // בדיקה אם כבר קיימת רשומה עבור תאריך מסוים
    fun hasEntryForDate(date: String): Boolean {
        return entries.value?.any { it.date == date } == true
    }
}
