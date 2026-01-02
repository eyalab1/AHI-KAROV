package com.first.ahikarov


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EmotionDao {

    @Query("SELECT * FROM emotion_entries ORDER BY date DESC")
    fun getAllEntries(): LiveData<List<EmotionEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addEntry(entry: EmotionEntry)

    @Delete
    fun deleteEntry(entry: EmotionEntry)

    @Query("SELECT COUNT(*) FROM emotion_entries WHERE date = :date")
    suspend fun countEntriesForDate(date: String): Int

}
