package com.first.ahikarov.ui.emotion_journal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.first.ahikarov.data.models.EmotionEntry
import com.first.ahikarov.data.reposetories.EmotionRepository
import kotlinx.coroutines.launch

class EmotionJournalViewModel(application: Application) : AndroidViewModel(application) {


    private val repository = EmotionRepository(application)


    val entries: LiveData<List<EmotionEntry>> = repository.allEntries

    fun addEntry(entry: EmotionEntry) {
        viewModelScope.launch {
            repository.addEntry(entry)
        }
    }

    fun hasEntryForDate(date: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val count = repository.countEntriesForDate(date)
            callback(count > 0)
        }
    }

    fun removeEntry(entry: EmotionEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }
}