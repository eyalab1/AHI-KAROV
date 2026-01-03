package com.first.ahikarov.data.reposetories

import android.app.Application
import androidx.lifecycle.LiveData
import com.first.ahikarov.data.local_db.AppDatabase
import com.first.ahikarov.data.local_db.ItemDao
import com.first.ahikarov.data.models.Item

class MyCenterRepository(application: Application) {

    private var itemDao: ItemDao

    val allItems: LiveData<List<Item>>

    init {
        //  גישה למסד הנתונים הראשי
        val db = AppDatabase.Companion.getDatabase(application)
        itemDao = db.itemDao()
        allItems = itemDao.getAllItems()
    }

    // הוספת פריט חדש
    suspend fun addItem(item: Item) {
        itemDao.addItem(item)
    }

    // מחיקת פריט
    suspend fun deleteItem(item: Item) {
        itemDao.deleteItem(item)
    }

    // עדכון פריט קיים
    suspend fun updateItem(item: Item) {
        itemDao.updateItem(item)
    }
}