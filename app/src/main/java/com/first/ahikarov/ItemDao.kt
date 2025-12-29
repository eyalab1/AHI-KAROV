package com.first.ahikarov

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItemDao {

    @Query("SELECT * FROM my_center_items ORDER BY item_title ASC")
    fun getAllItems(): LiveData<List<Item>>

    @Query("SELECT * FROM my_center_items WHERE item_id = :id")
    fun getItem(id: Int): Item

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItem(item: Item)

    @Update
    fun updateItem(item: Item)

    @Delete
    fun deleteItem(vararg items: Item)
}