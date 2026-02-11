package com.first.ahikarov.di

import android.content.Context
import androidx.room.Room
import com.first.ahikarov.data.InspirationApiService
// --- התיקונים הקריטיים ב-Imports: ---
import com.first.ahikarov.data.local_db.AppDatabase // <--- הנה המיקום הנכון!
import com.first.ahikarov.data.local_db.EmotionDao
import com.first.ahikarov.data.local_db.ItemDao
// ------------------------------------
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //  ציטוטים
    @Provides
    @Singleton
    @Named("QuotesApi")
    fun provideQuotesApi(): InspirationApiService {
        return Retrofit.Builder()
            .baseUrl("https://zenquotes.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InspirationApiService::class.java)
    }

    //  מוזיקה (Deezer)
    @Provides
    @Singleton
    @Named("MusicApi")
    fun provideMusicApi(): InspirationApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.deezer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InspirationApiService::class.java)
    }

    //  תמונות (Picsum)
    @Provides
    @Singleton
    @Named("ImagesApi")
    fun provideImagesApi(): InspirationApiService {
        return Retrofit.Builder()
            .baseUrl("https://picsum.photos/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InspirationApiService::class.java)
    }

    //  יצירת מסד הנתונים (Database)
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    //  גישה ל-DAO הראשי (ItemDao)
    @Provides
    fun provideDao(database: AppDatabase): ItemDao {
        return database.itemDao()
    }

    //  גישה ל-DAO של הרגשות (EmotionDao)
    @Provides
    fun provideEmotionDao(database: AppDatabase): EmotionDao {
        return database.emotionDao()
    }
}