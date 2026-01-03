package com.first.ahikarov.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.first.ahikarov.data.models.EmotionEntry
import com.first.ahikarov.data.models.Item

@Database(
    entities = [Item::class, EmotionEntry::class],
    version = 2,
    exportSchema = false
)abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun emotionDao(): EmotionDao


    // מסד נתונים אחד בכל האפליקציה (Singleton)
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}