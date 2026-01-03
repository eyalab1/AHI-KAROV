package com.first.ahikarov.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.first.ahikarov.data.models.EmotionEntry

@Dao
interface EmotionDao {


    @Query("SELECT * FROM emotion_entries ORDER BY date DESC")
    fun getAllEntries(): LiveData<List<EmotionEntry>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEntry(entry: EmotionEntry)


    @Delete
    suspend fun deleteEntry(entry: EmotionEntry)


    @Query("SELECT COUNT(*) FROM emotion_entries WHERE date = :date")
    suspend fun countEntriesForDate(date: String): Int
}