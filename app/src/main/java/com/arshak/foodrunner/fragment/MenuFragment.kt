package com.arshak.foodrunner.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arshak.foodrunner.R
import com.arshak.foodrunner.activity.CartActivity
import com.arshak.foodrunner.activity.MainActivity
import com.arshak.foodrunner.activity.RestMenuActivity
import com.arshak.foodrunner.adapter.MenuRecyclerAdapter
import com.arshak.foodrunner.databases.OrderEntity
import com.arshak.foodrunner.databases.RestaurantDatabase
import com.arshak.foodrunner.model.FoodItem
import com.arshak.foodrunner.util.ConnectionManager
import com.google.gson.Gson


class MenuFragment : Fragment() {

    private lateinit var recyclerMenu: RecyclerView
    private lateinit var restaurantMenuAdapter: MenuRecyclerAdapter
    private var menuList = arrayListOf<FoodItem>()
    private lateinit var rlLoading: RelativeLayout

    private var orderList = ArrayList<FoodItem>()
    lateinit var sharedPreferences: SharedPreferences

    object listener: MenuRecyclerAdapter.OnItemClickListener {
        override fun onAddItemClick(foodItem: FoodItem) {
            TODO("Not yet implemented")
        }

        override fun onRemoveItemClick(foodItem: FoodItem) {
            TODO("Not yet implemented")
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var goToCart: Button
        var resId: Int? = 0
        var resName: String? = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        sharedPreferences =
            activity?.getSharedPreferences("FoodApp", Context.MODE_PRIVATE) as SharedPreferences

        resId = (activity as RestMenuActivity).resId
        resName = (activity as RestMenuActivity).resName

        rlLoading = view?.findViewById(R.id.rlLoading) as RelativeLayout
        rlLoading.visibility = View.VISIBLE

        restaurantMenuAdapter = MenuRecyclerAdapter(
            activity as Context, menuList, listener)

        goToCart = view.findViewById(R.id.btnGoToCart)

        setHasOptionsMenu(true)
        goToCart.visibility = View.GONE
        goToCart.setOnClickListener {
            proceedToCart()
        }
        setUpRestaurantMenu(view)
        return view
    }


    private fun setUpRestaurantMenu(view: View) {

        recyclerMenu = view.findViewById(R.id.recyclerMenu)

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$resId"

        println("Response is $url")

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest = object :
                JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    rlLoading.visibility = View.GONE

                    println("Response is $it")
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        println("Response is $it")

                        if (success) {

                            val resArray = data.getJSONArray("data")

                            for (i in 0 until resArray.length()) {

                                val menuObject = resArray.getJSONObject(i)
                                val foodItem = FoodItem(
                                    menuObject.getString("id"),
                                    menuObject.getString("name"),
                                    menuObject.getString("cost_for_one").toInt(),
                                    menuObject.getString("restaurant_id")
                                )
                                menuList.add(foodItem)

                                restaurantMenuAdapter = MenuRecyclerAdapter(
                                    activity as Context,
                                    menuList,
                                    object : MenuRecyclerAdapter.OnItemClickListener {
                                        override fun onAddItemClick(foodItem: FoodItem) {
                                            orderList.add(foodItem)
                                            if (orderList.size > 0) {
                                                goToCart.visibility = View.VISIBLE
                                                MenuRecyclerAdapter.isCartEmpty = false
                                            }
                                        }

                                        override fun onRemoveItemClick(foodItem: FoodItem) {
                                            orderList.remove(foodItem)
                                            if (orderList.isEmpty()) {
                                                goToCart.visibility = View.GONE
                                                MenuRecyclerAdapter.isCartEmpty = true
                                            }
                                        }
                                    }
                                )
                                val mLayoutManager = LinearLayoutManager(activity)
                                recyclerMenu.layoutManager = mLayoutManager
                                recyclerMenu.itemAnimator = DefaultItemAnimator()
                                recyclerMenu.adapter = restaurantMenuAdapter
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(activity as Context, it.message, Toast.LENGTH_SHORT).show()
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
            Toast.makeText(activity as Context, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }


    private fun proceedToCart() {

        val gson = Gson()

        val foodItems = gson.toJson(orderList)

        val async = ItemsOfCart(activity as Context, resId.toString(), foodItems, 1).execute()
        val result = async.get()
        if (result) {
            val data = Bundle()
            data.putString("resId", resId.toString())
            data.putString("resName", resName)
            val intent = Intent(activity as Context, CartActivity::class.java)
            intent.putExtra("data", data)
            intent.putExtra("resId", resId)
            startActivity(intent)
        } else {
            Toast.makeText((activity as Context), "Some unexpected error (6)", Toast.LENGTH_SHORT)
                .show()
        }

    }


    class ItemsOfCart(
        context: Context,
        val restaurantId: String,
        val foodItems: String,
        val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "item-db").build()


        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.orderDao().insertOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }

                2 -> {
                    db.orderDao().deleteOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }
            }

            return false
        }

    }








}
