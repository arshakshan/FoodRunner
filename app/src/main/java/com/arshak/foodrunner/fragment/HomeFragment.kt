package com.arshak.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.arshak.foodrunner.adapter.HomeRecyclerAdapter
import com.arshak.foodrunner.R
import com.arshak.foodrunner.databases.RestaurantDatabase
import com.arshak.foodrunner.databases.RestaurantEntity
import com.arshak.foodrunner.model.Restaurant
import com.arshak.foodrunner.util.ConnectionManager
import com.arshak.foodrunner.util.Messenger
import com.google.android.material.navigation.NavigationView
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class HomeFragment : Fragment() {

    lateinit var messenger : Messenger

    lateinit var recyclerView: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerRestaurant: RecyclerView

    lateinit var progressLayout: RelativeLayout

    lateinit var progressBar: ProgressBar

    lateinit var recyclerAdapter: HomeRecyclerAdapter

    val menuList = arrayListOf<Restaurant>()

    lateinit var restaurant: Restaurant


    var ratingComparator = Comparator<Restaurant> { Restaurant1, Restaurant2 ->
        Restaurant1.restaurantRatings.compareTo(Restaurant2.restaurantRatings, true)
    }

    var priceComparator = Comparator<Restaurant> { Restaurant1, Restaurant2 ->
        Restaurant1.restaurantCost.toString().compareTo(Restaurant2.restaurantCost.toString(), true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerRestaurant = view.findViewById(R.id.recyclerRestaurant)

        recyclerAdapter = HomeRecyclerAdapter(
            activity as Context,
            menuList
        )

        layoutManager = LinearLayoutManager(activity)

        recyclerRestaurant.adapter = recyclerAdapter
        recyclerRestaurant.layoutManager = layoutManager

        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        messenger = activity as Messenger
        progressLayout.visibility = View.VISIBLE

        restaurant = Restaurant(
            restaurantId = 12,
            restaurantCost = 12,
            restaurantImage = "",
            restaurantName = "Js",
            restaurantRatings = "5"
        )

        var restoId = restaurant.restaurantId
        var restoName = restaurant.restaurantName

        messenger.passData(restoId,restoName)


        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    try {

                        progressLayout.visibility = View.GONE

                        val data = it.getJSONObject("data")

                        val success = data.getBoolean("success")

                        if (success) {

                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {

                                val restaurantJsonObject = resArray.getJSONObject(i)
                                val restaurantObject = Restaurant(

                                    restaurantJsonObject.getString("id").toInt(),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one").toInt(),
                                    restaurantJsonObject.getString("image_url")

                                )

                                menuList.add(restaurantObject)


                                recyclerRestaurant.adapter = recyclerAdapter
                                recyclerRestaurant.layoutManager = layoutManager

                                val restaurantEntity = RestaurantEntity(
                                    restaurantJsonObject.getString("id").toInt(),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one").toString(),
                                    restaurantJsonObject.getString("image_url")
                                )


                                val bundle = Bundle()
                                val restName: String = restoName.toString()
                                val restId: Int = restoId

                                bundle.putInt("id", restId)
                                bundle.putString("name", restName)
                                // set Fragmentclass Arguments

                                val fragobj = MenuFragment()
                                fragobj.setArguments(bundle)


                            }

                        } else {

                            Toast.makeText(
                                activity as Context,
                                "Some Error Has Occurred (3)!!!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }


                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some Error has Occurred (4) !!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {

                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error $it Occurred (5)!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }) {

                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "40d294a3a614fa"
                        return headers
                    }
                }

            queue.add(jsonObjectRequest)
        } else {

            //Internet is not available

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val Intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(Intent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }

            dialog.create()
            dialog.show()

        }




        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_sort) {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Sort By-")
            val options = arrayOf("Cost(Low-High)", "Cost(High-Low)", "Rating")

            dialog.setSingleChoiceItems(options, -1) { dialog, which ->
                when (which) {
                    0 -> {
                        Collections.sort(menuList, priceComparator)
                    }
                    1 -> {
                        Collections.sort(menuList, priceComparator)
                        menuList.reverse()
                    }
                    2 -> {
                        Collections.sort(menuList, ratingComparator)
                        menuList.reverse()
                    }
                }
            }

            dialog.setPositiveButton("Ok") { text, listner ->
                recyclerAdapter.notifyDataSetChanged()
            }

            dialog.create()
            dialog.show()

        }

        return super.onOptionsItemSelected(item)
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        /*
        Mode 1 -> Check DB if the book is favourite or not
        Mode 2 -> Save the book into DB as favourite
        Mode 3 -> Remove the favourite book
        * */

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {

                1 -> {

                    // Check DB if the book is favourite or not
                    val book: RestaurantEntity? = db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return book != null

                }

                2 -> {

                    // Save the book into DB as favourite
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true

                }

                3 -> {

                    // Remove the favourite book
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true

                }
            }
            return false
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.main, menu)
    }




}