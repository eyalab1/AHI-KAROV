package com.first.ahikarov.ui.emotion_journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.first.ahikarov.data.models.EmotionEntry
import com.first.ahikarov.data.reposetories.EmotionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmotionJournalViewModel @Inject constructor(
    private val repository: EmotionRepository
) : ViewModel() {

    val entries = repository.allEntries

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