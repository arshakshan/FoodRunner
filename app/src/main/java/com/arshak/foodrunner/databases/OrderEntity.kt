package com.arshak.foodrunner.databases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.arshak.foodrunner.model.FoodItem

@Entity(tableName = "orders")
data class OrderEntity(
    @ColumnInfo(name = "resId") val resId: String,
    @PrimaryKey val foodItems: String
)