package com.first.ahikarov

data class Item(
    val id: String,          // מזהה ייחודי
    val title: String,       // כותרת (או תוכן המשפט)
    //val description: String, // תיאור משני
    val text: String?,       //טקסט המופיע
    val photo: String?,      // נתיב לתמונה
    val type: Int
)

// המחסן המרכזי
object ItemManager {

    val items: MutableList<Item> = mutableListOf()

    fun add(item: Item) {
        items.add(item)
    }

    fun remove(item: Item) {
        items.remove(item)
    }
}