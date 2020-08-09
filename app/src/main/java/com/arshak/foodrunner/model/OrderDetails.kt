package com.arshak.foodrunner.model

import com.arshak.foodrunner.model.FoodItem
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONArray
import java.util.ArrayList

data class OrderDetails (
    val order_id: Int,
    val restaurant_name: String,
    val order_date:String,
    val foodItem: JSONArray
)