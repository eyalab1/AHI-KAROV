package com.first.ahikarov

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyCenterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MyCenterRepository = MyCenterRepository(application)
    val itemsLiveData: LiveData<List<Item>> = repository.allItems

    // --- התיקון: הוספת סימני שאלה (?) כדי לאפשר null ---

    // 1. משתנה פרטי (יכול להיות null)
    private val _chosenItem = MutableLiveData<Item?>()

    // 2. משתנה ציבורי (יכול להיות null)
    val chosenItem: LiveData<Item?> get() = _chosenItem

    // 3. הפונקציה מקבלת Item? (עם סימן שאלה) כדי שנוכל לשלוח null
    fun setItem(item: Item?) {
        _chosenItem.value = item
    }
    // ----------------------------------------------------

    fun addItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) { repository.addItem(item) }
    }

    fun removeItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteItem(item) }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateItem(item) }
    }
}