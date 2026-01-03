package com.first.ahikarov

import android.app.Application
import androidx.lifecycle.LiveData

class MyCenterRepository(application: Application) {

    private var itemDao: ItemDao

    val allItems: LiveData<List<Item>>

    init {
        //  גישה למסד הנתונים הראשי
        val db = AppDatabase.getDatabase(application)
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