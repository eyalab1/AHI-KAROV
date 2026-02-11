package com.first.ahikarov.data.reposetories

import androidx.lifecycle.LiveData
import com.first.ahikarov.data.local_db.ItemDao
import com.first.ahikarov.data.models.Item
import javax.inject.Inject

class MyCenterRepository @Inject constructor(
    private val itemDao: ItemDao
) {

    val allItems: LiveData<List<Item>> = itemDao.getAllItems()

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