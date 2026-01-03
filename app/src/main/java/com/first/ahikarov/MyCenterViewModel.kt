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

    // רשימת כל הפריטים (מתעדכנת אוטומטית מה-LiveData של ה-Room)
    val itemsLiveData: LiveData<List<Item>> = repository.allItems

    //  ניהול פריט נבחר (למעבר בין מסכים)
    private val _selectedItem = MutableLiveData<Item?>()

    val selectedItem: LiveData<Item?> get() = _selectedItem

    // מעדכן את הפריט שנבחר
    fun setItem(item: Item?) {
        _selectedItem.value = item
    }


    // הוספת פריט
    fun addItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addItem(item)
        }
    }

    // מחיקת פריט
    fun deleteItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(item)
        }
    }

    // עדכון פריט
    fun updateItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateItem(item)
        }
    }
}