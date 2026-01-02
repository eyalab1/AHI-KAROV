package com.first.ahikarov

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EmotionJournalViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).emotionDao()
    val entries: LiveData<List<EmotionEntry>> = dao.getAllEntries()

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

    fun removeEntry(entry: EmotionEntry) {
        viewModelScope.launch {
            dao.deleteEntry(entry)
        }
    }

}
