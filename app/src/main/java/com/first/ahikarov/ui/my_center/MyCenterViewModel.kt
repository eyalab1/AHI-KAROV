package com.first.ahikarov.ui.my_center

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.first.ahikarov.data.models.Item
import com.first.ahikarov.data.reposetories.MyCenterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCenterViewModel @Inject constructor(
    private val repository: MyCenterRepository
) : ViewModel() {
    // רשימת כל הפריטים (מגיע מה-Repo שקיבלנו)
    val itemsLiveData: LiveData<List<Item>> = repository.allItems

    // ניהול פריט נבחר
    private val _selectedItem = MutableLiveData<Item?>()
    val selectedItem: LiveData<Item?> get() = _selectedItem

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