package com.arshak.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arshak.foodrunner.R
import com.arshak.foodrunner.activity.RestMenuActivity
import com.arshak.foodrunner.databases.RestaurantEntity
import com.squareup.picasso.Picasso

class FavoritesRecyclerAdapter(val contextMenu: Context, val itemList: List<RestaurantEntity>) :
    RecyclerView.Adapter<FavoritesRecyclerAdapter.FavoritesViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritesRecyclerAdapter.FavoritesViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_fav_single_row, parent, false)

        return FavoritesViewHolder(view)

    }

    companion object {
        var isFav = true
    }

    override fun getItemCount(): Int {

        return itemList.size

    }

    override fun onBindViewHolder(
        holder: FavoritesRecyclerAdapter.FavoritesViewHolder,
        position: Int
    ) {

        val restaurant = itemList[position]
        holder.txtRestaurantName.text = restaurant.name
        holder.txtCost.text = restaurant.costForTwo.toString()
        holder.txtRatings.text = restaurant.rating

        Picasso.get().load(restaurant.imageUrl).error(R.drawable.default_restaurant)
            .into(holder.imgRestaurant);

        holder.llContent.setOnClickListener {
            val intent = Intent(contextMenu, RestMenuActivity::class.java)
            intent.putExtra("id", restaurant.id)
            intent.putExtra("name", restaurant.name.toString())
            contextMenu.startActivity(intent)
        }
    }

    class FavoritesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtCost: TextView = view.findViewById(R.id.txtCost)
        val txtRatings: TextView = view.findViewById(R.id.txtRatings)
        val imgRestaurant: ImageView = view.findViewById(R.id.imgRestaurant)
        val imgRupee: ImageView = view.findViewById(R.id.imgRupee)
        val imgFavorite: ImageView = view.findViewById(R.id.imgFavorite)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val txtPerPerson: TextView = view.findViewById(R.id.txtPerPerson)
    }


}

