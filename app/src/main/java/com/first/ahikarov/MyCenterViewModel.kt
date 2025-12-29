package com.first.ahikarov

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData // <-- הוספנו את זה
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyCenterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MyCenterRepository = MyCenterRepository(application)

    // רשימת כל הפריטים (מחובר ל-DB)
    val itemsLiveData: LiveData<List<Item>> = repository.allItems

    // --- החלק החדש שהוספנו (Shared ViewModel) ---
    // 1. משתנה פרטי לשמירת הפריט שנבחר (אנחנו כותבים אליו)
    private val _chosenItem = MutableLiveData<Item>()

    // 2. משתנה ציבורי להאזנה (מסך הפרטים יקרא ממנו)
    val chosenItem: LiveData<Item> get() = _chosenItem

    // 3. פונקציה שהמסך הראשי יקרא לה כדי "לנעוץ" את הפריט
    fun setItem(item: Item) {
        _chosenItem.value = item
    }

    // הוספה
    fun addItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addItem(item)
        }
    }

    // מחיקה
    fun removeItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(item)
        }
    }

    // עדכון
    fun updateItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateItem(item)
        }
    }
}