package com.arshak.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arshak.foodrunner.R
import com.arshak.foodrunner.model.FoodItem
import com.arshak.foodrunner.model.Restaurant

class CartItemAdapter(val contextMenu: Context, val itemList: ArrayList<FoodItem>): RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartItemAdapter.CartViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_items_row,parent,false)

        return CartItemAdapter.CartViewHolder(
            view
        )

    }

    override fun getItemCount(): Int {

        return itemList.size

    }

    override fun onBindViewHolder(holder: CartItemAdapter.CartViewHolder, position: Int) {
        val items = itemList.get(position)
        holder.txtItemName.text = items.itemName
        holder.txtItemCost.text = items.itemCost.toString()


    }


    class CartViewHolder(view: View): RecyclerView.ViewHolder(view){

        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtItemCost: TextView = view.findViewById(R.id.txtItemCost)

    }



}