package com.first.ahikarov

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "my_center_items")
data class Item(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    val id: Int = 0,

    @ColumnInfo(name = "item_title")
    val title: String,

    @ColumnInfo(name = "item_text")
    val text: String?,

    @ColumnInfo(name = "item_image_url")
    val photo: String?,

    @ColumnInfo(name = "item_type")
    val type: Int
) : Parcelable