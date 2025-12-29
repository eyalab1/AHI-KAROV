package com.first.ahikarov

import android.app.Application
import androidx.lifecycle.LiveData

class MyCenterRepository(application: Application) {

    private var itemDao: ItemDao

    // משתנה שמחזיק את כל הרשימה ומעדכן אותה אוטומטית (LiveData)
    val allItems: LiveData<List<Item>>

    init {
        // 1. מקבלים גישה למסד הנתונים הראשי (הבניין)
        val db = AppDatabase.getDatabase(application)
        // 2. מבקשים את המנהל האישי שלך (Dao)
        itemDao = db.itemDao()
        // 3. ממלאים את הרשימה
        allItems = itemDao.getAllItems()
    }

    // הוספת פריט חדש
    fun addItem(item: Item) {
        itemDao.addItem(item)
    }

    // מחיקת פריט
    fun deleteItem(item: Item) {
        itemDao.deleteItem(item)
    }

    // עדכון פריט קיים (למשל שינוי שם)
    fun updateItem(item: Item) {
        itemDao.updateItem(item)
    }
}