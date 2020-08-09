package com.arshak.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arshak.foodrunner.R
import com.arshak.foodrunner.databases.OrderEntity
import com.arshak.foodrunner.databases.RestaurantEntity
import com.arshak.foodrunner.model.FoodItem
import com.arshak.foodrunner.model.OrderDetails
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.internal.Intrinsics

class OrderHistoryAdapter(val contextMenu: Context, val itemList: List<OrderDetails>) :
    RecyclerView.Adapter<OrderHistoryAdapter.HistoryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderHistoryAdapter.HistoryViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_history, parent, false)

        return OrderHistoryAdapter.HistoryViewHolder(view)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryAdapter.HistoryViewHolder, position: Int) {

        val order = itemList[position]
        holder.txtResName.text = order.restaurant_name
        holder.txtOrderDate.text = formatDate(order.order_date)
        setUpRecycler(holder.recyclerResHistoryItems, order)
    }

    fun setUpRecycler(recyclerView: RecyclerView, orderDetails: OrderDetails) {

        val arrayList = ArrayList<FoodItem>()

        val length = orderDetails.foodItem.length()

        for (i in 0 until length) {

            val menuObject = orderDetails.foodItem.getJSONObject(i)
            val foodItem = FoodItem(
                menuObject.getString("food_item_id"),
                menuObject.getString("name"),
                menuObject.getString("cost").toInt(),
                "null"
            )
            arrayList.add(foodItem)
        }

        val cartItemAdapter = CartItemAdapter(contextMenu, arrayList)
        recyclerView.layoutManager = LinearLayoutManager(contextMenu)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = cartItemAdapter
    }

    private fun formatDate(str: String): String {
        val parse =
            SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH).parse(str)
        if (parse != null) {
            return SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(parse)
        }
        throw TypeCastException("null cannot be cast to non-null type java.util.Date")
    }

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtResName: TextView = view.findViewById(R.id.txtResHistoryResName)
        val txtOrderDate: TextView = view.findViewById(R.id.txtDate)
        val recyclerResHistoryItems: RecyclerView = view.findViewById(R.id.recyclerResHistoryItems)

    }

}