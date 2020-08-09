package com.arshak.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.arshak.foodrunner.R
import com.arshak.foodrunner.activity.RestMenuActivity
import com.arshak.foodrunner.databases.RestaurantDatabase
import com.arshak.foodrunner.databases.RestaurantEntity
import com.arshak.foodrunner.fragment.HomeFragment
import com.arshak.foodrunner.model.Restaurant
import com.squareup.picasso.Picasso


class HomeRecyclerAdapter(val contextMenu: Context, val itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)

        return HomeViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

        val resObject = itemList[position]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.imgRestaurant.clipToOutline = true
        }

        holder.txtRestaurantName.text = resObject.restaurantName
        holder.txtRatings.text = resObject.restaurantRatings
        val costForTwo = "${resObject.restaurantCost.toString()}/person"
        holder.txtCost.text = costForTwo
        Picasso.get().load(resObject.restaurantImage).error(R.drawable.default_restaurant)
            .into(holder.imgRestaurant)


        val listOfFavourites = GetAllFavAsyncTask(contextMenu).execute().get()

        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(resObject.restaurantId.toString())) {
            holder.imgFavorite.setImageResource(R.drawable.ic_favorites)
        } else {
            holder.imgFavorite.setImageResource(R.drawable.ic_favo)
        }

        holder.imgFavorite.setOnClickListener {
            val restaurantEntity = RestaurantEntity(
                resObject.restaurantId,
                resObject.restaurantName,
                resObject.restaurantRatings,
                resObject.restaurantCost.toString(),
                resObject.restaurantImage
            )

            if (!HomeFragment.DBAsyncTask(contextMenu, restaurantEntity, 1).execute().get()) {
                val async =
                    HomeFragment.DBAsyncTask(contextMenu, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.imgFavorite.setImageResource(R.drawable.ic_favorites)
                }
            } else {
                val async = HomeFragment.DBAsyncTask(contextMenu, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    holder.imgFavorite.setImageResource(R.drawable.ic_favo)
                }
            }
        }


        val restaurant = itemList[position]
        holder.llContent.setOnClickListener {

            val intent = Intent(contextMenu, RestMenuActivity::class.java)
            intent.putExtra("id", restaurant.restaurantId)
            intent.putExtra("name", restaurant.restaurantName.toString())
            contextMenu.startActivity(intent)
            Toast.makeText(
                contextMenu,
                "Clicked on ${holder.txtRestaurantName.text}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtCost: TextView = view.findViewById(R.id.txtCost)
        val txtRatings: TextView = view.findViewById(R.id.txtRatings)
        val imgRestaurant: ImageView = view.findViewById(R.id.imgRestaurant)
        val imgRupee: ImageView = view.findViewById(R.id.imgRupee)
        val imgFavorite: ImageView = view.findViewById(R.id.imgFavorite)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)

    }

    class GetAllFavAsyncTask(context: Context) :
        AsyncTask<Void, Void, List<String>>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {

            val list = db.restaurantDao().getAllRestaurants()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.id.toString())
            }
            return listOfIds
        }
    }

}

