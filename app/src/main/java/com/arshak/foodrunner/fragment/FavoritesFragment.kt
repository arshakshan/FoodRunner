package com.arshak.foodrunner.fragment

import android.content.Context
import android.opengl.Visibility
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.arshak.foodrunner.R
import com.arshak.foodrunner.adapter.FavoritesRecyclerAdapter
import com.arshak.foodrunner.databases.RestaurantDatabase
import com.arshak.foodrunner.databases.RestaurantEntity


class FavoritesFragment : Fragment() {


    lateinit var recyclerFavorite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManger: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavoritesRecyclerAdapter
    lateinit var rlNoFav: RelativeLayout

    var dbResList = listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_favorites, container, false)


        recyclerFavorite = view.findViewById(R.id.recyclerFavRestaurant)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)

        layoutManger = LinearLayoutManager(activity as Context)
        rlNoFav = view.findViewById(R.id.rlNoFav)

        dbResList = RetrieveFavorites(context as Context).execute().get()

        if(dbResList != emptyList<RestaurantEntity>()){
            rlNoFav.visibility = View.GONE
        }

        if (activity != null) {
            progressLayout.visibility = View.GONE
            recyclerAdapter = FavoritesRecyclerAdapter(activity as Context, dbResList)
            recyclerFavorite.adapter = recyclerAdapter
            recyclerFavorite.layoutManager = layoutManger
        }

        return view
    }


    class RetrieveFavorites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {

            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

            return db.restaurantDao().getAllRestaurants()

        }


    }


}