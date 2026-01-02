package com.first.ahikarov

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// פה אנחנו רושמים את הטבלה שלך (Item) כדי שהבוס יכיר אותה
@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // כאן אנחנו חושפים את המנהל שיצרנו קודם (DAO)
    abstract fun itemDao(): ItemDao

    // הקוד הזה דואג שיהיה רק מסד נתונים אחד בכל האפליקציה (Singleton)
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database" // זה השם של הקובץ שיישמר בתוך הטלפון
                )
                    .allowMainThreadQueries() // מאפשר עבודה פשוטה בלי להיתקע (מותר למטלה)
                    .fallbackToDestructiveMigration() // אם משנים משהו בטבלה, זה מוחק ויוצר מחדש (מונע קריסות בפיתוח)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}